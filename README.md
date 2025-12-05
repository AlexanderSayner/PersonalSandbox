# JavaEE GraphQL Project

This project demonstrates a GraphQL API implementation using JavaEE technologies.

## Docker Deployment

To deploy the application using Docker, follow these steps:

1. Build the Docker image:
   ```bash
   docker build -t javaee-graphql-project .
   ```

2. Run the container:
   ```bash
   docker run -p 8380:8380 javaee-graphql-project
   ```

The application will be accessible at `http://localhost:8380/javaee-graphql-project`.