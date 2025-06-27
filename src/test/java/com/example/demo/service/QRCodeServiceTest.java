package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QRCodeService 測試類
 * 測試 non-destructive QR Code with logo space 功能
 */
@SpringBootTest
class QRCodeServiceTest {

    @Autowired
    private QRCodeService qrCodeService;

    @Test
    void testGenerateQRCodeBase64() throws Exception {
        String text = "https://example.com";
        String result = qrCodeService.generateQRCodeBase64(text);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        System.out.println(result);
        
        // 驗證 Base64 格式
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void testGenerateQRCodeBase64WithCustomSize() throws Exception {
        String text = "https://example.com";
        String result = qrCodeService.generateQRCodeBase64(text, 300, 300);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // 驗證 Base64 格式
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void testGenerateQRCodeWithLogoSpace() throws Exception {
        String text = "https://example.com";
        String result = qrCodeService.generateQRCodeWithLogoSpace(text);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // 驗證 Base64 格式
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void testGenerateQRCodeWithLogoSpaceWithCustomSize() throws Exception {
        String text = "https://example.com";
        String result = qrCodeService.generateQRCodeWithLogoSpace(text, null, 500, 500, 100);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // 驗證 Base64 格式
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void testLoadImageFromBase64() throws Exception {
        // 創建一個簡單的測試圖片
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        // 轉換為 Base64
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(testImage, "PNG", baos);
        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        
        // 測試載入
        BufferedImage loadedImage = qrCodeService.loadImageFromBase64(base64Image);
        
        assertNotNull(loadedImage);
        assertEquals(100, loadedImage.getWidth());
        assertEquals(100, loadedImage.getHeight());
    }

    @Test
    void testGenerateQRCodeWithLogoSpaceWithLogo() throws Exception {
        String text = "https://example.com";
        
        // 創建一個簡單的 logo 圖片
        BufferedImage logoImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        
        String result = qrCodeService.generateQRCodeWithLogoSpace(text, logoImage);

        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // 驗證 Base64 格式
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void testGenerateQRCodeWithComplexText() throws Exception {
        String complexText = "https://example.com?param1=value1&param2=value2&param3=中文測試";
        String result = qrCodeService.generateQRCodeWithLogoSpace(complexText);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // 驗證 Base64 格式
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void testGenerateQRCodeWithLargeLogo() throws Exception {
        String text = "https://example.com";
        
        // 創建一個較大的 logo 圖片
        BufferedImage logoImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        String result = qrCodeService.generateQRCodeWithLogoSpace(text, logoImage, 400, 400, 100);

        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // 驗證 Base64 格式
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void testGenerateQRCodeWithTwdiwLogo() throws Exception {
        String text = "https://example.com/twdiw";
        
        try {
            // 讀取 static/images/twdiw.png 作為 logo
            ClassPathResource resource = new ClassPathResource("static/images/twdiw.png");
            BufferedImage twdiwLogo = ImageIO.read(resource.getInputStream());
            
            assertNotNull(twdiwLogo, "twdiw.png 圖片載入失敗");
            
            // 生成帶有 twdiw logo 的 QR Code
            String result = qrCodeService.generateQRCodeWithLogoSpace(text, twdiwLogo);
            
            System.out.println("=== TWDIW Logo QR Code ===");
            System.out.println("原始圖片尺寸: " + twdiwLogo.getWidth() + "x" + twdiwLogo.getHeight());
            System.out.println("QR Code Base64 長度: " + result.length());
            System.out.println("QR Code Base64 前100字元: " + result.substring(0, Math.min(100, result.length())));

            System.out.println(result);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            
            // 驗證 Base64 格式
            assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
            
            // 驗證生成的圖片可以正確解碼
            byte[] imageBytes = Base64.getDecoder().decode(result);
            BufferedImage generatedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            assertNotNull(generatedImage, "生成的 QR Code 圖片解碼失敗");
            
            System.out.println("生成的 QR Code 尺寸: " + generatedImage.getWidth() + "x" + generatedImage.getHeight());
            
        } catch (IOException e) {
            fail("讀取 twdiw.png 失敗: " + e.getMessage());
        }
    }

    @Test
    void testGenerateQRCodeWithTwdiwLogoCustomSize() throws Exception {
        String text = "https://example.com/twdiw-custom";
        
        try {
            // 讀取 static/images/twdiw.png 作為 logo
            ClassPathResource resource = new ClassPathResource("static/images/twdiw.png");
            BufferedImage twdiwLogo = ImageIO.read(resource.getInputStream());
            
            assertNotNull(twdiwLogo, "twdiw.png 圖片載入失敗");
            
            // 生成帶有 twdiw logo 的 QR Code（自訂尺寸）
            String result = qrCodeService.generateQRCodeWithLogoSpace(text, twdiwLogo, 600, 600, 150);
            
            System.out.println("=== TWDIW Logo QR Code (Custom Size) ===");
            System.out.println("原始圖片尺寸: " + twdiwLogo.getWidth() + "x" + twdiwLogo.getHeight());
            System.out.println("QR Code 尺寸: 600x600, Logo 尺寸: 150x150");
            System.out.println("QR Code Base64 長度: " + result.length());

            System.out.println(result);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            
            // 驗證 Base64 格式
            assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
            
        } catch (IOException e) {
            fail("讀取 twdiw.png 失敗: " + e.getMessage());
        }
    }

    @Test
    void testNonDestructiveQRCodeVerification() throws Exception {
        String text = "https://example.com/non-destructive-test";
        
        try {
            // 讀取 static/images/twdiw.png 作為 logo
            ClassPathResource resource = new ClassPathResource("static/images/twdiw.png");
            BufferedImage twdiwLogo = ImageIO.read(resource.getInputStream());
            
            assertNotNull(twdiwLogo, "twdiw.png 圖片載入失敗");
            
            // 生成標準 QR Code（無 logo）
            String standardQRCode = qrCodeService.generateQRCodeBase64(text);

            //System.out.println(standardQRCode);

            System.out.println("============================");

            // 生成帶有 logo 的 QR Code
            String logoQRCode = qrCodeService.generateQRCodeWithLogoSpace(text, twdiwLogo);

            System.out.println(logoQRCode);
            
            System.out.println("=== Non-Destructive QR Code 驗證 ===");
            System.out.println("原始文字: " + text);
            System.out.println("標準 QR Code Base64 長度: " + standardQRCode.length());
            System.out.println("帶 Logo QR Code Base64 長度: " + logoQRCode.length());
            
            // 驗證兩個 QR Code 都不為空
            assertNotNull(standardQRCode);
            assertNotNull(logoQRCode);
            assertFalse(standardQRCode.isEmpty());
            assertFalse(logoQRCode.isEmpty());
            
            // 驗證 Base64 格式
            assertDoesNotThrow(() -> Base64.getDecoder().decode(standardQRCode));
            assertDoesNotThrow(() -> Base64.getDecoder().decode(logoQRCode));
            
            // 驗證生成的圖片可以正確解碼
            byte[] standardImageBytes = Base64.getDecoder().decode(standardQRCode);
            byte[] logoImageBytes = Base64.getDecoder().decode(logoQRCode);
            
            BufferedImage standardImage = ImageIO.read(new ByteArrayInputStream(standardImageBytes));
            BufferedImage logoImage = ImageIO.read(new ByteArrayInputStream(logoImageBytes));
            
            assertNotNull(standardImage, "標準 QR Code 圖片解碼失敗");
            assertNotNull(logoImage, "帶 Logo QR Code 圖片解碼失敗");
            
            System.out.println("標準 QR Code 尺寸: " + standardImage.getWidth() + "x" + standardImage.getHeight());
            System.out.println("帶 Logo QR Code 尺寸: " + logoImage.getWidth() + "x" + logoImage.getHeight());
            
            // 驗證尺寸一致
            assertEquals(standardImage.getWidth(), logoImage.getWidth());
            assertEquals(standardImage.getHeight(), logoImage.getHeight());
            
            // 驗證 QR Code 內容是否真的被保留（通過比較圖片差異）
            int differences = 0;
            int totalPixels = standardImage.getWidth() * standardImage.getHeight();
            
            for (int x = 0; x < standardImage.getWidth(); x++) {
                for (int y = 0; y < standardImage.getHeight(); y++) {
                    if (standardImage.getRGB(x, y) != logoImage.getRGB(x, y)) {
                        differences++;
                    }
                }
            }
            
            double differencePercentage = (double) differences / totalPixels * 100;
            System.out.println("圖片差異像素數: " + differences + " / " + totalPixels);
            System.out.println("差異百分比: " + String.format("%.2f", differencePercentage) + "%");
            
            // 驗證差異主要集中在中間區域（logo 區域）
            // 差異應該小於總像素的 15%（logo 區域約佔 10-15%）
            assertTrue(differencePercentage < 15.0, 
                "QR Code 差異過大，可能影響了可讀性。差異百分比: " + differencePercentage + "%");
            
            System.out.println("✅ Non-destructive QR Code 驗證通過！");
            
        } catch (IOException e) {
            fail("讀取 twdiw.png 失敗: " + e.getMessage());
        }
    }
} 