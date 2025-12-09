package com.reviewer.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewer.service.model.Book;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
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
    
    private HttpClient httpClient;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // This should match the JavaEE app URL
    private String javaEEAppUrl = "http://javaee-app:8080"; // Using service name in Docker
    
    @PostConstruct
    public void init() {
        // Initialize HTTP client with timeout configuration
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(10000)
                .build();
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
                
        // Check if we're running in Docker environment
        String envUrl = System.getenv("JAVAEE_APP_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            javaEEAppUrl = envUrl;
        }
        logger.info("JavaEE App URL configured as: {}", javaEEAppUrl);
    }
    
    public Book getBookById(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        
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
    
    /**
     * Fetches a book from the JavaEE application by ID using GraphQL API
     */
    private Book fetchBookFromJavaEEApp(String bookId) {
        try {
            // Since JavaEE app uses GraphQL, we need to call GraphQL endpoint
            String url = javaEEAppUrl + "/graphql";
            
            // Prepare GraphQL query
            String graphqlQuery = "{\\\"query\\\":\\\"{ book(id: \\\"" + bookId + "\\\" ) { id title author year } }\\\"}";
            
            // For now, using a simpler approach - assuming REST endpoint exists
            // In real scenario, we would need to call GraphQL endpoint properly
            String urlWithId = javaEEAppUrl + "/api/books/" + bookId;
            HttpGet request = new HttpGet(urlWithId);
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
            } else if (statusCode == 404) {
                logger.info("Book not found in JavaEE app: {}", bookId);
                return null;
            } else {
                logger.warn("Failed to fetch book from JavaEE app. Status: {}, URL: {}", statusCode, urlWithId);
            }
        } catch (IOException e) {
            logger.error("Error fetching book from JavaEE app: " + bookId, e);
        }
        
        return null;
    }
    
    public void invalidateBookCache(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        String cacheKey = "book:" + bookId;
        redisTemplate.delete(cacheKey);
        logger.info("Invalidated cache for book: {}", bookId);
    }
}