# Use WildFly 27+ as the base image for Jakarta EE 10 support
FROM quay.io/wildfly/wildfly:27.0.1.Final

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Create a working directory for the build process
WORKDIR /build

# Copy Gradle wrapper and project files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src/ src/

# Make gradlew executable and build the project
RUN chmod +x ./gradlew && \
    ./gradlew build && \
    cp build/libs/javaee-graphql-project.war /opt/wildfly/standalone/deployments/

# Switch back to the default WildFly working directory
WORKDIR /opt/wildfly

# Set the deployment scanner to auto-deploy
RUN echo '<deployment-scanner path="deployments" relative-to="jboss.server.base.dir" scan-interval="5000" auto-deploy-exploded="false"/>' >> /opt/wildfly/standalone/configuration/standalone.xml

# Expose port 8080
EXPOSE 8080

# Run WildFly
CMD ["/opt/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]