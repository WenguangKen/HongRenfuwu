# Build stage - install shared dependency and build the service
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /application

# Install Maven
RUN apk add --no-cache maven

# Copy plugin artifacts if any (optimization)
# COPY backend-user/pom.xml /application/backend-user/pom.xml
# COPY backend-shopify-integration/pom.xml /application/backend-shopify-integration/pom.xml

# Copy backend-user source and build/install it
COPY backend-user /application/backend-user
WORKDIR /application/backend-user
# Skip tests to speed up
RUN mvn clean install -DskipTests

# Now build the shopify service
WORKDIR /application/backend-shopify-integration
COPY backend-shopify-integration /application/backend-shopify-integration
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add application user
RUN addgroup -S athluna && adduser -S athluna -G athluna

# Copy the built artifact via temporary directory to handle naming variations
COPY --from=build /application/backend-shopify-integration/target /tmp/target
RUN find /tmp/target -name "*.jar" ! -name "*.original" ! -name "*-sources.jar" ! -name "*-javadoc.jar" -exec cp {} app.jar \;

# Set ownership
RUN chown -R athluna:athluna /app

USER athluna

# JVM configuration
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/heapdump.hprof"
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/api/shopify/actuator/health || exit 1

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
