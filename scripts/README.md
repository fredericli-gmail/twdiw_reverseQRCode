# 常用腳本

本目錄包含 QR Code 反向驗證系統的常用腳本。

## 目錄結構

```
scripts/
├── README.md
├── build.sh          # 建置腳本
├── run.sh           # 執行腳本
├── test.sh          # 測試腳本
└── docker/          # Docker 相關腳本
```

## 腳本說明

### build.sh

用於建置專案：

```bash
./build.sh
```

功能：
1. 清理舊的建置檔案
2. 執行 Maven 建置
3. 執行單元測試
4. 產生建置報告

### run.sh

用於執行專案：

```bash
./run.sh
```

功能：
1. 檢查環境設定
2. 啟動應用程式
3. 監控應用程式狀態
4. 記錄執行日誌

### test.sh

用於執行測試：

```bash
./test.sh
```

功能：
1. 執行單元測試
2. 執行整合測試
3. 產生測試報告
4. 檢查測試覆蓋率

### Docker 腳本

Docker 相關腳本位於 `docker` 目錄：

1. `build-docker.sh`：建置 Docker 映像
2. `run-docker.sh`：執行 Docker 容器
3. `docker-compose.yml`：Docker Compose 設定

## 使用方式

1. 確保腳本具有執行權限：
   ```bash
   chmod +x *.sh
   ```

2. 執行需要的腳本：
   ```bash
   ./build.sh
   ./run.sh
   ./test.sh
   ```

## 注意事項

1. 請確保已安裝必要的工具：
   - Maven
   - Java
   - Docker（如果使用 Docker 腳本）

2. 請確保環境變數已正確設定：
   - JAVA_HOME
   - MAVEN_HOME
   - DOCKER_HOME（如果使用 Docker）

3. 請確保有足夠的權限執行腳本

4. 請定期備份重要資料 