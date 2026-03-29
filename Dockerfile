# Use official Tomcat image as base
FROM tomcat:9.0-jdk11-openjdk-slim

# Set working directory
WORKDIR /usr/local/tomcat

# Copy the WAR file to Tomcat's webapps directory
COPY target/pure-spring-app.war /usr/local/tomcat/webapps/

# Expose port 8080
EXPOSE 8080

# Start Tomcat server
CMD ["catalina.sh", "run"]