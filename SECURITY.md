# 安全性政策

## 支援的版本

我們目前支援以下版本的安全性更新：

| 版本 | 支援狀態 |
|------|----------|
| 0.0.1-SNAPSHOT | :white_check_mark: 支援 |

## 回報安全性問題

如果您發現了安全性問題，請不要公開回報。相反地，請透過以下方式聯繫我們：

1. 發送電子郵件至 [frederic62.li@gmail.com](mailto:frederic62.li@gmail.com)
2. 在 GitHub 上建立私密的 security advisory

請在回報中包含以下資訊：
- 問題的詳細描述
- 重現步驟
- 可能的影響
- 建議的修復方式（如果有的話）

## 安全性更新

我們承諾：
- 在收到安全性問題回報後 48 小時內進行初步回應
- 定期進行安全性審查
- 及時修復發現的安全性問題
- 在修復後發布安全性更新

## 安全性最佳實踐

使用本系統時，請遵循以下安全性最佳實踐：

1. 定期更新系統到最新版本
2. 妥善保管金鑰和密碼
3. 使用安全的網路連線
4. 定期備份重要資料
5. 遵循最小權限原則

## 安全性功能

本系統實作了以下安全性功能：

1. RSA-2048 非對稱加密
2. TOTP 時間型一次性密碼
3. HMAC 訊息驗證碼
4. 資料完整性驗證
5. 安全的金鑰管理

## 安全性審查

我們定期進行以下安全性審查：

1. 程式碼安全性審查
2. 相依套件安全性審查
3. 系統架構安全性審查
4. 滲透測試
5. 弱點掃描 
