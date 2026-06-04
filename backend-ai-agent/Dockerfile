FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/athluna-ai-agent-1.0.0.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
