# Stage 1: Build stage using Gradle
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /build
# Copy project files (excluding Gradle wrapper files)
COPY build.gradle .
COPY settings.gradle .
COPY src/ src/
# Generate the Gradle wrapper files
RUN gradle wrapper
# Run the Gradle build
RUN ./gradlew build

# Stage 2: Runtime stage using WildFly 27+ as the base image for Jakarta EE 10 support
FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17
# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
# Install PostgreSQL JDBC driver using JBoss CLI
COPY --chown=jboss:jboss src/main/resources/postgresql-module.xml /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/module.xml
# Create directory for PostgreSQL driver
RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/
# Download and install PostgreSQL JDBC driver
RUN curl -L https://jdbc.postgresql.org/download/postgresql-42.6.0.jar -o /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/postgresql-42.6.0.jar
# Create JBoss CLI script to configure PostgreSQL datasource
RUN echo "embed-server --std-out=echo" > /tmp/datasource-configuration.cli && \
    echo "/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)" >> /tmp/datasource-configuration.cli && \
    echo "/subsystem=datasources/data-source=PostgreSQLDS:add(jndi-name=java:jboss/datasources/PostgreSQLDS,enabled=true,driver-name=postgresql,connection-url=jdbc:postgresql://postgres:5432/librarydb,initial-pool-size=5,min-pool-size=5,max-pool-size=20,user-name=libraryuser,password=librarypass)" >> /tmp/datasource-configuration.cli && \
    echo "stop-embedded-server" >> /tmp/datasource-configuration.cli
# Run the CLI script to configure the datasource
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/datasource-configuration.cli
# Copy the built WAR file from the build stage
COPY --from=build /build/build/libs/javaee-graphql-project.war /opt/jboss/wildfly/standalone/deployments/
# Create a startup configuration file
COPY standalone.conf /opt/jboss/wildfly/bin/

# Expose port 8080
EXPOSE 8080

# Run WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]