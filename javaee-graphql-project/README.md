# Java EE GraphQL Project with Gradle

This is a Java EE project that implements a GraphQL API without using Spring Boot. The project uses Gradle as the build system and SmallRye GraphQL as the MicroProfile GraphQL implementation.

## Project Structure

- `src/main/java/com/example/graphql/` - Main GraphQL API and related classes
- `src/main/java/com/example/graphql/model/` - Data models
- `src/main/java/com/example/graphql/service/` - Business logic services
- `src/main/resources/` - Configuration files
- `src/main/webapp/` - Web application resources

## Technologies Used

- Java 17
- Jakarta EE 10
- MicroProfile GraphQL (SmallRye implementation)
- CDI (Contexts and Dependency Injection)
- Gradle 8.5 (with wrapper)

## Build and Run

To build the project:

```bash
./gradlew build
```

This will create a WAR file in the `build/libs/` directory.

To run tests:

```bash
./gradlew test
```

## GraphQL API

The application provides a GraphQL API with the following operations:

### Queries

- `allBooks` - Get all books in the library
- `book(id: String)` - Get a book by its ID
- `booksByAuthor(author: String)` - Find books by author name

### Mutations

- `addBook(input: BookInput)` - Add a new book to the library
- `updateBook(id: String, input: BookInput)` - Update an existing book
- `deleteBook(id: String)` - Delete a book by ID

## Configuration

The GraphQL endpoint is available at `/graphql` and the GraphQL UI is available at `/graphql/ui`.

## Deployment

The generated WAR file can be deployed to any Jakarta EE compatible application server like:
- WildFly
- Payara
- OpenLiberty
- TomEE

## Project Setup

This project uses Gradle wrapper, so you don't need to install Gradle separately. Just run the commands with `./gradlew` instead of `gradle`.

## Docker Deployment

This project includes a Dockerfile for easy deployment. To build and run the application using Docker:

### Build the Docker Image

```bash
docker build -t javaee-graphql-project .
```

### Run the Docker Container

```bash
docker run -p 8080:8080 javaee-graphql-project
```

The application will be accessible at `http://localhost:8080/javaee-graphql-project`.

### Docker Compose (Optional)

You can also use Docker Compose to run the application:

```yaml
version: '3.8'
services:
  graphql-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xms512m -Xmx1024m
```