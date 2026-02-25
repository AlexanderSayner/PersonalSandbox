FROM openjdk:24-ea-24-bookworm

WORKDIR /app

COPY build/libs/*.jar workshop-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "workshop-service.jar"]