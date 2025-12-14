# Statistics Service

A Spring Boot 4 service that consumes workshop selling and viewing products data via Kafka and stores it in ClickHouse database.

## Features

- Consumes sales and user activity data from Kafka topics
- Stores data in ClickHouse database
- Provides GraphQL endpoints for querying statistics
- Includes Actuator and Prometheus metrics

## Models

### Sales_Statistics
| Column | Type | Description |
|--------|------|-------------|
| sale_id | UUID | Unique identifier for the sale |
| product_id | UUID | Unique identifier for the product |
| quantity | Integer | Quantity of the product sold |
| amount | Decimal | Total amount of the sale |
| sale_date | Date | Date of the sale |

### User_Activity
| Column | Type | Description |
|--------|------|-------------|
| activity_id | UUID | Unique identifier for the user activity |
| user_id | UUID | Unique identifier for the user |
| activity_type | String | Type of activity (e.g., login, purchase, view) |
| activity_date | Date | Date of the activity |

## Endpoints

- `/graphql` - GraphQL endpoint for querying statistics
- `/actuator/health` - Health check
- `/actuator/prometheus` - Prometheus metrics

## Configuration

Environment variables:
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka broker URL (default: localhost:9092)
- `CLICKHOUSE_URL` - ClickHouse JDBC URL (default: jdbc:clickhouse://localhost:8123/default)
- `CLICKHOUSE_USER` - ClickHouse username (default: default)
- `CLICKHOUSE_PASSWORD` - ClickHouse password (default: empty)