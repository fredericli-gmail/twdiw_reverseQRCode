# 變更日誌

本專案遵循 [語意化版本](https://semver.org/lang/zh-TW/) 進行版本控制。

## [0.0.3-SNAPSHOT] - 2025-06-18

### 變更
- 更新加密實作，從 BouncyCastle 改為使用 Java 標準加密函式庫
- 更新 ECC 加密規格，使用 X25519 和 ChaCha20-Poly1305
- 更新 Base64 編碼/解碼，使用 Java 原生的 Base64 實作
- 更新技術文件，修正加密演算法說明
- 更新 README.md，修正技術堆疊和安全性說明

### 安全性
- 更新 ECC 加密機制，使用 X25519 和 ChaCha20-Poly1305
- 新增 SHA-256 作為 KDF（金鑰衍生函數）
- 移除對 BouncyCastle 的依賴，改用 Java 標準加密函式庫
- 更新加密流程說明，加入 KDF 步驟

### 文件
- 更新 ECC 服務規格文件，修正加密演算法和流程說明
- 更新 README.md，修正技術堆疊和安全性說明
- 更新 API 文件，反映新的加密實作

### 效能
- 優化加密/解密效能，使用 Java 原生的加密實作
- 減少外部函式庫依賴，提升系統穩定性

## [0.0.2-SNAPSHOT] - 2025-06-16

### 變更
- 更新 README.md 文件，加入完整的專案說明
- 修正 TOTP 服務規格文件中的金鑰格式說明（從 Base32 改為 Base64）
- 更新專案名稱從「QR Code Offline 驗證系統」改為「QR Code 反向驗證系統」
- 更新加密方式從 RSA-2048 改為 ECC（Curve25519）
- 新增深色/淺色主題支援
- 新增響應式設計支援
- 新增技術文件連結
- 修正應用程式存取路徑（從 / 改為 /reverseqrcode）

### 文件
- 更新 README.md 文件結構和內容
- 更新 TOTP 服務規格文件
- 新增技術文件連結

### 安全性
- 更新加密機制說明
- 更新 TOTP 時間間隔說明（從 30 秒改為 60 秒）
- 更新金鑰格式說明（從 Base32 改為 Base64）

## [0.0.1-SNAPSHOT] - 2025-06-14

### 新增
- 初始專案設定
- 基本 QR Code 生成功能
- RSA-2048 加密/解密功能
- TOTP 驗證功能
- HMAC 驗證功能
- 取件人資料管理功能
- 管理員金鑰管理功能
- 基本網頁介面

### 安全性
- 實作 RSA-2048 非對稱加密
- 實作 TOTP 時間型一次性密碼
- 實作 HMAC 訊息驗證碼
- 實作資料完整性驗證

### 文件
- 新增 README.md
- 新增 LICENSE
- 新增 CONTRIBUTING.md
- 新增 CHANGELOG.md
- 新增 CODE_OF_CONDUCT.md
- 新增 SECURITY.md
- 新增 API 文件 
