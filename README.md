# Library web system

This project demonstrates a Java stack web system for a bookshop management 

## Projects Overview

### Javaee-graphql-project
JavaEE App is a Book Library service based on ***Jakarta EE 11*** and Java 17.
Uses ***GraphQL*** as an API and ***Postgres*** for a data storage.

### Spring-reviewer-service
Spring reviewer service made for Books Reviewing based on pure ***Spring 7*** framework.
Takes a book from Book Library service and caches it in ***Redis***.
Reviews themselves store in ***MongoDB***. 

## Changes Made

### 1. Updated Book Entity
- Added JPA annotations to the `Book` model
- Changed ID from `String` to `Long` with auto-generation
- Added table mapping to `books` table

### 2. JPA-Enabled Book Service
- Replaced in-memory list with JPA EntityManager
- Implemented proper database operations using JPA
- Added transaction management with `@Transactional`

### 3. Dependencies
- Added JPA API dependency
- Added PostgreSQL JDBC driver
- Added Hibernate as JPA implementation
- Added JTA for transaction management

### 4. Configuration
- Created `persistence.xml` for JPA configuration
- Configured PostgreSQL datasource for WildFly
- Added proper database connection settings

## Docker Compose Services

The `docker-compose.yml` file includes:

1. **PostgreSQL Database** - Stores the book library data
2. **JavaEE Application** - The GraphQL API server
3. **Adminer** - Web-based database management tool

## Running the Application

To start all services:

```bash
docker-compose up --build
```

The services will be available at:
- Application: http://localhost:8080
- GraphQL Endpoint: http://localhost:8080/graphql
- Adminer (DB UI): http://localhost:8081
- PostgreSQL: localhost:5432

## GraphQL Queries and Mutations

Once the application is running, you can access the GraphQL endpoint at `/graphql` and perform operations like:

### Get all books:
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

### Get a book by ID:
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

### Add a new book:
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

## Database Schema

The application automatically creates the `books` table with the following structure:
- `id` (BIGSERIAL, Primary Key, Auto-increment)
- `title` (VARCHAR, NOT NULL)
- `author` (VARCHAR, NOT NULL)
- `year` (INTEGER)