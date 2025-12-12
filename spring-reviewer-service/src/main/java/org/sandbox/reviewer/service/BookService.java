package org.sandbox.reviewer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.sandbox.reviewer.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private static final String CACHE_PREFIX = "book:";

    private CloseableHttpClient httpClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String javaEEAppUrl = "http://javaee-app:8080"; // Using service name in Docker

    @PostConstruct
    public void init() {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(5000, TimeUnit.MILLISECONDS)
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();

        // Configuration for Docker environment URL
        String envUrl = System.getenv("JAVAEE_APP_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            javaEEAppUrl = envUrl;
        }
        logger.info("JavaEE App URL configured as: {}", javaEEAppUrl);
    }

    public Book getBookById(String bookId) {
        validateBookId(bookId);
        String cacheKey = CACHE_PREFIX + bookId;

        // Attempt to get book from cache
        Book cachedBook = (Book) redisTemplate.opsForValue().get(cacheKey);
        if (cachedBook != null) {
            logger.info("Book found in cache: {}", bookId);
            return cachedBook;
        }

        // Fetch from JavaEE app if not cached
        Book book = fetchBookFromJavaEEApp(bookId);
        if (book != null) {
            redisTemplate.opsForValue().set(cacheKey, book, 1, TimeUnit.HOURS);
            logger.info("Book fetched from JavaEE app and cached: {}", bookId);
        }

        return book;
    }

    private Book fetchBookFromJavaEEApp(String bookId) {
        String urlWithId = javaEEAppUrl + "/api/books/" + bookId;
        HttpGet request = new HttpGet(urlWithId);
        request.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            if (statusCode == 200) {
                return processResponse(response);
            } else {
                handleErrorResponse(statusCode, urlWithId);
            }
        } catch (IOException | ParseException e) {
            logger.error("Error fetching book from JavaEE app: {}", bookId, e);
        }
        return null;
    }

    private Book processResponse(HttpEntityContainer response) throws IOException, ParseException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String responseBody = EntityUtils.toString(entity);
            logger.info("Successfully fetched book from JavaEE app.");
            return objectMapper.readValue(responseBody, Book.class);
        }
        return null;
    }

    private void handleErrorResponse(int statusCode, String urlWithId) {
        if (statusCode == 404) {
            logger.info("Book not found in JavaEE app: {}", urlWithId);
        } else {
            logger.warn("Failed to fetch book from JavaEE app. Status: {}, URL: {}", statusCode, urlWithId);
        }
    }

    public void invalidateBookCache(String bookId) {
        validateBookId(bookId);
        String cacheKey = CACHE_PREFIX + bookId;
        redisTemplate.delete(cacheKey);
        logger.info("Invalidated cache for book: {}", bookId);
    }

    private void validateBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
    }
}
