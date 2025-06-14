package com.example.demo.controller;

// 引入 Spring 框架相關類別
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 引入服務層類別
import com.example.demo.service.RSA2048Service;
import com.example.demo.service.TOTPService;
import com.example.demo.service.HMACService;

// 引入 Java 標準庫相關類別
import java.util.Map;
import java.util.HashMap;

/**
 * 管理者金鑰控制器
 * 此控制器提供管理者相關的金鑰產生 API，包含：
 * 1. RSA 金鑰對產生
 * 2. TOTP 金鑰產生
 * 3. HMAC 金鑰產生
 */
@RestController
@RequestMapping("/api/admin")
public class AdminKeyController {

    // 注入 RSA-2048 加密服務
    @Autowired
    private RSA2048Service rsa2048Service;

    // 注入 TOTP 服務
    @Autowired
    private TOTPService totpService;

    // 注入 HMAC 服務
    @Autowired
    private HMACService hmacService;

    /**
     * 產生 RSA 金鑰對
     * 
     * @return 包含公鑰和私鑰的 Map，金鑰以 Base64 格式儲存
     */
    @PostMapping("/generate/rsa")
    public ResponseEntity<Map<String, String>> generateRSAKeys() {
        try {
            // 產生 RSA 金鑰對
            Map<String, String> keys = rsa2048Service.generateKeyPair();
            return ResponseEntity.ok(keys);
        } catch (Exception e) {
            // 處理錯誤情況
            Map<String, String> error = new HashMap<>();
            error.put("error", "RSA 金鑰產生失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 產生 TOTP 金鑰
     * 
     * @return 包含 TOTP 金鑰的 Map，金鑰以 Base64 格式儲存
     */
    @PostMapping("/generate/totp")
    public ResponseEntity<Map<String, String>> generateTOTPKey() {
        try {
            // 產生 TOTP 金鑰
            Map<String, String> key = totpService.generateTOTPKey();
            return ResponseEntity.ok(key);
        } catch (Exception e) {
            // 處理錯誤情況
            Map<String, String> error = new HashMap<>();
            error.put("error", "TOTP 金鑰產生失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 產生 HMAC 金鑰
     * 
     * @return 包含 HMAC 金鑰的 Map，金鑰以 Base64 格式儲存
     */
    @PostMapping("/generate/hmac")
    public ResponseEntity<Map<String, String>> generateHMACKey() {
        try {
            // 產生 HMAC 金鑰
            Map<String, String> key = hmacService.generateHMACKey();
            return ResponseEntity.ok(key);
        } catch (Exception e) {
            // 處理錯誤情況
            Map<String, String> error = new HashMap<>();
            error.put("error", "HMAC 金鑰產生失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 