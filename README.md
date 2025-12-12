# Library web system

This project demonstrates a Java stack web system for a bookshop management 

## Projects Overview

### Javaee-graphql-project
JavaEE App is a Book Library service based on ***Jakarta EE 11*** deploying by ***WildFly*** and Java 17.
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

# Pure Spring Configuration Example

This workspace also includes a standalone Spring application configured entirely with pure Spring configuration, without using Spring Boot.

## Features

- Pure Spring configuration using Java annotations
- No Spring Boot dependencies
- MVC architecture with JSP views
- Deployable via Docker
- Maven-based build system

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/
│   │       ├── controller/
│   │       │   └── HomeController.java
│   │       └── config/
│   │           ├── WebConfig.java
│   │           └── WebInitializer.java
│   ├── webapp/
│   │   ├── WEB-INF/
│   │   │   └── views/
│   │   │       ├── home.jsp
│   │       │       └── about.jsp
│   │   └── resources/
│   │       └── css/
│   │           └── style.css
└── pom.xml
```

## How to Build and Run

### Using Maven (for local development)

```bash
# Compile and package the application
mvn clean package

# Run with embedded Jetty server
mvn jetty:run
```

The application will be available at `http://localhost:8080`

### Using Docker

```bash
# Build the application
mvn clean package

# Build the Docker image
docker build -t pure-spring-app .

# Run the container
docker run -p 8080:8080 pure-spring-app
```

### Using Docker Compose

```bash
# Build and run with Docker Compose
docker-compose -f docker-compose.spring.yml up --build
```

## Configuration Details

- **WebConfig.java**: Contains Spring MVC configuration using `@Configuration` annotation
- **WebInitializer.java**: Implements WebApplicationInitializer to replace traditional `web.xml` with Java configuration, properly setting up the Spring context and dispatcher servlet
- **HomeController.java**: Basic controller with two endpoints
- **JSP Views**: Located in `src/main/webapp/WEB-INF/views/`
- **Static Resources**: Located in `src/main/webapp/resources/`

## Endpoints

- `GET /` - Home page
- `GET /about` - About page

## Dependencies

- Spring Framework 5.3.21
- Servlet API 4.0.1
- JSP API 2.3.3
- JSTL 1.2
- Jetty 9.4.44 (for local testing)