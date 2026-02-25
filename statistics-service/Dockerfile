FROM openjdk:24-ea-24-bookworm

WORKDIR /app

COPY build/libs/*.jar statistics-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "statistics-service.jar"]