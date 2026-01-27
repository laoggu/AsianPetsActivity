# 构建阶段
FROM eclipse-temurin:17-jdk-focal as builder
LABEL authors="14199"
WORKDIR /app
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src
RUN chmod +x ./gradlew
RUN ./gradlew build -x verify -x test --no-daemon

# 运行阶段
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
