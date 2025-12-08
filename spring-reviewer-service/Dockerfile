FROM openjdk:11-jre-slim

# Install Gradle
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-7.6-bin.zip && \
    unzip gradle-7.6-bin.zip && \
    rm gradle-7.6-bin.zip && \
    mv gradle-7.6 /opt/gradle && \
    apt-get clean

# Add Gradle to PATH
ENV GRADLE_HOME=/opt/gradle
ENV PATH=$PATH:$GRADLE_HOME/bin

# Create app directory
WORKDIR /app

# Copy the Gradle files
COPY build.gradle settings.gradle ./

# Copy source code
COPY src/ src/

# Build the application
RUN gradle clean build -xmx1g

# Download and install Tomcat
RUN mkdir -p /usr/local/tomcat
RUN apt-get update && apt-get install -y wget && \
    wget https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.65/bin/apache-tomcat-9.0.65.tar.gz && \
    tar -xzf apache-tomcat-9.0.65.tar.gz -C /usr/local && \
    rm apache-tomcat-9.0.65.tar.gz

# Copy the WAR file to Tomcat
RUN cp build/libs/reviewer-service-1.0.0.war /usr/local/tomcat/apache-tomcat-9.0.65/webapps/reviewer.war

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
CMD ["sh", "-c", "/usr/local/tomcat/apache-tomcat-9.0.65/bin/catalina.sh run"]