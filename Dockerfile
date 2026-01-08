FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /workspace/app

COPY build.gradle.kts .
COPY settings.gradle.kts .
<<<<<<< HEAD
=======

# Download dependencies first (cached layer)
RUN gradle dependencies --no-daemon || true

>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
COPY src src

RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/libs
COPY --from=build ${DEPENDENCY}/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
