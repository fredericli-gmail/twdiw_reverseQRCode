# ECC 加密規格說明文件

## 1. 概述

本文件描述使用 Curve25519 橢圓曲線進行非對稱加密的實作規格。此加密方案結合了 ECDH（橢圓曲線迪菲-赫爾曼金鑰交換）和 AES 加密，提供安全的資料傳輸機制。

## 2. 技術規格

### 2.1 使用的演算法
- 橢圓曲線：Curve25519（256 位元）
- 金鑰交換：ECDH
- 對稱加密：AES-256-CBC with PKCS5Padding
- 編碼方式：Base64

### 2.2 金鑰格式

#### 2.2.1 公鑰格式
- 使用壓縮格式的橢圓曲線點座標
- 長度：33 bytes（包含壓縮標記）
- 編碼：Base64

#### 2.2.2 私鑰格式
- 使用 PKCS#8 格式
- 編碼：Base64

## 3. 加密流程

### 3.1 金鑰對產生
1. 使用 Curve25519 產生金鑰對
2. 公鑰轉換為壓縮格式的點座標（33 bytes）
3. 將公鑰和私鑰分別進行 Base64 編碼
4. 回傳格式：`[Base64(公鑰), Base64(私鑰)]`

### 3.2 加密過程
1. 產生臨時金鑰對（使用 Curve25519）
2. 使用 ECDH 與接收者公鑰產生共享金鑰（32 bytes）
3. 產生 16 bytes 隨機 IV
4. 使用 AES-256-CBC 加密原始資料
5. 組合資料：`[壓縮公鑰(33 bytes) + IV(16 bytes) + 加密資料]`
6. 將組合後的資料進行 Base64 編碼

### 3.3 解密過程
1. Base64 解碼加密資料
2. 分離資料：
   - 前 33 bytes：壓縮公鑰
   - 接下來 16 bytes：IV
   - 剩餘部分：加密資料
3. 使用 ECDH 與私鑰重建共享金鑰（32 bytes）
4. 使用 AES-256-CBC 解密資料

## 4. 資料格式

### 4.1 金鑰對格式
```json
{
    "publicKey": "Base64編碼的公鑰字串",
    "privateKey": "Base64編碼的私鑰字串"
}
```

### 4.2 加密資料格式
```
Base64(壓縮公鑰(33 bytes) + IV(16 bytes) + AES加密資料)
```

## 5. 實作注意事項

### 5.1 安全性考量
- 每次加密都使用新的臨時金鑰對
- 使用隨機 IV 確保相同明文產生不同密文
- 使用 CBC 模式提供更好的安全性
- 使用 Curve25519 提供 128 位元的安全性
- 使用 AES-256 提供 256 位元的安全性

### 5.2 效能考量
- 使用壓縮格式的公鑰減少資料長度
- 使用 Base64 編碼確保資料可以安全傳輸
- 使用 Curve25519 提供高效的橢圓曲線運算

### 5.3 錯誤處理
- 金鑰格式錯誤時應拋出 InvalidKeySpecException
- 加密/解密失敗時應拋出相應的加密異常

## 6. 範例

### 6.1 金鑰對產生
```java
String[] keyPair = generateKeyPair();
String publicKey = keyPair[0];  // Base64 編碼的公鑰
String privateKey = keyPair[1]; // Base64 編碼的私鑰
```

### 6.2 加密
```java
String plaintext = "要加密的文字";
String encrypted = encrypt(plaintext, publicKey);
```

### 6.3 解密
```java
String decrypted = decrypt(encrypted, privateKey);
```

## 7. 相容性說明

### 7.1 支援的程式語言
- Java
- Python
- Node.js
- Go
- C#

### 7.2 必要的函式庫
- BouncyCastle 或相容的加密函式庫
- Base64 編碼/解碼功能
- AES-256-CBC 加密支援

## 8. 測試建議

建議實作時進行以下測試：
1. 金鑰對產生測試
2. 基本加密/解密測試
3. 特殊字元測試
4. 長文字測試
5. 錯誤處理測試

## 9. 參考資源

- [Curve25519 規格](https://cr.yp.to/ecdh/curve25519-20060209.pdf)
- [ECDH 標準](https://tools.ietf.org/html/rfc7748)
- [AES 標準](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.197.pdf) 