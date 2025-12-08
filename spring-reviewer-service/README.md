# Spring Reviewer Service

A Java Spring (non-Boot) application that provides book review functionality. This service integrates with a JavaEE library application to fetch book information, caches frequently accessed books in Redis, and stores reviews in MongoDB.

## Architecture

- **Spring Framework**: Core application framework (non-Boot)
- **Redis**: Caching layer for book information to reduce requests to JavaEE app
- **MongoDB**: NoSQL database for storing book reviews
- **Tomcat**: Servlet container for deployment
- **Docker**: Containerization for easy deployment

## Features

- Fetch book information from JavaEE library application
- Cache books in Redis to minimize API calls
- Add, retrieve, and manage book reviews
- Calculate average ratings per book
- RESTful API endpoints for review management

## API Endpoints

- `POST /api/reviews/book/{bookId}` - Add a review for a book
- `GET /api/reviews/book/{bookId}` - Get all reviews for a book
- `GET /api/reviews/book/{bookId}/average-rating` - Get average rating for a book
- `GET /api/reviews` - Get all reviews
- `DELETE /api/reviews/{reviewId}` - Delete a review

## Project Structure

```
spring-reviewer-service/
├── pom.xml                 # Maven dependencies
├── Dockerfile             # Docker build instructions
├── src/
│   └── main/
│       ├── java/
│       │   └── com/reviewer/service/
│       │       ├── controller/      # REST controllers
│       │       ├── model/           # Data models
│       │       └── service/         # Business logic
│       └── webapp/
│           └── WEB-INF/
│               ├── web.xml          # Web configuration
│               └── reviewer-servlet.xml  # Spring configuration
└── docker-compose.yml     # Docker Compose configuration
```

## Running the Application

The application is designed to run in Docker with the JavaEE library application, Redis, and MongoDB. Use the main docker-compose.yml to start all services:

```bash
docker-compose up --build
```

The reviewer service will be available at `http://localhost:8082/api/reviews`

## Dependencies

- JavaEE Library Application (running on http://javaee-app:8080)
- Redis (running on redis:6379)
- MongoDB (running on mongodb:27017)