package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ECCService 的單元測試
 */
class ECCServiceTest {

    private ECCService eccService;

    @BeforeEach
    void setUp() {
        eccService = new ECCService();
    }

    @Test
    void testGenerateKeyPair() throws Exception {
        // 測試金鑰對產生
        String[] keyPair = eccService.generateKeyPair();

        // 驗證金鑰對不為空
        assertNotNull(keyPair);
        assertEquals(2, keyPair.length);

        // 驗證公鑰和私鑰不為空且不相同
        assertNotNull(keyPair[0]); // 公鑰
        assertNotNull(keyPair[1]); // 私鑰
        assertNotEquals(keyPair[0], keyPair[1]);
    }

    @Test
    void testEncryptAndDecrypt() throws Exception {
        // 產生金鑰對
        String[] keyPair = eccService.generateKeyPair();
        String publicKey = keyPair[0];
        String privateKey = keyPair[1];

        // 測試資料
        String originalText = "這是一段測試文字！@#$%^&*()_+";

        // 加密
        String encrypted = eccService.encrypt(originalText, publicKey);

        // 驗證加密結果不為空且與原文不同
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);

        // 解密
        String decrypted = eccService.decrypt(encrypted, privateKey);

        // 驗證解密結果與原文相同
        assertEquals(originalText, decrypted);
    }

    @Test
    void testEncryptAndDecryptWithSpecialCharacters() throws Exception {
        // 產生金鑰對
        String[] keyPair = eccService.generateKeyPair();
        String publicKey = keyPair[0];
        String privateKey = keyPair[1];

        // 測試特殊字元
        String[] testCases = {
                "Hello, 世界！",
                "特殊字元：!@#$%^&*()_+",
                "換行\n測試",
                "Tab\t測試",
                "中英文混合 Mixed Text 123",
                "空白字元  測試"
        };

        for (String testCase : testCases) {
            String encrypted = eccService.encrypt(testCase, publicKey);
            String decrypted = eccService.decrypt(encrypted, privateKey);
            assertEquals(testCase, decrypted, "測試案例失敗：" + testCase);
        }
    }

    @Test
    void testEncryptAndDecryptWithLongText() throws Exception {
        // 產生金鑰對
        String[] keyPair = eccService.generateKeyPair();
        String publicKey = keyPair[0];
        String privateKey = keyPair[1];

        // 產生長文字
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("這是一段很長的測試文字，用來測試加密解密功能。");
        }
        String originalText = longText.toString();

        // 加密
        String encrypted = eccService.encrypt(originalText, publicKey);

        // 解密
        String decrypted = eccService.decrypt(encrypted, privateKey);

        // 驗證
        assertEquals(originalText, decrypted);
    }

    @Test
    void testEncryptAndDecryptWithPrint() throws Exception {
        // 產生金鑰對
        String[] keyPair = eccService.generateKeyPair();
        String publicKey = keyPair[0];
        String privateKey = keyPair[1];

        System.out.println("公鑰：" + publicKey);
        System.out.println("私鑰：" + privateKey);
        System.out.println("----------------------------------------");

        // 要加密的文字
        String originalText = "{\"totp\":\"145351\",\"phone\":\"0963110951\",\"hmac\":\"HFCk/6P3JpIM0yx4H4EspuNdfSH3HzFGr8/UUxllJvc=\",\"name\":\"李長恩\"}\n";
        System.out.println("原始文字：" + originalText);

        // 加密
        String encrypted = eccService.encrypt(originalText, publicKey);
        System.out.println("加密後：" + encrypted);
        System.out.println("加密後的長度:" + encrypted.length());
        System.out.println("----------------------------------------");

        // 解密
        String decrypted = eccService.decrypt(encrypted, privateKey);
        System.out.println("解密後：" + decrypted);

        // 驗證
        assertEquals(originalText, decrypted, "解密後的文字應該與原始文字相同");
    }
}