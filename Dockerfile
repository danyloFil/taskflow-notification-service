FROM gradle:9.2.1-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/notification-service.jar

EXPOSE 8083
CMD ["java", "-jar", "/app/notification-service.jar"]
