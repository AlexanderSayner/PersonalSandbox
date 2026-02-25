# Architecture Overview

## System Components

This system consists of multiple microservices that work together to provide a comprehensive book management and sales platform.

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    SYSTEM ARCHITECTURE                                              │
├─────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                     │
│  ┌─────────────────┐      HTTP      ┌──────────────────┐                                            │
│  │                 │ ─────────────→ │                  │                                            │
│  │   Frontend      │                │  Bookstore       │←─────────── Kafka events ──────────────────┤
│  │   Clients       │ ←────────────  │  Service         │                                            │
│  │                 │    GraphQL     │                  │                                            │
│  └─────────────────┘                └──────────────────┘                                            │
│                                              │                                                      │
│                                              │ gRPC                                                 │
│                                              ▼                                                      │
│  ┌─────────────────┐                ┌──────────────────┐                                            │
│  │                 │                │                  │                                            │
│  │  Cassandra DB   │ ←────────────  │  Workshop        │ ←──────┐                                   │
│  │                 │                │  Service         │        │                                   │
│  │                 │                │                  │        │                                   │
│  └─────────────────┘                └──────────────────┘        │                                   │
│                                              │                  │                                   │
│                                              │ GraphQL          │                                   │
│                                              ▼                  │                                   │
│                                     ┌──────────────────┐        │                                   │
│                                     │                  │        │ REST                              │
│                                     │  Library         │        │                                   │
│                                     │  Service         │        │                                   │
│                                     │                  │        │                                   │
│                                     └──────────────────┘        │                                   │
│                                              │                  │                                   │
│                                        REST  │                  │                                   │
│                                              │                  │                                   │
│                                              ▼                  ▼                                   │
│                                   ┌──────────────────┐ ┌──────────────────┐                         │
│                                   │                  │ │                  │                         │
│                                   │  Review          │ │  Pricing         │                         │
│                                   │  Service         │ │  Service         │                         │
│                                   │                  │ │                  │                         │
│                                   └──────────────────┘ └──────────────────┘                         │
│                                             │                  │                                    │
│                                             ▼                  ▼                                    │
│                                   ┌──────────────────┐ ┌──────────────────┐                         │
│                                   │                  │ │                  │                         │
│                                   │  MongoDB         │ │  PostgreSQL      │                         │
│                                   │  (Reviews)       │ │  (Prices)        │                         │
│                                   │                  │ │                  │                         │
│                                   └──────────────────┘ └──────────────────┘                         │
│                                                                                                     │
│                                                                                                     │
│  ┌─────────────────┐      HTTP      ┌──────────────────┐                                            │
│  │                 │ ─────────────→ │                  │                                            │
│  │   Users         │                │  Statistics      │                                            │
│  │   Service       │                │  Service         │                                            │
│  │                 │ ←──────────────│                  │ ←────── Kafka Events ──────────────────────┤
│  └─────────────────┘   HTTP         └──────────────────┘                                            │
│         │                                    │                                                      │
│         │                                    │                                                      │
│         ▼                                    ▼                                                      │
│  ┌─────────────────┐                ┌──────────────────┐                                            │
│  │                 │                │                  │                                            │
│  │ PostgreSQL      │                │ ClickHouse       │                                            │
│  │ (Users)         │                │ (Statistics)     │                                            │
│  │                 │                │                  │                                            │
│  └─────────────────┘                └──────────────────┘                                            │
│                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## Service Details

### 1. Jakarta EE Library Service (Book Library)
- **Technology**: Jakarta EE
- **Database**: PostgreSQL
- **API**: GraphQL endpoints
- **Purpose**: Stores and manages book information
- **Interactions**:
  - Serves book data to Review Service via HTTP
  - Serves book information to Bookstore Service via HTTP

### 2. Spring 7 Review Service
- **Technology**: Spring Framework 7
- **Database**: MongoDB
- **API**: HTTP endpoints
- **Purpose**: Manages book reviews
- **Interactions**:
  - Makes HTTP requests to Library Service to fetch book information
  - Stores reviews in MongoDB

### 3. Spring Boot 4 Bookstore Service
- **Technology**: Spring Boot 4
- **Database**: None (orchestration service)
- **API**: GraphQL endpoints for frontend clients
- **Purpose**: Orchestrates bookstore operations
- **Interactions**:
  - Exposes GraphQL endpoints to frontend clients
  - Communicates with Workshop Service via gRPC to get items
  - Fetches item information from Library Service via HTTP

### 4. Spring Boot 4 Workshop Service
- **Technology**: Spring Boot 4
- **Database**: Cassandra
- **API**: gRPC endpoints
- **Purpose**: Manages items available for sale
- **Interactions**:
  - Stores items in Cassandra database
  - Provides items to Bookstore Service via gRPC

### 5. Spring Boot 4 Pricing Service
- **Technology**: Spring Boot 4
- **Database**: PostgreSQL
- **API**: RESTful API
- **Purpose**: Manages pricing information
- **Interactions**:
  - Provides prices to Bookstore Service via REST API
  - Stores pricing data in PostgreSQL

### 6. Spring Boot 4 Statistics Service
- **Technology**: Spring Boot 4
- **Database**: ClickHouse (NoSQL)
- **API**: HTTP endpoints
- **Purpose**: Collects and analyzes real-time sales statistics
- **Interactions**:
  - Consumes selling data from Kafka (sent by Bookstore service)
  - Stores real-time statistics in ClickHouse

### 7. Spring Boot 3 User Service
- **Technology**: Spring Boot 3
- **Database**: PostgreSQL
- **API**: HTTP endpoints
- **Purpose**: Manages user information
- **Interactions**:
  - Provides user data via HTTP endpoints
  - Stores user information in PostgreSQL

## Data Flow

1. **Frontend Client Request Flow**:
   - Frontend → Bookstore Service (GraphQL) → Workshop Service (gRPC) + Library Service (HTTP) + Pricing Service (REST)

2. **Review Creation Flow**:
   - Review Service → Library Service (HTTP) → MongoDB (Store Review)

3. **Sales Data Flow**:
   - Bookstore Service → Kafka → Statistics Service → ClickHouse

4. **User Management Flow**:
   - Frontend/Other Services → User Service (HTTP) → PostgreSQL

## Communication Protocols

- **GraphQL**: Used for frontend communication with Bookstore Service and internal access to Library Service
- **HTTP/REST**: Standard web communication between services
- **gRPC**: High-performance communication between Bookstore and Workshop services
- **Kafka**: Event streaming for sales data processing