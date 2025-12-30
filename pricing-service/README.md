# Pricing Service

A Kotlin-based Spring Boot 4 microservice that calculates product pricing with delivery costs. This service connects to the PostgreSQL `pricingdb` database and communicates with the Bookshop service via gRPC to fetch product prices.

## Features

- Calculates total pricing including delivery costs
- Supports multiple delivery methods (Standard, Express, Overnight)
- Uses Flyway for database migrations
- Integrates with Bookshop service via gRPC
- Exposes REST API for pricing calculations
- Includes Prometheus metrics and OpenAPI documentation
- Implements Spring Boot Actuator endpoints

## Architecture

- **Database**: PostgreSQL (`pricingdb`)
- **Language**: Kotlin
- **Framework**: Spring Boot 4
- **Communication**: gRPC client for Bookshop service, REST API for external clients
- **Database Migration**: Flyway
- **Documentation**: OpenAPI/Swagger
- **Monitoring**: Actuator + Prometheus

## Tables

### Delivery_Methods
| Column | Type | Description |
|--------|------|-------------|
| method_id | UUID | Unique identifier for the delivery method |
| name | String | Name of the delivery method (e.g., Standard, Express, Overnight) |
| description | String | Description of the delivery method |

### Pricing_Rules
| Column | Type | Description |
|--------|------|-------------|
| rule_id | UUID | Unique identifier for the pricing rule |
| method_id | UUID | Foreign key to the Delivery Methods table |
| min_distance | Integer | Minimum distance for this rule to apply (in miles or kilometers) |
| max_distance | Integer | Maximum distance for this rule to apply (in miles or kilometers) |
| min_weight | Decimal | Minimum weight for this rule to apply (in kg or lbs) |
| max_weight | Decimal | Maximum weight for this rule to apply (in kg or lbs) |
| cost | Decimal | Cost for this delivery method within the specified distance and weight range |

## Endpoints

- `GET /api/v1/pricing/health` - Health check endpoint
- `POST /api/v1/pricing/calculate` - Calculate pricing with delivery cost

### Calculate Pricing Request Example
```json
{
  "productId": "product-123",
  "distance": 15,
  "weight": 2.5,
  "deliveryMethod": "Express"
}
```

### Calculate Pricing Response Example
```json
{
  "productId": "product-123",
  "basePrice": 29.99,
  "deliveryCost": 12.99,
  "totalWithDelivery": 42.98,
  "totalWithoutDelivery": 29.99
}
```

## Running the Service

The service is designed to run as part of the overall system using Docker Compose:

```bash
docker-compose up pricing-service
```

## Dependencies

- PostgreSQL database with `pricingdb`
- Bookshop service (for product price information via gRPC)
- gRPC communication with Bookshop service

## Local Development
### Environment
```shell
export PRICING_DB_HOST=localhost;PRICING_DB_PORT=6432;PRICING_DB_USERNAME=botuser;PRICING_DB_PWD=botuser
```
