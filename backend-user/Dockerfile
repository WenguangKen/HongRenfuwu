# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY backend-user/pom.xml .
COPY backend-user/src ./src

# Install Maven
RUN apk add --no-cache maven

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add application user
RUN addgroup -S athluna && adduser -S athluna -G athluna

# Copy the built artifact
COPY --from=build /app/target/athluna-kms-backend-1.0.0-exec.jar app.jar

# Set ownership
RUN chown -R athluna:athluna /app

USER athluna

# JVM configuration
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
