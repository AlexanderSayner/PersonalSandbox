package com.reviewer.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewer.service.model.Book;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HttpClient httpClient;

    private BookService bookService;

    @BeforeEach
    void setUp() throws Exception {
        bookService = new BookService();
        bookService.redisTemplate = redisTemplate;
        
        // Mock the HTTP client initialization
        bookService.init();
        // Override the httpClient field since it's initialized in init()
        bookService.getClass().getDeclaredField("httpClient").setAccessible(true);
        bookService.getClass().getDeclaredField("httpClient").set(bookService, httpClient);
    }

    @Test
    void testGetBookById_Cached() throws Exception {
        // Arrange
        Book cachedBook = new Book(1L, "Test Book", "Test Author", 2023);
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("book:1")).thenReturn(cachedBook);

        // Act
        Book result = bookService.getBookById("1");

        // Assert
        assertEquals(cachedBook, result);
        verify(redisTemplate.opsForValue()).get("book:1");
        verify(httpClient, never()).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_NotCached_FetchSuccess() throws Exception {
        // Arrange
        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("book:1")).thenReturn(null); // Not in cache
        
        // Mock HTTP response
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        HttpEntity httpEntity = mock(HttpEntity.class);
        
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(EntityUtils.toString(httpEntity)).thenReturn("{\"id\":1,\"title\":\"Test Book\",\"author\":\"Test Author\",\"year\":2023}");
        
        // Mock cache operations
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(valueOps).set(anyString(), any(Book.class), any(Long.class), any());

        // Act
        Book result = bookService.getBookById("1");

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        verify(redisTemplate.opsForValue()).get("book:1");
        verify(redisTemplate.opsForValue()).set("book:1", result, 1L, java.util.concurrent.TimeUnit.HOURS);
        verify(httpClient).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_NotCached_FetchNotFound() throws Exception {
        // Arrange
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("book:1")).thenReturn(null); // Not in cache
        
        // Mock HTTP response for 404
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(404);

        // Act
        Book result = bookService.getBookById("1");

        // Assert
        assertNull(result);
        verify(redisTemplate.opsForValue()).get("book:1");
        verify(httpClient).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_NotCached_FetchError() throws Exception {
        // Arrange
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("book:1")).thenReturn(null); // Not in cache
        
        // Mock HTTP client to throw exception
        when(httpClient.execute(any(HttpGet.class))).thenThrow(new IOException("Connection failed"));

        // Act
        Book result = bookService.getBookById("1");

        // Assert
        assertNull(result);
        verify(redisTemplate.opsForValue()).get("book:1");
        verify(httpClient).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_InvalidNullBookId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> bookService.getBookById(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetBookById_InvalidEmptyBookId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> bookService.getBookById(""));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testInvalidateBookCache() {
        // Arrange
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(redisTemplate).delete("book:1");

        // Act
        bookService.invalidateBookCache("1");

        // Assert
        verify(redisTemplate).delete("book:1");
    }

    @Test
    void testInvalidateBookCache_InvalidNullBookId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> bookService.invalidateBookCache(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testInvalidateBookCache_InvalidEmptyBookId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> bookService.invalidateBookCache(""));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }
}