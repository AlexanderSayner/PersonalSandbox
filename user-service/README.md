# User Service

A Gradle Kotlin Spring Boot 3 service for storing, registration and authenticating users with PostgreSQL userdb. This service provides user management functionality with JWT-based authentication and role-based authorization.

## Features

- User registration and authentication
- JWT token generation and validation
- Role-based access control
- User profile management
- PostgreSQL database integration
- Docker containerization

## Database Schema

### Users Table
| Column      | Type      | Description                          |
|-------------|-----------|--------------------------------------|
| user_id     | UUID      | Unique identifier for the user       |
| username    | String    | Username of the user                 |
| email       | String    | Email of the user                    |
| password_hash | String  | Hashed password of the user          |
| created_at  | Timestamp | Date and time when the user was created |
| updated_at  | Timestamp | Date and time when the user was last updated |

### User Profiles Table
| Column      | Type      | Description                          |
|-------------|-----------|--------------------------------------|
| profile_id  | UUID      | Unique identifier for the user profile |
| user_id     | UUID      | Foreign key to the Users table       |
| first_name  | String    | First name of the user               |
| last_name   | String    | Last name of the user                |
| address     | String    | Address of the user                  |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate user and get JWT token

### User Management
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users/{userId}/profile` - Create or update user profile
- `GET /api/users/{userId}/profile` - Get user profile

## Configuration

The service uses the following environment variables:
- `SPRING_DATASOURCE_URL` - PostgreSQL database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT signing
- `JWT_EXPIRATION` - JWT token expiration time

## Integration with Docker Compose

The user service is integrated into the main docker-compose.yml file as a service named `user-service` that runs on port 8087 and connects to the PostgreSQL database.

## Security

- Passwords are securely hashed using BCrypt
- JWT tokens contain user roles for authorization
- Role-based access control for protected endpoints
- CORS configured for cross-origin requests

## Local Development
### Environment
```shell
export USER_DATABASE_HOST=localhost;USER_DATABASE_PORT=6432;USER_DATABASE_USERNAME=botuser;USER_DATABASE_PWD=botuser
```
