# === Build Stage ===
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory for the build process
WORKDIR /app

# Copy the Maven project files to leverage Docker's layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests
# 如果上下文里只有 src 文件夹（没有 pom.xml），可以直接 copy *
COPY src ./src   

# Build the Spring Boot application using Maven
RUN mvn clean package -DskipTests

# === Runtime Stage ===
FROM eclipse-temurin:17-jre-alpine

# Set the working directory for the application
WORKDIR /app

# 复制构建好的 jar 包到运行镜像
# 假设生成的 jar 在 target/ 下，名字为 line-login-starter-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/line-login-starter.jar app.jar

# 暴露端口（Spring Boot 默认 8080，可改成你项目端口）
EXPOSE 8080

# 启动 Spring Boot 应用
CMD ["java", "-jar", "app.jar"]