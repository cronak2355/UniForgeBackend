FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /workspace/app

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/libs
COPY --from=build ${DEPENDENCY}/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
