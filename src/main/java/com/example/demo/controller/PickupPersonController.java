package com.example.demo.controller;

// 引入 Spring 框架相關類別
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 引入服務層類別
import com.example.demo.service.PickupPersonService;
import com.example.demo.service.TOTPService;
import com.example.demo.service.HMACService;
import com.example.demo.service.QRCodeService;
import com.example.demo.service.RSA2048Service;

// 引入 JSON 處理相關類別
import com.fasterxml.jackson.databind.ObjectMapper;

// 引入 Java 標準庫相關類別
import java.util.Map;
import java.util.HashMap;

// 引入日誌相關類別
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 取件人資料控制器
 * 此控制器負責處理取件人資料的產生和加密，包含：
 * 1. 產生 TOTP 碼
 * 2. 計算 HMAC 值
 * 3. RSA 加密
 * 4. 產生 QR Code
 */
@RestController
@RequestMapping("/api/pickup-person")
public class PickupPersonController {
    
    // 設定日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(PickupPersonController.class);
    
    // 建立 JSON 處理器
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 注入 TOTP 服務
    @Autowired
    private TOTPService totpService;
    
    // 注入 HMAC 服務
    @Autowired
    private HMACService hmacService;
    
    // 注入 RSA-2048 加密服務
    @Autowired
    private RSA2048Service rsa2048Service;

    // 注入 QR Code 服務
    @Autowired
    private QRCodeService qrCodeService;
    
    /**
     * 產生取件人資料並進行加密
     * 
     * @param request 包含取件人資料和金鑰的請求
     * @return 包含明碼資料、加密資料和 QR Code 的回應
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generatePickupPersonData(@RequestBody Map<String, String> request) {
        try {
            // 從請求中取得必要參數
            String phone = request.get("phone");
            String name = request.get("name");
            String totpKey = request.get("totpKey");
            String hmacKey = request.get("hmacKey");
            String rsaPublicKey = request.get("rsaPublicKey");
            
            // 驗證所有必要參數是否存在
            if (phone == null || name == null || totpKey == null || hmacKey == null || rsaPublicKey == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "缺少必要參數");
                return ResponseEntity.badRequest().body(error);
            }
            
            // 產生 TOTP 碼
            String totp = totpService.generateTOTP(totpKey);
            
            // 使用 TOTP 和姓名計算 HMAC 值
            String hmac = hmacService.calculateHMAC(totp+name, hmacKey);
            
            // 準備明碼資料
            Map<String, String> plainData = new HashMap<>();
            plainData.put("phone", phone);
            plainData.put("name", name);
            plainData.put("totp", totp);
            plainData.put("hmac", hmac);
            
            // 將明碼資料轉換為 JSON 字串
            String plainDataJson = objectMapper.writeValueAsString(plainData);
            
            // 使用 RSA 公鑰加密資料
            String encryptedData = rsa2048Service.encrypt(plainDataJson, rsaPublicKey);
            
            // 準備加密後的資料結構
            Map<String, String> encryptedResult = new HashMap<>();
            encryptedResult.put("T", "A");  // 設定資料類型
            encryptedResult.put("DATA", encryptedData);
            
            // 準備回傳資料
            Map<String, Object> result = new HashMap<>();
            result.put("plainData", plainData);
            result.put("encryptedData", encryptedResult);

            // 產生 QR Code
            String encryptedResultJson = objectMapper.writeValueAsString(encryptedResult);
            result.put("qrCode", qrCodeService.generateQRCodeBase64(encryptedResultJson));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // 記錄錯誤並回傳錯誤訊息
            logger.error("產生資料時發生錯誤", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "資料產生失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 