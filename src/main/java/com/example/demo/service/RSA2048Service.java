package com.example.demo.service;

// 引入 Spring 框架相關類別
import org.springframework.stereotype.Service;

// 引入 Java 加密相關類別
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * RSA-2048 加密服務
 * 此服務類別提供 RSA-2048 非對稱加密的相關功能，包含：
 * 1. 產生金鑰對
 * 2. 使用公鑰加密
 * 3. 使用私鑰解密
 */
@Service
public class RSA2048Service {
    
    /**
     * 產生 RSA-2048 金鑰對
     * 
     * @return 包含公鑰和私鑰的 Map，金鑰以 Base64 格式儲存
     * @throws RuntimeException 當金鑰產生失敗時拋出
     */
    public Map<String, String> generateKeyPair() {
        try {
            // 初始化 RSA 金鑰產生器，設定金鑰長度為 2048 位元
            java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            
            // 產生金鑰對
            java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            // 將金鑰轉換為 Base64 字串格式
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            
            // 建立回傳結果
            Map<String, String> result = new HashMap<>();
            result.put("privateKey", privateKey);
            result.put("publicKey", publicKey);
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("RSA 金鑰對產生失敗", e);
        }
    }
    
    /**
     * 使用 RSA 公鑰加密資料
     * 
     * @param data 要加密的資料
     * @param publicKeyStr Base64 格式的公鑰字串
     * @return 加密後的 Base64 字串
     * @throws RuntimeException 當加密過程發生錯誤時拋出
     */
    public String encrypt(String data, String publicKeyStr) {
        try {
            // 將 Base64 格式的公鑰轉換為 PublicKey 物件
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            // 初始化加密器並執行加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            
            // 將加密結果轉換為 Base64 字串
            return Base64.getEncoder().encodeToString(encryptedBytes);
            
        } catch (Exception e) {
            throw new RuntimeException("RSA 加密失敗", e);
        }
    }

    /**
     * 使用 RSA 私鑰解密資料
     * 
     * @param encryptedData Base64 格式的加密資料
     * @param privateKey Base64 格式的私鑰
     * @return 解密後的原始資料
     * @throws Exception 當解密過程發生錯誤時拋出
     */
    public String decrypt(String encryptedData, String privateKey) throws Exception {
        // 驗證輸入參數
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            throw new IllegalArgumentException("加密資料不能為空");
        }
        if (privateKey == null || privateKey.trim().isEmpty()) {
            throw new IllegalArgumentException("私鑰不能為空");
        }

        // 將 Base64 格式的加密資料和私鑰轉換為位元組陣列
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
        
        // 將私鑰位元組陣列轉換為 PrivateKey 物件
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey rsaPrivateKey = keyFactory.generatePrivate(keySpec);
        
        // 初始化解密器並執行解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        
        // 解密資料並轉換為字串
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
} 