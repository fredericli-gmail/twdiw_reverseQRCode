package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HMACServiceTest {

    @Autowired
    private HMACService hmacService;

    @Test
    public void testCalculateHMAC() throws Exception {
        // 準備測試資料
        String data = "testData";
        String key = "wiyvBquTOCAMX3Rk80zJ0q4OqU4X/8xTr8B0ESXsYIo=";
        
        // 計算 HMAC
        String hmac = hmacService.calculateHMAC(data, key);
        
        // 驗證結果不為空
        assertNotNull(hmac, "HMAC 不應為空");
        
        // 驗證結果為 Base64 格式
        assertTrue(hmac.matches("^[A-Za-z0-9+/=]+$"), "HMAC 應為 Base64 格式");
    }

    @Test
    public void testCalculateHMACWithSameInput() throws Exception {
        // 準備測試資料
        String data = "testData";
        String key = "testKey";
        
        // 計算兩次 HMAC
        String hmac1 = hmacService.calculateHMAC(data, key);
        String hmac2 = hmacService.calculateHMAC(data, key);
        
        // 驗證兩次結果相同
        assertEquals(hmac1, hmac2, "相同輸入應產生相同的 HMAC");
    }

    @Test
    public void testCalculateHMACWithDifferentData() throws Exception {
        // 準備測試資料
        String key = "testKey";
        String data1 = "testData1";
        String data2 = "testData2";
        
        // 計算不同資料的 HMAC
        String hmac1 = hmacService.calculateHMAC(data1, key);
        String hmac2 = hmacService.calculateHMAC(data2, key);
        
        // 驗證結果不同
        assertNotEquals(hmac1, hmac2, "不同資料應產生不同的 HMAC");
    }

    @Test
    public void testCalculateHMACWithDifferentKeys() throws Exception {
        // 準備測試資料
        String data = "testData";
        String key1 = "testKey1";
        String key2 = "testKey2";
        
        // 計算不同金鑰的 HMAC
        String hmac1 = hmacService.calculateHMAC(data, key1);
        String hmac2 = hmacService.calculateHMAC(data, key2);
        
        // 驗證結果不同
        assertNotEquals(hmac1, hmac2, "不同金鑰應產生不同的 HMAC");
    }

    @Test
    public void testCalculateHMACWithEmptyData() throws Exception {
        // 準備測試資料
        String data = "";
        String key = "testKey";
        
        // 計算空資料的 HMAC
        String hmac = hmacService.calculateHMAC(data, key);
        
        // 驗證結果不為空且為 Base64 格式
        assertNotNull(hmac, "空資料的 HMAC 不應為空");
        assertTrue(hmac.matches("^[A-Za-z0-9+/=]+$"), "空資料的 HMAC 應為 Base64 格式");
    }

    @Test
    public void testCalculateHMACWithSpecialCharacters() throws Exception {
        // 準備測試資料
        String data = "!@#$%^&*()_+{}|:<>?";
        String key = "testKey";
        
        // 計算特殊字元的 HMAC
        String hmac = hmacService.calculateHMAC(data, key);
        
        // 驗證結果不為空且為 Base64 格式
        assertNotNull(hmac, "特殊字元的 HMAC 不應為空");
        assertTrue(hmac.matches("^[A-Za-z0-9+/=]+$"), "特殊字元的 HMAC 應為 Base64 格式");
    }

    @Test
    public void testCalculateHMACWithLongData() throws Exception {
        // 準備測試資料
        StringBuilder longData = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longData.append("testData");
        }
        String key = "testKey";
        
        // 計算長資料的 HMAC
        String hmac = hmacService.calculateHMAC(longData.toString(), key);
        
        // 驗證結果不為空且為 Base64 格式
        assertNotNull(hmac, "長資料的 HMAC 不應為空");
        assertTrue(hmac.matches("^[A-Za-z0-9+/=]+$"), "長資料的 HMAC 應為 Base64 格式");
    }
} 