package org.sandbox.reviewer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.sandbox.reviewer.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("query", "query Book { book(id: \"%s\") { id title author year } }"
                .formatted(bookId));
        String graphQlQuery;
        try {
            graphQlQuery = objectMapper.writeValueAsString(queryMap);
        } catch (JsonProcessingException e) {
            logger.error("Failed to cook GraphQL request json query with {}", e.getMessage());
            throw new RuntimeException(e);
        }
        String url = javaEEAppUrl + "/javaee-graphql-project/graphql"; // http://docker-container/context-project/graphql
        logger.info("Executing GraphQL query\n'{}'\n url '{}'", graphQlQuery, url);
        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(graphQlQuery, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            if (statusCode == 200) {
                logger.info("JavaEE GraphQL response code is 200");
                return processGraphQLResponse(response);
            } else {
                handleErrorResponse(statusCode, url);
            }
        } catch (IOException | ParseException e) {
            logger.error("Error fetching book from JavaEE app: {}", bookId, e);
        }
        return null;
    }

    private Book processGraphQLResponse(HttpEntityContainer response) throws IOException, ParseException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String responseBody = EntityUtils.toString(entity);
            logger.info("Successfully fetched book from JavaEE app via GraphQL.");
            // Parse the GraphQL response
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("errors")) {
                logger.error("GraphQL errors: {}", root.get("errors").toString());
                return null;
            }
            JsonNode bookNode = root.path("data").path("book");
            return objectMapper.treeToValue(bookNode, Book.class);
        }
        return null;
    }

    private void handleErrorResponse(int statusCode, String url) {
        if (statusCode == 404) {
            logger.info("Book not found in JavaEE app: {}", url);
        } else {
            logger.warn("Failed to fetch book from JavaEE app. Status: {}, URL: {}", statusCode, url);
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

    @PreDestroy
    public void cleanup() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
