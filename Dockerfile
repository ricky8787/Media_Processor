FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 直接從 GitHub Runner 的環境中複製剛剛編譯好的 jar
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]