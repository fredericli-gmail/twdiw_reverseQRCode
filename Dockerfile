# 使用輕量級的 JRE，明確指定 Linux x64 平台
FROM --platform=linux/amd64 eclipse-temurin:17-jre

# 安裝必要的系統套件（為了支援字型渲染，QR Code 生成可能需要）
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig \
    fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

# 設定工作目錄
WORKDIR /app

# 從本地複製已經在外部編譯好的 JAR 檔案
# 注意：build-docker.sh 會先執行 mvn clean package -DskipTests
COPY target/reverse-code-0.0.4-SNAPSHOT.jar app.jar

# 建立日誌目錄
RUN mkdir -p logs

# 暴露應用程式埠
EXPOSE 8080

# 設定 JVM 參數以優化 Linux x64 記憶體使用
ENV JAVA_OPTS="-Xms256m -Xmx1g -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"

# 啟動應用程式
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]