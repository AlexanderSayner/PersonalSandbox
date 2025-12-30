# Build stage
#FROM gradle:9.2.1-jdk25 AS build
FROM gradle:8.14.3-jdk24-graal AS build

WORKDIR /opt/app

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
RUN gradle bootJar

# Extract the fat jar (or create a layered jar structure)
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

# Runtime stage
#FROM openjdk:25-ea-25-bookworm
FROM openjdk:24-ea-24-bookworm

WORKDIR /opt/app

# Copy layers from the builder stage
COPY --from=build /opt/app/dependencies/ ./
COPY --from=build /opt/app/spring-boot-loader/ ./
COPY --from=build /opt/app/snapshot-dependencies/ ./
COPY --from=build /opt/app/application/ ./

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]