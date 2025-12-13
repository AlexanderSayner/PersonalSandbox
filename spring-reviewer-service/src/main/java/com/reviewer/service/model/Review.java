package com.reviewer.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String bookId;
    private String reviewerName;
    private int rating; // 1-5 scale
    private String comment;
    private long timestamp;

    // Default constructor
    public Review() {
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor with parameters
    public Review(String bookId, String reviewerName, int rating, String comment) {
        this.bookId = bookId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", bookId='" + bookId + '\'' +
                ", reviewerName='" + reviewerName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}