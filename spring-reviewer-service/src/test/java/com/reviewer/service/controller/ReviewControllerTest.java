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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddReview_Success() {
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

        ResponseEntity<?> response = reviewController.addReview(bookId, review);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedReview, response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_BookNotFound() {
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        when(bookService.getBookById(bookId)).thenReturn(null);

        ResponseEntity<?> response = reviewController.addReview(bookId, review);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ID 1 not found", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidBookId() {
        String bookId = null;
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        ResponseEntity<?> response = reviewController.addReview(bookId, review);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Book ID cannot be null or empty", response.getBody());
        verify(bookService, never()).getBookById(anyString());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidBookIdEmpty() {
        String bookId = "";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        ResponseEntity<?> response = reviewController.addReview(bookId, review);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Book ID cannot be null or empty", response.getBody());
        verify(bookService, never()).getBookById(anyString());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidReviewNull() {
        String bookId = "1";
        Review review = null;

        ResponseEntity<?> response = reviewController.addReview(bookId, review);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Review cannot be null", response.getBody());
        verify(bookService, never()).getBookById(anyString());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidReviewMissingReviewerName() {
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName(null); // Invalid
        review.setRating(5);
        review.setComment("Great book!");

        ResponseEntity<?> response = reviewController.addReview(bookId, review);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Reviewer name cannot be null or empty", response.getBody());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testAddReview_InvalidReviewInvalidRating() {
        Review review = new Review();
        review.setReviewerName("John Doe");
        assertThrows(IllegalArgumentException.class, () -> review.setRating(0));
    }

    @Test
    void testAddReview_InvalidReviewMissingComment() {
        String bookId = "1";
        Review review = new Review();
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment(null); // Invalid

        ResponseEntity<?> response = reviewController.addReview(bookId, review);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Review comment cannot be null or empty", response.getBody());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void testGetReviewsByBookId_Success() {
        String bookId = "1";
        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("1", "Jane Smith", 4, "Good read");
        List<Review> reviews = Arrays.asList(review1, review2);

        when(bookService.getBookById(bookId)).thenReturn(book);
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(reviews);
        when(reviewService.getAverageRatingForBook(bookId)).thenReturn(4.5);

        ResponseEntity<?> response = reviewController.getReviewsByBookId(bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

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
        String bookId = "1";
        when(bookService.getBookById(bookId)).thenReturn(null);

        ResponseEntity<?> response = reviewController.getReviewsByBookId(bookId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ID 1 not found", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).getReviewsByBookId(anyString());
    }

    @Test
    void testGetAverageRatingForBook_Success() {
        String bookId = "1";
        Book book = new Book(1L, "Test Book", "Test Author", 2023);
        double averageRating = 4.2;

        when(bookService.getBookById(bookId)).thenReturn(book);
        when(reviewService.getAverageRatingForBook(bookId)).thenReturn(averageRating);

        ResponseEntity<?> response = reviewController.getAverageRatingForBook(bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(bookId, responseBody.get("bookId"));
        assertEquals(averageRating, responseBody.get("averageRating"));

        verify(bookService).getBookById(bookId);
        verify(reviewService).getAverageRatingForBook(bookId);
    }

    @Test
    void testGetAverageRatingForBook_BookNotFound() {
        String bookId = "1";
        when(bookService.getBookById(bookId)).thenReturn(null);

        ResponseEntity<?> response = reviewController.getAverageRatingForBook(bookId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ID 1 not found", response.getBody());
        verify(bookService).getBookById(bookId);
        verify(reviewService, never()).getAverageRatingForBook(anyString());
    }

    @Test
    void testGetAllReviews_Success() {
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("2", "Jane Smith", 4, "Good read");
        List<Review> reviews = Arrays.asList(review1, review2);

        when(reviewService.getAllReviews()).thenReturn(reviews);

        ResponseEntity<List<Review>> response = reviewController.getAllReviews();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviews, response.getBody());
        verify(reviewService).getAllReviews();
    }

    @Test
    void testGetAllReviews_Error() {
        when(reviewService.getAllReviews()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<Review>> response = reviewController.getAllReviews();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(reviewService).getAllReviews();
    }

    @Test
    void testDeleteReview_Success() {
        String reviewId = "1";
        doNothing().when(reviewService).deleteReview(reviewId);

        ResponseEntity<?> response = reviewController.deleteReview(reviewId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewService).deleteReview(reviewId);
    }

    @Test
    void testDeleteReview_Error() {
        String reviewId = "1";
        doThrow(new RuntimeException("Database error")).when(reviewService).deleteReview(reviewId);

        ResponseEntity<?> response = reviewController.deleteReview(reviewId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(reviewService).deleteReview(reviewId);
    }
}