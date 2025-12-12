package org.sandbox.reviewer.service;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sandbox.reviewer.model.Book;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private ValueOperations<String, Object> valueOps;

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