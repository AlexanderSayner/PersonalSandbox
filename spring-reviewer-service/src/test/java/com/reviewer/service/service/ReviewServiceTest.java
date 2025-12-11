package com.reviewer.service.service;

import com.reviewer.service.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveReview() {
        Review review = new Review("1", "John Doe", 5, "Great book!");
        when(mongoTemplate.save(review)).thenReturn(review);

        Review savedReview = reviewService.saveReview(review);

        assertEquals(review, savedReview);
        verify(mongoTemplate).save(review);
    }

    @Test
    void testSaveReview_InvalidNullReview() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(null));
        assertEquals("Review cannot be null", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidNullBookId() {
        Review review = new Review();
        review.setBookId(null);
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidEmptyBookId() {
        Review review = new Review();
        review.setBookId("");
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("Great book!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidNullReviewerName() {
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName(null);
        review.setRating(5);
        review.setComment("Great book!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review));
        assertEquals("Reviewer name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidEmptyReviewerName() {
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("");
        review.setRating(5);
        review.setComment("Great book!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review));
        assertEquals("Reviewer name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidNullComment() {
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review));
        assertEquals("Review comment cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveReview_InvalidEmptyComment() {
        Review review = new Review();
        review.setBookId("1");
        review.setReviewerName("John Doe");
        review.setRating(5);
        review.setComment("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review));
        assertEquals("Review comment cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetReviewsByBookId() {
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("1", "Jane Smith", 4, "Good read");
        List<Review> expectedReviews = Arrays.asList(review1, review2);

        Query query = new Query();
        query.addCriteria(Criteria.where("bookId").is("1"));

        when(mongoTemplate.find(query, Review.class)).thenReturn(expectedReviews);

        List<Review> actualReviews = reviewService.getReviewsByBookId("1");

        assertEquals(expectedReviews, actualReviews);
        verify(mongoTemplate).find(query, Review.class);
    }

    @Test
    void testGetReviewsByBookId_InvalidNullBookId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.getReviewsByBookId(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetReviewsByBookId_InvalidEmptyBookId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.getReviewsByBookId(""));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetAverageRatingForBook_WithReviews() {
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("1", "Jane Smith", 3, "Average book");
        List<Review> reviews = Arrays.asList(review1, review2);

        Query query = new Query();
        query.addCriteria(Criteria.where("bookId").is("1"));

        when(mongoTemplate.find(query, Review.class)).thenReturn(reviews);

        double averageRating = reviewService.getAverageRatingForBook("1");

        assertEquals(4.0, averageRating, 0.01); // Expected average: (5 + 3) / 2 = 4.0
        verify(mongoTemplate).find(query, Review.class);
    }

    @Test
    void testGetAverageRatingForBook_NoReviews() {
        List<Review> reviews = List.of();

        Query query = new Query();
        query.addCriteria(Criteria.where("bookId").is("1"));

        when(mongoTemplate.find(query, Review.class)).thenReturn(reviews);

        double averageRating = reviewService.getAverageRatingForBook("1");

        assertEquals(0.0, averageRating, 0.01);
        verify(mongoTemplate).find(query, Review.class);
    }

    @Test
    void testGetAverageRatingForBook_InvalidNullBookId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.getAverageRatingForBook(null));
        assertEquals("Book ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetAllReviews() {
        Review review1 = new Review("1", "John Doe", 5, "Great book!");
        Review review2 = new Review("2", "Jane Smith", 4, "Good read");
        List<Review> expectedReviews = Arrays.asList(review1, review2);

        when(mongoTemplate.findAll(Review.class)).thenReturn(expectedReviews);

        List<Review> actualReviews = reviewService.getAllReviews();

        assertEquals(expectedReviews, actualReviews);
        verify(mongoTemplate).findAll(Review.class);
    }

    @Test
    void testDeleteReview() {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is("1"));

        reviewService.deleteReview("1");

        verify(mongoTemplate).remove(query, Review.class);
    }

    @Test
    void testDeleteReview_InvalidNullReviewId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.deleteReview(null));
        assertEquals("Review ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testDeleteReview_InvalidEmptyReviewId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.deleteReview(""));
        assertEquals("Review ID cannot be null or empty", exception.getMessage());
    }
}