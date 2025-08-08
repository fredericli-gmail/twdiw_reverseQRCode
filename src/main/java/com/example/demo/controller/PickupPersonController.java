package com.example.demo.controller;

// 引入 Spring 框架相關類別
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 引入服務層類別

// 引入 JSON 處理相關類別
import com.fasterxml.jackson.databind.ObjectMapper;

// 引入 Java 標準庫相關類別
import java.util.Map;
import java.util.HashMap;

// 引入日誌相關類別
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 引入圖片處理相關類別
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

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
    
    // 注入加解密服務
    @Autowired
    private ECCService eccService;

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
            String keyCode = request.get("keyCode");
            
            // 驗證所有必要參數是否存在
            if (phone == null || name == null || totpKey == null || hmacKey == null || rsaPublicKey == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "缺少必要參數");
                return ResponseEntity.badRequest().body(error);
            }
            
            // 如果沒有提供金鑰代碼，使用預設值
            if (keyCode == null || keyCode.trim().isEmpty()) {
                keyCode = "default";
            }
            
            // 產生 TOTP 碼
            String totp = totpService.generateTOTP(totpKey);
            
            // 準備明碼資料（不包含 HMAC）
            Map<String, String> plainData = new HashMap<>();
            plainData.put("phone", phone);
            plainData.put("name", name);
            plainData.put("totp", totp);
            
            // 將明碼資料轉換為 JSON 字串
            String plainDataJson = objectMapper.writeValueAsString(plainData);
            
            // 針對整個 JSON 資料計算 HMAC 值
            String hmac = hmacService.calculateHMAC(plainDataJson, hmacKey);
            
            // 使用 公鑰加密資料
            String encryptedData = eccService.encrypt(plainDataJson, rsaPublicKey);
            
            // 準備加密後的資料結構，包含 HMAC 欄位和金鑰代碼
            Map<String, String> encryptedResult = new HashMap<>();
            encryptedResult.put("t", "超商取貨");  // 設定資料類型
            encryptedResult.put("d", encryptedData);
            encryptedResult.put("h", hmac);  // 將 HMAC 值放在加密後的資料 JSON 中
            encryptedResult.put("k", keyCode);  // 將金鑰代碼放在加密後的資料 JSON 中
            
            // 準備回傳資料
            Map<String, Object> result = new HashMap<>();
            result.put("plainData", plainData);
            result.put("encryptedData", encryptedResult);

            // 產生 QR Code
            String encryptedResultJson = objectMapper.writeValueAsString(encryptedResult);
            
            // 註解掉標準 QR Code 生成
            // result.put("qrCode", qrCodeService.generateQRCodeBase64(encryptedResultJson));
            
            // 使用 TWDIW logo 生成 QR Code
            try {
                // 讀取 TWDIW logo 圖片
                ClassPathResource logoResource = new ClassPathResource("static/images/twdiw.png");
                BufferedImage logoImage = ImageIO.read(logoResource.getInputStream());
                
                // 使用帶 logo 的 QR Code 生成方法
                result.put("qrCode", qrCodeService.generateQRCodeWithLogoSpace(
                    encryptedResultJson, 
                    logoImage, 
                    400,  // QR Code 尺寸
                    400,  // QR Code 尺寸
                    60    // Logo 尺寸
                ));
            } catch (IOException e) {
                logger.error("讀取 TWDIW logo 失敗，使用標準 QR Code", e);
                // 如果讀取 logo 失敗，回退到標準 QR Code
                result.put("qrCode", qrCodeService.generateQRCodeBase64(encryptedResultJson));
            }
            
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