package com.reviewer.service.controller;

import com.reviewer.service.model.Book;
import com.reviewer.service.model.Review;
import com.reviewer.service.service.BookService;
import com.reviewer.service.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewController controller;

    @BeforeEach
    void setUp() {
        controller = new ReviewController();
        controller.reviewService = reviewService;
        controller.bookService = bookService;
    }

    @Test
    void testAddReview_Success() {
        // Arrange
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");
        
        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        Review savedReview = new Review();
        savedReview.setId("review1");
        savedReview.setBookId(bookId);
        savedReview.setReviewerName("John Doe");
        savedReview.setRating(5);
        savedReview.setComment("Great book!");
        
        when(bookService.getBookById(bookId)).thenReturn(book);
        when(reviewService.saveReview(any(Review.class))).thenReturn(savedReview);

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedReview, response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_BookNotFound() {
        // Arrange
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");
        
        when(bookService.getBookById(bookId)).thenReturn(null);

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ID 1 not found", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidBookId() {
        // Arrange
        String bookId = null;
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Book ID cannot be null or empty", response.getBody());
        verify(bookService, never()).getBookById(anyString());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidBookIdEmpty() {
        // Arrange
        String bookId = "";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Book ID cannot be null or empty", response.getBody());
        verify(bookService, never()).getBookById(anyString());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidReviewNull() {
        // Arrange
        String bookId = "1";
        Review review = null;

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Review cannot be null", response.getBody());
        verify(bookService, never()).getBookById(anyString());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidReviewMissingReviewerName() {
        // Arrange
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName(null); // Invalid
        review.setRating(5);
        review.setComment("Great book!");

        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        when(bookService.getBookById(bookId)).thenReturn(book);

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Reviewer name cannot be null or empty", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidReviewInvalidRating() {
        // Arrange
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(0); // Invalid rating
        review.setComment("Great book!");

        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        when(bookService.getBookById(bookId)).thenReturn(book);

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Rating must be between 1 and 5", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidReviewMissingComment() {
        // Arrange
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment(null); // Invalid

        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        when(bookService.getBookById(bookId)).thenReturn(book);

        // Act
        ResponseEntity<?> response = controller.addReview(bookId, review);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Review comment cannot be null or empty", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testGetReviewsByBookId_Success() {
        // Arrange
        String bookId = "1";
        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("1", "Jane Smith", 4, "Good read");
        List<Review> reviews = Arrays.asList(review1, review2);
        
        when(bookService.getBookById(bookId)).thenReturn(book);
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(reviews);
        when(reviewService.getAverageRatingForBook(bookId)).thenReturn(4.5);

        // Act
        ResponseEntity<?> response = controller.getReviewsByBookId(bookId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(bookId, responseBody.get("bookId"));
        assertEquals(4.5, responseBody.get("averageRating"));
        assertEquals(2, ((List<?>) responseBody.get("reviews")).size());
        
        verify(bookService).getBookById(bookId);
        verify(reviewService).getReviewsByBookId(bookId);
        verify(reviewService).getAverageRatingForBook(bookId);
    }

    @Test
    void testGetReviewsByBookId_BookNotFound() {
        // Arrange
        String bookId = "1";
        when(bookService.getBookById(bookId)).thenReturn(null);

        // Act
        ResponseEntity<?> response = controller.getReviewsByBookId(bookId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ID 1 not found", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).getReviewsByBookId(anyString());
    }

    @Test
    void testGetAverageRatingForBook_Success() {
        // Arrange
        String bookId = "1";
        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        double averageRating = 4.2;
        
        when(bookService.getBookById(bookId)).thenReturn(book);
        when(reviewService.getAverageRatingForBook(bookId)).thenReturn(averageRating);

        // Act
        ResponseEntity<?> response = controller.getAverageRatingForBook(bookId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(bookId, responseBody.get("bookId"));
        assertEquals(averageRating, responseBody.get("averageRating"));
        
        verify(bookService).getBookById(bookId);
        verify(reviewService).getAverageRatingForBook(bookId);
    }

    @Test
    void testGetAverageRatingForBook_BookNotFound() {
        // Arrange
        String bookId = "1";
        when(bookService.getBookById(bookId)).thenReturn(null);

        // Act
        ResponseEntity<?> response = controller.getAverageRatingForBook(bookId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ID 1 not found", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).getAverageRatingForBook(anyString());
    }

    @Test
    void testGetAllReviews_Success() {
        // Arrange
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("2", "Jane Smith", 4, "Good read");
        List<Review> reviews = Arrays.asList(review1, review2);
        
        when(reviewService.getAllReviews()).thenReturn(reviews);

        // Act
        ResponseEntity<List<Review>> response = controller.getAllReviews();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviews, response.getBody());
        verify(reviewService).getAllReviews();
    }

    @Test
    void testGetAllReviews_Error() {
        // Arrange
        when(reviewService.getAllReviews()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<List<Review>> response = controller.getAllReviews();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(reviewService).getAllReviews();
    }

    @Test
    void testDeleteReview_Success() {
        // Arrange
        String reviewId = "1";
        doNothing().when(reviewService).deleteReview(reviewId);

        // Act
        ResponseEntity<?> response = controller.deleteReview(reviewId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewService).deleteReview(reviewId);
    }

    @Test
    void testDeleteReview_Error() {
        // Arrange
        String reviewId = "1";
        doThrow(new RuntimeException("Database error")).when(reviewService).deleteReview(reviewId);

        // Act
        ResponseEntity<?> response = controller.deleteReview(reviewId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(reviewService).deleteReview(reviewId);
    }
}