package org.sandbox.reviewer.controller;

import org.sandbox.reviewer.model.Book;
import org.sandbox.reviewer.model.Review;
import org.sandbox.reviewer.service.BookService;
import org.sandbox.reviewer.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private BookService bookService;
    
    @PostMapping("/book/{bookId}")
    public ResponseEntity<?> addReview(@PathVariable String bookId, @RequestBody Review review) {
        try {
            if (bookId == null || bookId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Book ID cannot be null or empty");
            }
            
            if (review == null) {
                return ResponseEntity.badRequest()
                        .body("Review cannot be null");
            }
            
            // Validate review fields
            if (review.getReviewerName() == null || review.getReviewerName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Reviewer name cannot be null or empty");
            }
            
            if (review.getRating() < 1 || review.getRating() > 5) {
                return ResponseEntity.badRequest()
                        .body("Rating must be between 1 and 5");
            }
            
            if (review.getComment() == null || review.getComment().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Review comment cannot be null or empty");
            }
            
            // Verify the book exists by fetching it from the JavaEE app via cache
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Book with ID " + bookId + " not found");
            }
            
            // Set the bookId in the review
            review.setBookId(bookId);
            
            // Save the review
            Review savedReview = reviewService.saveReview(review);
            
            logger.info("Added review for book: {}", bookId);
            return ResponseEntity.ok(savedReview);
        } catch (Exception e) {
            logger.error("Error adding review for book: {}", bookId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding review: " + e.getMessage());
        }
    }
    
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getReviewsByBookId(@PathVariable String bookId) {
        try {
            // Verify the book exists
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Book with ID " + bookId + " not found");
            }
            
            List<Review> reviews = reviewService.getReviewsByBookId(bookId);
            double averageRating = reviewService.getAverageRatingForBook(bookId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("averageRating", averageRating);
            response.put("bookId", bookId);
            
            logger.info("Retrieved {} reviews for book: {}", reviews.size(), bookId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting reviews for book: {}", bookId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting reviews: " + e.getMessage());
        }
    }
    
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<?> getAverageRatingForBook(@PathVariable String bookId) {
        try {
            // Verify the book exists
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Book with ID " + bookId + " not found");
            }
            
            double averageRating = reviewService.getAverageRatingForBook(bookId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("bookId", bookId);
            response.put("averageRating", averageRating);
            
            logger.info("Retrieved average rating for book: {} - {}", bookId, averageRating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting average rating for book: {}", bookId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting average rating: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        try {
            List<Review> reviews = reviewService.getAllReviews();
            logger.info("Retrieved all reviews, count: {}", reviews.size());
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            logger.error("Error getting all reviews", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        try {
            // TODO: Add logic to verify review exists before deletion if needed
            reviewService.deleteReview(reviewId);
            logger.info("Deleted review: {}", reviewId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting review: {}", reviewId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}