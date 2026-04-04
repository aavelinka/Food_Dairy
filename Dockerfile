FROM maven:3.9.13-eclipse-temurin-25-alpine AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
COPY checkstyle.xml ./
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:25-alpine

WORKDIR /app

RUN addgroup -S spring \
    && adduser -S spring -G spring \
    && mkdir -p /app/logs/archive \
    && chown -R spring:spring /app

COPY --from=build /app/target/project-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
ENV LOG_DIR=/app/logs

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider "http://127.0.0.1:${PORT}/actuator/health" || exit 1

USER spring

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
