#!/bin/bash

# QR Code 反向驗證系統 Docker 建置腳本

# 設定變數
IMAGE_NAME="twdiw-reverseqrcode"
IMAGE_TAG="latest"
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"
OUTPUT_DIR="docker-images"
OUTPUT_FILE="${OUTPUT_DIR}/${IMAGE_NAME}-${IMAGE_TAG}.tar"

# 顏色設定
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 印出訊息的函式
print_message() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

print_error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] 錯誤：${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] 警告：${NC} $1"
}

# 檢查 Docker 是否安裝
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安裝，請先安裝 Docker"
        exit 1
    fi
    print_message "Docker 已安裝"
}

# 檢查 Maven 是否安裝
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven 未安裝，請先安裝 Maven"
        print_message "在 macOS 上可以使用：brew install maven"
        print_message "在 Ubuntu/Debian 上可以使用：sudo apt-get install maven"
        exit 1
    fi
    print_message "Maven 已安裝"
}

# 清理舊的建置產物
clean_old_builds() {
    print_message "清理舊的建置產物..."
    if [ -d "target" ]; then
        rm -rf target
    fi
}

# 使用 Maven 編譯 JAR 檔案
build_jar_with_maven() {
    print_message "開始使用 Maven 編譯 JAR 檔案..."
    
    # 檢查是否有 pom.xml
    if [ ! -f "pom.xml" ]; then
        print_error "找不到 pom.xml 檔案"
        exit 1
    fi
    
    # 執行 Maven 編譯，跳過測試案例
    print_message "執行 Maven clean package（跳過測試）..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        print_message "Maven 編譯成功"
        # 顯示產生的 JAR 檔案
        if [ -d "target" ]; then
            print_message "產生的 JAR 檔案："
            ls -lh target/*.jar 2>/dev/null | while read line; do
                echo "  $line"
            done
        fi
    else
        print_error "Maven 編譯失敗"
        exit 1
    fi
}


# 建立輸出目錄
create_output_dir() {
    if [ ! -d "$OUTPUT_DIR" ]; then
        print_message "建立輸出目錄：$OUTPUT_DIR"
        mkdir -p "$OUTPUT_DIR"
    fi
}

# 建置 Docker 映像檔
build_docker_image() {
    print_message "開始建置 Docker 映像檔（使用本地編譯的 JAR 檔案）..."
    
    # 檢查是否有編譯好的 JAR 檔案
    if [ ! -f "target/reverse-code-0.0.4-SNAPSHOT.jar" ]; then
        print_error "找不到編譯好的 JAR 檔案"
        print_error "請確認 Maven 編譯成功"
        exit 1
    fi
    
    # 檢查是否指定為 ARM 架構（樹莓派）
    if [ "$1" == "--arm" ] || [ "$1" == "--arm64" ]; then
        print_message "建置 ARM64 架構的映像檔（適用於樹莓派）"
        # --no-cache 確保不使用快取
        docker buildx build --no-cache --platform linux/arm64 -t "$FULL_IMAGE_NAME" --load .
    else
        print_message "建置 x64/amd64 架構的映像檔（預設）"
        # 明確指定 linux/amd64 平台以確保在任何環境都建置 x64 版本
        # --no-cache 確保不使用快取
        docker buildx build --no-cache --platform linux/amd64 -t "$FULL_IMAGE_NAME" --load .
    fi
    
    if [ $? -eq 0 ]; then
        print_message "Docker 映像檔建置成功：$FULL_IMAGE_NAME"
    else
        print_error "Docker 映像檔建置失敗"
        exit 1
    fi
}

# 儲存 Docker 映像檔
save_docker_image() {
    print_message "儲存 Docker 映像檔到：$OUTPUT_FILE"
    
    # 如果檔案已存在，先備份
    if [ -f "$OUTPUT_FILE" ]; then
        BACKUP_FILE="${OUTPUT_FILE}.backup-$(date +'%Y%m%d%H%M%S')"
        print_warning "檔案已存在，備份到：$BACKUP_FILE"
        mv "$OUTPUT_FILE" "$BACKUP_FILE"
    fi
    
    # 儲存映像檔
    docker save -o "$OUTPUT_FILE" "$FULL_IMAGE_NAME"
    
    if [ $? -eq 0 ]; then
        # 計算檔案大小
        FILE_SIZE=$(du -h "$OUTPUT_FILE" | cut -f1)
        print_message "映像檔已儲存，大小：$FILE_SIZE"
        
        # 壓縮映像檔以節省空間
        print_message "壓縮映像檔..."
        gzip -c "$OUTPUT_FILE" > "${OUTPUT_FILE}.gz"
        COMPRESSED_SIZE=$(du -h "${OUTPUT_FILE}.gz" | cut -f1)
        print_message "壓縮後大小：$COMPRESSED_SIZE"
    else
        print_error "儲存映像檔失敗"
        exit 1
    fi
}

# 顯示使用說明
show_usage() {
    echo "使用方式："
    echo "  ./build-docker.sh [選項]"
    echo ""
    echo "選項："
    echo "  --arm, --arm64    建置 ARM64 架構的映像檔（適用於樹莓派）"
    echo "  --help            顯示此說明訊息"
    echo ""
    echo "範例："
    echo "  ./build-docker.sh          # 建置 x64/amd64 架構（預設）"
    echo "  ./build-docker.sh --arm    # 建置 ARM64 架構（樹莓派）"
}

# 主程式
main() {
    print_message "====== QR Code 反向驗證系統 Docker 建置開始 ======"
    
    # 檢查參數
    if [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
        show_usage
        exit 0
    fi
    
    # 執行檢查
    check_docker
    check_maven
    
    # 清理舊的建置產物（確保每次都重新編譯）
    clean_old_builds
    
    # 使用 Maven 編譯 JAR 檔案
    build_jar_with_maven
    
    # 建立輸出目錄
    create_output_dir
    
    # 建置 Docker 映像檔
    build_docker_image "$1"
    
    # 儲存映像檔
    save_docker_image
    
    print_message "====== 建置完成 ======"
    print_message "映像檔位置："
    print_message "  原始檔案：$OUTPUT_FILE"
    print_message "  壓縮檔案：${OUTPUT_FILE}.gz"
    print_message ""
    print_message "在樹莓派上載入映像檔："
    print_message "  docker load -i ${OUTPUT_FILE}"
    print_message "或使用壓縮版本："
    print_message "  gunzip -c ${OUTPUT_FILE}.gz | docker load"
    print_message ""
    print_message "執行容器："
    print_message "  docker run -d -p 8080:8080 --name reverseqrcode $FULL_IMAGE_NAME"
}

# 執行主程式
main "$@"