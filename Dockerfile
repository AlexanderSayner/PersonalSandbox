FROM quay.io/wildfly/wildfly-jakartaee10:27.0.1.Final

# Set environment variables for WildFly
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Copy the project files
COPY . /tmp/app/

WORKDIR /tmp/app

# Build the project using Gradle
RUN ./gradlew clean build -PskipTests

# Deploy the WAR file to WildFly
RUN cp build/libs/*.war $JBOSS_HOME/standalone/deployments/ && \
    echo "JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Djboss.bind.address=0.0.0.0 -Djboss.bind.address.management=0.0.0.0 -Dwildfly.as.deployment.ondemand=false ${JAVA_OPTS}" > $JBOSS_HOME/bin/standalone.conf

# Expose port 8380
EXPOSE 8380

CMD ["sh", "-c", "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Djboss.socket.binding.port-offset=200"]