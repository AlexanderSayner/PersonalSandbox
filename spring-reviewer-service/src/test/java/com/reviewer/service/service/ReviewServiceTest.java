package com.reviewer.service.service;

import com.reviewer.service.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService();
        reviewService.mongoTemplate = mongoTemplate;
    }

    @Test
    void testSaveReview() {
        // Arrange
        Review review = new Review("1", "John Doe", 5, "Great book!");
        when(mongoTemplate.save(review)).thenReturn(review);

        // Act
        Review savedReview = reviewService.saveReview(review);

        // Assert
        assertEquals(review, savedReview);
        verify(mongoTemplate).save(review);
    }

    @Test
    void testSaveReview_InvalidNullReview() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(null));
        assertEquals("Review cannot be null", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidNullBookId() {
        // Arrange
        Review review = new Review();
        review.setBookId(null);
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidEmptyBookId() {
        // Arrange
        Review review = new Review();
        review.setBookId("");
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidNullReviewerName() {
        // Arrange
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName(null);
        review.setRating(5);
        review.setComment("Great book!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Reviewer name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidEmptyReviewerName() {
        // Arrange
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("");
        review.setRating(5);
        review.setComment("Great book!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Reviewer name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidRatingTooLow() {
        // Arrange
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("John Doe");
        review.setRating(0); // Invalid rating
        review.setComment("Great book!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Rating must be between 1 and 5", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidRatingTooHigh() {
        // Arrange
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("John Doe");
        review.setRating(6); // Invalid rating
        review.setComment("Great book!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Rating must be between 1 and 5", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidNullComment() {
        // Arrange
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Review comment cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidEmptyComment() {
        // Arrange
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.saveReview(review));
        assertEquals("Review comment cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetReviewsByBookId() {
        // Arrange
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("1", "Jane Smith", 4, "Good read");
        List<Review> expectedReviews = Arrays.asList(review1, review2);
        
        Query query = new Query();
        query.addCriteria(Criteria.where("bookId").is("1"));
        
        when(mongoTemplate.find(query, Review.class)).thenReturn(expectedReviews);

        // Act
        List<Review> actualReviews = reviewService.getReviewsByBookId("1");

        // Assert
        assertEquals(expectedReviews, actualReviews);
        verify(mongoTemplate).find(query, Review.class);
    }

    @Test
    void testGetReviewsByBookId_InvalidNullBookId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.getReviewsByBookId(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetReviewsByBookId_InvalidEmptyBookId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.getReviewsByBookId(""));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetAverageRatingForBook_WithReviews() {
        // Arrange
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("1", "Jane Smith", 3, "Average book");
        List<Review> reviews = Arrays.asList(review1, review2);
        
        Query query = new Query();
        query.addCriteria(Criteria.where("bookId").is("1"));
        
        when(mongoTemplate.find(query, Review.class)).thenReturn(reviews);

        // Act
        double averageRating = reviewService.getAverageRatingForBook("1");

        // Assert
        assertEquals(4.0, averageRating, 0.01); // Expected average: (5 + 3) / 2 = 4.0
        verify(mongoTemplate).find(query, Review.class);
    }

    @Test
    void testGetAverageRatingForBook_NoReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(); // Empty list
        
        Query query = new Query();
        query.addCriteria(Criteria.where("bookId").is("1"));
        
        when(mongoTemplate.find(query, Review.class)).thenReturn(reviews);

        // Act
        double averageRating = reviewService.getAverageRatingForBook("1");

        // Assert
        assertEquals(0.0, averageRating, 0.01);
        verify(mongoTemplate).find(query, Review.class);
    }

    @Test
    void testGetAverageRatingForBook_InvalidNullBookId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.getAverageRatingForBook(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetAllReviews() {
        // Arrange
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("2", "Jane Smith", 4, "Good read");
        List<Review> expectedReviews = Arrays.asList(review1, review2);
        
        when(mongoTemplate.findAll(Review.class)).thenReturn(expectedReviews);

        // Act
        List<Review> actualReviews = reviewService.getAllReviews();

        // Assert
        assertEquals(expectedReviews, actualReviews);
        verify(mongoTemplate).findAll(Review.class);
    }

    @Test
    void testDeleteReview() {
        // Arrange
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is("1"));
        
        doNothing().when(mongoTemplate).remove(query, Review.class);

        // Act
        reviewService.deleteReview("1");

        // Assert
        verify(mongoTemplate).remove(query, Review.class);
    }

    @Test
    void testDeleteReview_InvalidNullReviewId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.deleteReview(null));
        assertEquals("Review ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testDeleteReview_InvalidEmptyReviewId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> reviewService.deleteReview(""));
        assertEquals("Review ID cannot be null or empty", exception.getMessage());
    }
}