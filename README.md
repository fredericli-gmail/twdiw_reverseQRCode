# QR Code 反向驗證系統

這是一個基於 Spring Boot 的 QR Code 驗證系統，用於產生和驗證加密的 QR Code。系統提供了安全的資料傳輸機制，包含多重驗證層級。

## 功能特點

- QR Code 產生與驗證
- ECC（X25519）加密/解密
- TOTP（基於時間的一次性密碼）驗證
- HMAC 資料完整性驗證
- 完整的 Web 介面
- 即時驗證回饋
- 支援深色/淺色主題
- 響應式設計

## 技術堆疊

- Java 17
- Spring Boot 3.2.3
- Thymeleaf
- ZXing（QR Code 處理）
- Bootstrap（前端框架）
- JavaScript（前端互動）
- Java 標準加密函式庫

## 系統需求

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- 現代網頁瀏覽器（支援 ES6+）

## 快速開始

1. 複製專案
```bash
git clone https://github.com/fredericli-gmail/twdiw_reverseQRCode.git
cd twdiw_reverseQRCode
```

2. 編譯專案
```bash
mvn clean package
```

3. 執行應用程式
```bash
java -jar target/reverse-code-0.0.1-SNAPSHOT.jar
```

4. 開啟瀏覽器訪問
```
http://localhost:8080/reverseqrcode
```

## 使用說明

### 產生 QR Code

1. 在「產生超商資料」頁面中輸入必要資訊
2. 系統會自動產生加密的 QR Code
3. 同時顯示明碼和加密後的 JSON 資料

### 驗證 QR Code

1. 在「驗證 QR Code」頁面中貼上 QR Code 內容
2. 系統會進行以下驗證：
   - ECC 解密
   - TOTP 時間驗證
   - HMAC 完整性驗證
3. 驗證結果會即時顯示在畫面上

## 安全性說明

本系統採用多重安全機制：

1. ECC（X25519）非對稱加密
   - 使用 Curve25519 橢圓曲線
   - 提供 128 位元的安全性
   - 使用 ChaCha20-Poly1305 進行對稱加密
   - 使用 SHA-256 作為 KDF

2. TOTP 時間驗證
   - 使用 HMAC-SHA256 演算法
   - 6 位數密碼輸出
   - 60 秒時間間隔
   - 允許 30 秒時間偏移

3. HMAC 資料完整性驗證
   - 使用 SHA-256 雜湊演算法
   - 256 位元金鑰長度
   - Base64 編碼輸出

4. 所有敏感資料都經過加密處理

## 開發者資訊

### 專案結構

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── demo/
│   │               ├── controller/
│   │               ├── service/
│   │               └── DemoApplication.java
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   └── js/
│       └── templates/
└── test/
```

### API 端點

- POST `/api/pickup-person/generate` - 產生 QR Code
- POST `/api/verify-qrcode` - 驗證 QR Code

### 技術文件

- [ECC 服務規格](docs/ECC_Service_Specification.md)
- [TOTP 服務規格](docs/TOTP_Service_Specification.md)
- [HMAC 服務規格](docs/HMAC_Service_Specification.md)

## Docker 支援

### 使用 Docker 執行

1. 建置 Docker 映像
```bash
docker build -t reverseqrcode .
```

2. 執行容器
```bash
docker run -p 8080:8080 reverseqrcode
```

### 使用 Docker Compose

```bash
docker-compose up -d
```

## 授權條款

本專案採用 MIT 授權條款 - 詳見 [LICENSE](LICENSE) 檔案

## 貢獻指南

1. Fork 本專案
2. 建立您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的變更 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 開啟一個 Pull Request

## 聯絡方式

如有任何問題或建議，請開啟 Issue 或 Pull Request。

## 變更日誌

詳見 [CHANGELOG.md](CHANGELOG.md) 檔案。 