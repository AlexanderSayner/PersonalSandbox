FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar workshop-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "workshop-service.jar"]