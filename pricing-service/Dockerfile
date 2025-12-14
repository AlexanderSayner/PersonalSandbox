FROM amazoncorretto:21-alpine
RUN apk add --no-cache libc6-compat
RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring
USER 1001:1001
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/app.jar"]