################################
# 1. Build stage
################################
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle 캐시 최적화
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon


################################
# 2. Run stage
################################
FROM eclipse-temurin:17-jre
WORKDIR /app

# non-root 유저
RUN useradd -m spring
USER spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
