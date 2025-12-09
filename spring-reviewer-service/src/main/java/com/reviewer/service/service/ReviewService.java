package com.reviewer.service.service;

import com.reviewer.service.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public Review saveReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
        if (review.getBookId() == null || review.getBookId().trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        if (review.getReviewerName() == null || review.getReviewerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Reviewer name cannot be null or empty");
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Review comment cannot be null or empty");
        }
        
        try {
            Review savedReview = mongoTemplate.save(review);
            logger.info("Review saved for book: {}", review.getBookId());
            return savedReview;
        } catch (Exception e) {
            logger.error("Error saving review for book: " + review.getBookId(), e);
            throw new RuntimeException("Error saving review: " + e.getMessage(), e);
        }
    }
    
    public List<Review> getReviewsByBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("bookId").is(bookId));
            List<Review> reviews = mongoTemplate.find(query, Review.class);
            logger.info("Found {} reviews for book: {}", reviews.size(), bookId);
            return reviews;
        } catch (Exception e) {
            logger.error("Error retrieving reviews for book: " + bookId, e);
            throw new RuntimeException("Error retrieving reviews: " + e.getMessage(), e);
        }
    }
    
    public double getAverageRatingForBook(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        try {
            List<Review> reviews = getReviewsByBookId(bookId);
            if (reviews.isEmpty()) {
                return 0.0;
            }
            
            double sum = reviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();
            
            return Math.round((sum / reviews.size()) * 100.0) / 100.0; // Round to 2 decimal places
        } catch (Exception e) {
            logger.error("Error calculating average rating for book: " + bookId, e);
            throw new RuntimeException("Error calculating average rating: " + e.getMessage(), e);
        }
    }
    
    public List<Review> getAllReviews() {
        try {
            return mongoTemplate.findAll(Review.class);
        } catch (Exception e) {
            logger.error("Error retrieving all reviews", e);
            throw new RuntimeException("Error retrieving all reviews: " + e.getMessage(), e);
        }
    }
    
    public void deleteReview(String reviewId) {
        if (reviewId == null || reviewId.trim().isEmpty()) {
            throw new IllegalArgumentException("Review ID cannot be null or empty");
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(reviewId));
            mongoTemplate.remove(query, Review.class);
            logger.info("Deleted review: {}", reviewId);
        } catch (Exception e) {
            logger.error("Error deleting review: " + reviewId, e);
            throw new RuntimeException("Error deleting review: " + e.getMessage(), e);
        }
    }
}