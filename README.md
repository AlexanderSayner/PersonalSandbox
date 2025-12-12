# Library Web System

This project demonstrates a distributed Java microservices system for bookshop management, consisting of multiple interconnected services with persistent storage and caching capabilities.

## Projects Overview

### JavaEE GraphQL Project
JavaEE application serving as the core Book Library service based on ***Jakarta EE 11*** and ***Java 17***.
Uses ***GraphQL*** as the primary API and ***PostgreSQL*** for persistent data storage.
Deployed on ***WildFly*** application server via ***Docker*** containerization.

### Spring Reviewer Service
***Spring Framework 7*** (non-Boot) service providing book reviewing functionality.
Fetches book information from the JavaEE library service and caches frequently accessed books in ***Redis***.
Reviews are stored in ***MongoDB*** for scalable document storage.

## System Architecture

The complete system consists of 5 interconnected services:
1. **PostgreSQL Database** - Persistent storage for book library data
2. **JavaEE Application** - Core GraphQL API server for book management
3. **Spring Reviewer Service** - Review management with Redis caching and MongoDB storage
4. **Redis Cache** - High-performance caching layer for book data
5. **MongoDB** - Document database for storing book reviews
6. **Adminer** - Web-based database management interface

## Changes Made

Since the initial development, the system has evolved significantly:

### Initial Implementation
- Started with basic JavaEE application using GraphQL API
- Used in-memory storage initially
- Simple book management functionality

### Database Integration
- Integrated PostgreSQL for persistent book storage
- Added JPA/Hibernate for object-relational mapping
- Implemented proper transaction management
- Created comprehensive persistence configuration

### Microservices Architecture
- Developed Spring-based reviewer service as separate component
- Implemented inter-service communication between JavaEE and Spring apps
- Added Redis caching layer to optimize performance
- Integrated MongoDB for scalable review storage

### Container Orchestration
- Containerized all services using Docker
- Created comprehensive docker-compose orchestration
- Configured proper network isolation and service discovery
- Set up automated initialization scripts for databases

### API Enhancement
- Extended GraphQL schema with additional queries and mutations
- Implemented REST API endpoints for review management
- Added comprehensive API documentation and testing endpoints
- Improved error handling and validation

### Infrastructure Improvements
- Added proper environment variable configuration
- Implemented volume persistence for databases
- Enhanced service dependencies and startup sequences
- Added health checks and monitoring capabilities

## Docker Compose Services

The `docker-compose.yml` file orchestrates the complete microservices ecosystem:

1. **PostgreSQL Database** - Persistent storage for book library data
2. **JavaEE Application** - Core GraphQL API server for book management
3. **Spring Reviewer Service** - Review management with Redis caching and MongoDB storage
4. **Redis Cache** - High-performance caching layer for book data
5. **MongoDB** - Document database for storing book reviews
6. **Adminer** - Web-based database management interface

## Running the Application

To start all services:

```bash
docker-compose up --build
```

The services will be available at:
- JavaEE Application: http://localhost:8090/javaee-graphql-project
- GraphQL Endpoint: http://localhost:8090/javaee-graphql-project/graphql
- GraphQL UI: http://localhost:8090/javaee-graphql-project/graphql/ui
- Reviewer Service: http://localhost:8082/reviewer/api/reviews
- Adminer (DB UI): http://localhost:8081
- PostgreSQL: localhost:6543
- Redis: localhost:6379
- MongoDB: localhost:27017

## API Documentation

### JavaEE GraphQL API
Access the GraphQL endpoint at `/graphql` to perform operations:

#### Get all books:
```graphql
query {
  allBooks {
    id
    title
    author
    year
  }
}
```

#### Get a book by ID:
```graphql
query {
  book(id: 1) {
    id
    title
    author
    year
  }
}
```

#### Add a new book:
```graphql
mutation {
  addBook(input: {title: "New Book", author: "Author Name", year: 2023}) {
    id
    title
    author
    year
  }
}
```

#### Update a book:
```graphql
mutation {
  updateBook(id: "1", input: {title: "Updated Title", author: "New Author", year: 2024}) {
    id
    title
    author
    year
  }
}
```

#### Delete a book:
```graphql
mutation {
  deleteBook(id: "1") {
    success
  }
}
```

### Spring Reviewer Service API
The reviewer service provides REST endpoints for managing book reviews:

#### Add a review:
```bash
POST http://localhost:8082/reviewer/api/reviews/book/1
Content-Type: application/json

{
  "reviewerName": "John Doe",
  "rating": 5,
  "comment": "Excellent book!"
}
```

#### Get all reviews for a book:
```bash
GET http://localhost:8082/reviewer/api/reviews/book/1
```

#### Get average rating for a book:
```bash
GET http://localhost:8082/reviewer/api/reviews/book/1/average-rating
```

#### Get all reviews:
```bash
GET http://localhost:8082/reviewer/api/reviews
```

#### Delete a review:
```bash
DELETE http://localhost:8082/reviewer/api/reviews/{reviewId}
```

#### OpenAPI Documentation:
The API documentation is available at: `http://localhost:8082/reviewer/v3/api-docs`

## Database Schema

The JavaEE application automatically creates the `books` table with the following structure:
- `id` (BIGSERIAL, Primary Key, Auto-increment)
- `title` (VARCHAR, NOT NULL)
- `author` (VARCHAR, NOT NULL)
- `year` (INTEGER)

The Spring Reviewer service uses MongoDB collections to store reviews with the following structure:
- `_id` (ObjectId, Primary Key)
- `bookId` (Integer, Reference to book)
- `reviewerName` (String)
- `rating` (Integer, 1-5 scale)
- `comment` (String)
- `timestamp` (Date)

## System Integration

The services work together in the following way:
1. The JavaEE application manages the core book library data in PostgreSQL
2. The Spring Reviewer service fetches book information from the JavaEE app
3. Frequently accessed books are cached in Redis to reduce load
4. Reviews are stored in MongoDB for flexible querying and scalability
5. All services communicate through REST APIs and are orchestrated via Docker Compose