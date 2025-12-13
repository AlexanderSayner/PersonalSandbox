# Bookshop Service

A Kotlin Spring Boot 3 service that provides bookshop functionality with GraphQL API, gRPC integration, and database management.

## Features

- **GraphQL API**: Complete CRUD operations for products, orders, and order items
- **gRPC Integration**: Connects to Java EE service to fetch book information
- **Database Management**: Uses PostgreSQL with Flyway for schema migrations
- **Spring Boot 3**: Built with modern Spring Boot framework
- **Docker Support**: Containerized application with multi-stage Dockerfile

## Architecture

- **Language**: Kotlin
- **Framework**: Spring Boot 3
- **Database**: PostgreSQL
- **API**: GraphQL with GraphiQL UI
- **Communication**: gRPC for inter-service communication
- **Migration**: Flyway for database schema management

## GraphQL Schema

The service exposes the following GraphQL types:

### Queries
- `products`: Get all products
- `product(id: UUID!)`: Get a specific product
- `orders`: Get all orders
- `order(id: UUID!)`: Get a specific order
- `orderItems`: Get all order items
- `orderItem(id: UUID!)`: Get a specific order item
- `book(id: Int!)`: Get book information from Java EE service

### Mutations
- `createProduct`, `updateProduct`, `deleteProduct`
- `createOrder`, `updateOrder`, `deleteOrder`
- `createOrderItem`, `updateOrderItem`, `deleteOrderItem`

## Database Schema

### Product Table
- `product_id`: UUID (Primary Key)
- `title`: String
- `description`: String
- `price`: Decimal
- `product_type`: Enum (BOOK, DIGITAL_BOOK, PHYSICAL_GOOD, DIGITAL_GOOD)
- `library_book_id`: UUID (Foreign key to Library service)
- `created_at`: Timestamp
- `updated_at`: Timestamp

### Order Table
- `order_id`: UUID (Primary Key)
- `user_id`: UUID
- `status`: Enum (PENDING, COMPLETED, CANCELLED)
- `total_amount`: Decimal
- `created_at`: Timestamp
- `updated_at`: Timestamp

### OrderItem Table
- `order_item_id`: UUID (Primary Key)
- `order_id`: UUID (Foreign key to Orders)
- `product_id`: UUID (Foreign key to Products)
- `quantity`: Integer
- `price`: Decimal
- `created_at`: Timestamp
- `updated_at`: Timestamp

## gRPC Integration

The service connects to the Java EE service via gRPC to fetch book information. The `book(id: Int!)` query connects to the Java EE service and returns book details.

## Docker Configuration

The service is configured to run in Docker with the following ports:
- `8083`: Application port
- Health check endpoint at `/actuator/health`

## Environment Variables

- `SPRING_DATASOURCE_URL`: PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JAVAEE_SERVICE_ADDRESS`: Address of the Java EE gRPC service

## Endpoints

- `GET /graphql`: GraphQL endpoint
- `GET /graphiql`: GraphiQL UI for testing queries
- `GET /actuator/health`: Health check endpoint