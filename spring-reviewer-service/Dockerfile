# Build stage
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src/ src/
RUN gradle clean build -Dorg.gradle.jvmargs=-Xmx1g --info

# Runtime stage
FROM tomcat:11.0.15-jre17
WORKDIR /app

# Copy the built WAR file from the build stage
COPY --from=build /app/build/libs/reviewer-service-1.0.0.war /usr/local/tomcat/webapps/reviewer.war

# Expose port
EXPOSE 8080

# Set environment variables
ENV JAVAEE_APP_URL=http://javaee-app:8080
ENV REDIS_HOST=redis
ENV MONGODB_HOST=mongodb
ENV MONGODB_DATABASE=reviewerdb
ENV MONGODB_USERNAME=revieweruser
ENV MONGODB_PASSWORD=reviewerpwd

# Start Tomcat
CMD ["catalina.sh", "run"]
