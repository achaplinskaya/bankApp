# build stage
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -Dmaven.test.skip

#app package stage
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
COPY src/main/resources /liquibase-scripts
CMD ["java", "-Xms64m", "-Xmx64m", "-jar", "/app/app.jar"]
