# 變更日誌

本專案遵循 [語意化版本](https://semver.org/lang/zh-TW/) 進行版本控制。

## [0.0.5-SNAPSHOT] - 2025-10-14

### 新增
- 動態欄位輸入功能：支援使用者自訂任意 Key-Value 欄位
- 前端動態欄位介面：可新增、刪除、編輯欄位
- 動態欄位前端驗證：檢查欄位名稱格式（只允許英數字和底線）、值的長度（最多 500 字元）
- 動態欄位後端驗證：確保資料完整性和安全性

### 變更
- 移除固定的 phone 和 name 欄位，改為完全動態化
- 重構 `PickupPersonController.java`：支援動態欄位的接收和驗證
- 重構前端 `home.html` 和 `pickup-person.js`：實作動態欄位 UI 和互動邏輯
- 更新 CSS 樣式：支援動態欄位的顯示和操作

### 文件
- 更新相關技術文件，反映動態欄位的設計和實作

### 程式碼品質
- 所有新增程式碼都加上繁體中文註解
- 保持原有的加密、HMAC、QR Code 產生邏輯不變

## [0.0.4-SNAPSHOT] - 2025-09-05

### 新增
- Docker 容器化支援
  - 新增 `Dockerfile` 用於建置 Docker 映像
  - 新增 `docker-compose.yml` 用於容器編排
  - 設定容器健康檢查機制
  - 設定資源限制（記憶體和 CPU）
- QR Code 帶 Logo 功能
  - 實作非破壞性的 QR Code Logo 嵌入
  - 支援在 QR Code 中央嵌入 TWDIW logo
  - 自動調整 logo 尺寸以確保 QR Code 可讀性

### 變更
- HMAC 計算方式重大調整
  - 改為計算整個解密後的 JSON 資料（而非個別欄位）
  - HMAC 值 (h) 移至 QR Code 最外層 JSON 結構
  - 更新驗證流程：先解密 (d)，再驗證 HMAC (h)
- 移除領貨人資料的欄位驗證（考量情境通用性）
- 調整加密資料格式為小寫欄位名稱
- 移除部分中文說明註解

### 安全性
- 強化 HMAC 驗證機制：對整個資料結構進行驗證
- 確保資料完整性：任何資料篡改都會導致 HMAC 驗證失敗

### 文件
- 更新 QR Code 驗證規格說明文件
- 補充 HMAC 欄位說明
- 更新加密格式和驗證流程文件
- 新增 Docker 使用說明

### 協作
- 合併 PR #1: QR Code with logo 功能（來自 @cht-chrischang）
- 合併 PR #3: HMAC 計算方式調整（來自 @chiahsichang）
- 合併 PR #4: HMAC 驗證流程優化（來自 @chiahsichang）

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
