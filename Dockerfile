### ===== Stage 1: BUILD =====
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

COPY . .

# 🔑 используем gradle из образа
RUN gradle clean build -x test --no-daemon

### ===== Stage 2: RUNTIME =====
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
