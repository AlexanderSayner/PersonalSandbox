# Use WildFly 27+ as the base image for Jakarta EE 10 support
FROM quay.io/wildfly/wildfly:27.0.1.Final

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

# Create a startup configuration file
COPY standalone.conf /opt/jboss/wildfly/bin/

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
    cp build/libs/javaee-graphql-project.war /opt/jboss/wildfly/standalone/deployments/

# Switch back to the default WildFly working directory
WORKDIR /opt/jboss/wildfly

# Expose port 8080
EXPOSE 8080

# Run WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]