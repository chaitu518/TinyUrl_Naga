# ---------- BUILD STAGE ----------
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Download dependencies first (better caching)
RUN ./gradlew dependencies || true

COPY src src

RUN ./gradlew clean bootJar -x test

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
