# 使用範例

本目錄包含 QR Code 反向驗證系統的使用範例。

## 目錄結構

```
examples/
├── README.md
├── curl/              # curl 命令列範例
├── java/             # Java 程式碼範例
└── postman/          # Postman 集合
```

## 範例說明

### curl 範例

curl 範例展示如何使用命令列工具呼叫 API：

1. 驗證 QR Code
2. 生成取件人資料
3. 生成管理員金鑰

### Java 範例

Java 範例展示如何在 Java 程式中使用 API：

1. 使用 Spring RestTemplate
2. 使用 OkHttp
3. 使用 WebClient

### Postman 範例

Postman 範例包含完整的 API 集合，可以直接匯入 Postman 使用：

1. 環境變數設定
2. 請求範例
3. 測試腳本

## 使用方式

1. 選擇適合的範例
2. 複製範例程式碼
3. 修改必要的參數
4. 執行程式碼

## 注意事項

1. 範例中的金鑰僅供測試使用
2. 請勿在生產環境使用範例中的金鑰
3. 請確保使用安全的網路連線
4. 請妥善保管金鑰和密碼 