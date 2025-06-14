# API 文件

## 概述

本文件說明 QR Code 反向驗證系統的 API 端點。所有 API 都遵循 RESTful 設計原則，並使用 JSON 格式進行資料交換。

## 基礎 URL

```
http://localhost:8080/api
```

## 認證

目前 API 不需要認證。

## API 端點

### 1. 驗證 QR Code

#### 請求

```http
POST /verify-qrcode
```

#### 請求內容

```json
{
    "encryptedData": "加密後的資料",
    "privateKey": "RSA 私鑰",
    "totpKey": "TOTP 金鑰",
    "hmacKey": "HMAC 金鑰"
}
```

#### 回應

```json
{
    "message": "驗證結果訊息",
    "isValid": true,
    "data": "解密後的資料"
}
```

### 2. 生成取件人資料

#### 請求

```http
POST /generate-pickup-person
```

#### 請求內容

```json
{
    "name": "取件人姓名",
    "phone": "取件人電話",
    "totpKey": "TOTP 金鑰",
    "hmacKey": "HMAC 金鑰",
    "publicKey": "RSA 公鑰"
}
```

#### 回應

```json
{
    "message": "生成結果訊息",
    "encryptedData": "加密後的資料",
    "qrCode": "Base64 編碼的 QR Code 圖片"
}
```

### 3. 生成管理員金鑰

#### 請求

```http
POST /admin/generate-keys
```

#### 請求內容

```json
{
    "keyType": "RSA|TOTP|HMAC"
}
```

#### 回應

```json
{
    "message": "生成結果訊息",
    "key": "生成的金鑰"
}
```

## 錯誤處理

所有 API 在發生錯誤時都會回傳適當的 HTTP 狀態碼和錯誤訊息：

```json
{
    "message": "錯誤訊息",
    "isValid": false,
    "data": null
}
```

常見的 HTTP 狀態碼：
- 200：成功
- 400：請求格式錯誤
- 500：伺服器內部錯誤

## 安全性考量

1. 所有敏感資料都使用 RSA-2048 加密
2. 使用 TOTP 進行時間驗證
3. 使用 HMAC 確保資料完整性
4. 建議使用 HTTPS 進行通訊

## 使用範例

### 驗證 QR Code

```bash
curl -X POST http://localhost:8080/api/verify-qrcode \
  -H "Content-Type: application/json" \
  -d '{
    "encryptedData": "加密後的資料",
    "privateKey": "RSA 私鑰",
    "totpKey": "TOTP 金鑰",
    "hmacKey": "HMAC 金鑰"
  }'
```

### 生成取件人資料

```bash
curl -X POST http://localhost:8080/api/generate-pickup-person \
  -H "Content-Type: application/json" \
  -d '{
    "name": "取件人姓名",
    "phone": "取件人電話",
    "totpKey": "TOTP 金鑰",
    "hmacKey": "HMAC 金鑰",
    "publicKey": "RSA 公鑰"
  }'
```

### 生成管理員金鑰

```bash
curl -X POST http://localhost:8080/api/admin/generate-keys \
  -H "Content-Type: application/json" \
  -d '{
    "keyType": "RSA"
  }'
``` 