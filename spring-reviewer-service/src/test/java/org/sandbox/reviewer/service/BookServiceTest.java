package org.sandbox.reviewer.service;

import org.sandbox.reviewer.model.Book;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBookById_Cached() throws Exception {
        Book cachedBook = new Book(1L, "Test Book", "Test Author", 2023);
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("book:1")).thenReturn(cachedBook);

        Book result = bookService.getBookById("1");

        assertEquals(cachedBook, result);
        verify(redisTemplate.opsForValue()).get("book:1");
        verify(httpClient, never()).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_NotCached_FetchSuccess() throws Exception {
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

        // Create an InputStream from the JSON string
        String jsonResponse = "{\"id\":1,\"title\":\"Test Book\",\"author\":\"Test Author\",\"year\":2023}";
        InputStream inputStream = IOUtils.toInputStream(jsonResponse, StandardCharsets.UTF_8);
        when(httpEntity.getContent()).thenReturn(inputStream);
        when(httpEntity.getContentLength()).thenReturn((long) jsonResponse.length());

        // Mock cache operations
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(valueOps).set(anyString(), any(Book.class), anyLong(), any());

        Book result = bookService.getBookById("1");

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());

        verify(redisTemplate.opsForValue()).get("book:1");
        verify(redisTemplate.opsForValue()).set("book:1", result, 1L, TimeUnit.HOURS);
        verify(httpClient).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_NotCached_FetchNotFound() throws Exception {
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("book:1")).thenReturn(null); // Not in cache

        // Mock HTTP response for 404
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(404);

        Book result = bookService.getBookById("1");

        assertNull(result);
        verify(redisTemplate.opsForValue()).get("book:1");
        verify(httpClient).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_NotCached_FetchError() throws Exception {
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("book:1")).thenReturn(null); // Not in cache

        // Mock HTTP client to throw exception
        when(httpClient.execute(any(HttpGet.class))).thenThrow(new IOException("Connection failed"));

        Book result = bookService.getBookById("1");

        assertNull(result);
        verify(redisTemplate.opsForValue()).get("book:1");
        verify(httpClient).execute(any(HttpGet.class));
    }

    @Test
    void testGetBookById_InvalidNullBookId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBookById(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetBookById_InvalidEmptyBookId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBookById(""));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testInvalidateBookCache() {
        bookService.invalidateBookCache("1");

        verify(redisTemplate).delete("book:1");
    }

    @Test
    void testInvalidateBookCache_InvalidNullBookId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.invalidateBookCache(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testInvalidateBookCache_InvalidEmptyBookId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.invalidateBookCache(""));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }
}