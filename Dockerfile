### ===== Stage 1: BUILD (Debian-based Gradle) =====
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

COPY . .
RUN ./gradlew clean build -x test --no-daemon

### ===== Stage 2: RUNTIME (Alpine + minimal JRE) =====
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# копируем собранный jar из предыдущего stage
COPY --from=builder /app/build/libs/*.jar app.jar

# включаем prod профиль Spring
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
