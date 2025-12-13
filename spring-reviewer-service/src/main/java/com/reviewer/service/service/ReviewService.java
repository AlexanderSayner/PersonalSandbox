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
        Review savedReview = mongoTemplate.save(review);
        logger.info("Review saved for book: {}", review.getBookId());
        return savedReview;
    }
    
    public List<Review> getReviewsByBookId(String bookId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("bookId").is(bookId));
        List<Review> reviews = mongoTemplate.find(query, Review.class);
        logger.info("Found {} reviews for book: {}", reviews.size(), bookId);
        return reviews;
    }
    
    public double getAverageRatingForBook(String bookId) {
        List<Review> reviews = getReviewsByBookId(bookId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        
        return sum / reviews.size();
    }
    
    public List<Review> getAllReviews() {
        return mongoTemplate.findAll(Review.class);
    }
    
    public void deleteReview(String reviewId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(reviewId));
        mongoTemplate.remove(query, Review.class);
        logger.info("Deleted review: {}", reviewId);
    }
}