# Dockerfile for optimized deployment
# Assumes build/libs/*.jar exists (built by CI pipeline)

# Switch to Debian-based image to support java.awt (BufferedImage) without extra config
FROM eclipse-temurin:17-jre

WORKDIR /app

# Ensure we have a place for logs/temp files
VOLUME /tmp

# Copy the built JAR file from the context (which will be populated by CI)
COPY build/libs/*.jar app.jar

ARG COMMIT_HASH
ENV COMMIT_HASH=${COMMIT_HASH}

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
