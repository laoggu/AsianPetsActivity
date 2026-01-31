# 构建阶段
FROM eclipse-temurin:17-jdk-focal as builder
LABEL authors="14199"
LABEL description="Asian Pets Association System"

WORKDIR /app

# 复制Gradle包装器
COPY gradlew .
COPY gradle ./gradle
RUN chmod +x ./gradlew

# 使用国内镜像加速
RUN sed -i 's|https\\://services.gradle.org/distributions/|https\\://mirrors.cloud.tencent.com/gradle/|g' gradle/wrapper/gradle-wrapper.properties

# 复制构建文件
COPY build.gradle .
COPY settings.gradle .

# 下载依赖（利用Docker层缓存）
RUN ./gradlew dependencies --no-daemon

# 复制源代码
COPY src ./src

# 构建应用
RUN ./gradlew build -x test --no-daemon

# 运行阶段
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# 创建非root用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 复制构建好的jar文件
COPY --from=builder /app/build/libs/*.jar app.jar

# 设置权限
RUN chown appuser:appuser app.jar

# 切换到非root用户
USER appuser

# JVM优化参数
ENV JAVA_OPTS="-Xms256m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError"

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]