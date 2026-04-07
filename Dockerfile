# 第一階段：編譯階段 (Build Stage)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
# 只複製 pom.xml 先下載依賴（利用 Docker 快取層級，加速後續打包）
COPY pom.xml .
RUN mvn dependency:go-offline
# 複製原始碼並打包
COPY src ./src
RUN mvn clean package -DskipTests

# 第二階段：執行階段 (Run Stage)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 從編譯階段複製 jar 檔過來
COPY --from=build /app/target/*.jar app.jar
# 執行 Java
ENTRYPOINT ["java", "-jar", "app.jar"]