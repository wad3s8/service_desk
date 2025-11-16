FROM eclipse-temurin:21-jdk-alpine

# <-- ВАЖНО: для Gradle путь другой
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app.jar"]