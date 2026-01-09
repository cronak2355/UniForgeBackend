# Dockerfile for optimized deployment
# Assumes build/libs/*.jar exists (built by CI pipeline)

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Ensure we have a place for logs/temp files
VOLUME /tmp

# Copy the built JAR file from the context (which will be populated by CI)
COPY build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
