FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the gradle wrapper files
COPY gradlew .
COPY gradle/ ./gradle/

# Make the gradle wrapper executable
RUN chmod +x ./gradlew

# Copy build files
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy source code
COPY src/ ./src/

# Build the application
RUN ./gradlew build -xmx2g

# Extract the fat jar (or create a layered jar structure)
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

# Create a lightweight runtime image
FROM openjdk:17-jre-slim

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy layers from the builder stage
COPY --from=0 /app/dependencies/ ./
COPY --from=0 /app/spring-boot-loader/ ./
COPY --from=0 /app/snapshot-dependencies/ ./
COPY --from=0 /app/application/ ./

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]