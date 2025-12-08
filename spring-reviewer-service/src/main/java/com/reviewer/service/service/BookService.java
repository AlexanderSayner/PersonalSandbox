package com.reviewer.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewer.service.model.Book;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class BookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    @Autowired
    private HttpClient httpClient;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // This should match the JavaEE app URL
    private String javaEEAppUrl = "http://javaee-app:8080"; // Using service name in Docker
    
    @PostConstruct
    public void init() {
        // Check if we're running in Docker environment
        String envUrl = System.getenv("JAVAEE_APP_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            javaEEAppUrl = envUrl;
        }
        logger.info("JavaEE App URL configured as: {}", javaEEAppUrl);
    }
    
    public Book getBookById(String bookId) {
        String cacheKey = "book:" + bookId;
        
        // Try to get from cache first
        Object cachedBook = redisTemplate.opsForValue().get(cacheKey);
        if (cachedBook != null) {
            logger.info("Book found in cache: {}", bookId);
            return (Book) cachedBook;
        }
        
        // If not in cache, fetch from JavaEE app
        Book book = fetchBookFromJavaEEApp(bookId);
        if (book != null) {
            // Cache the book for 1 hour
            redisTemplate.opsForValue().set(cacheKey, book, 1, TimeUnit.HOURS);
            logger.info("Book fetched from JavaEE app and cached: {}", bookId);
        }
        
        return book;
    }
    
    private Book fetchBookFromJavaEEApp(String bookId) {
        try {
            String url = javaEEAppUrl + "/api/books/" + bookId;
            HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "application/json");
            
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    logger.info("Successfully fetched book from JavaEE app: {}", bookId);
                    return objectMapper.readValue(responseBody, Book.class);
                }
            } else {
                logger.warn("Failed to fetch book from JavaEE app. Status: {}, URL: {}", statusCode, url);
            }
        } catch (IOException e) {
            logger.error("Error fetching book from JavaEE app: " + bookId, e);
        }
        
        return null;
    }
    
    public void invalidateBookCache(String bookId) {
        String cacheKey = "book:" + bookId;
        redisTemplate.delete(cacheKey);
        logger.info("Invalidated cache for book: {}", bookId);
    }
}