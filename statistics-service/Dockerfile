FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar statistics-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "statistics-service.jar"]