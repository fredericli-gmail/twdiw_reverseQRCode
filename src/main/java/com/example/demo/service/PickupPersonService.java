package com.example.demo.service;

// 引入 Spring 框架相關類別
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

// 引入 Java 標準庫相關類別
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 取件人資料服務
 * 此服務類別負責處理取件人相關的資料驗證和產生
 */
@Service
public class PickupPersonService {
    
    // 注入取件人資料驗證服務
    @Autowired
    private ReceiverValidationService validationService;
    
    /**
     * 產生取件人資料
     * 
     * @param phone 取件人電話號碼
     * @param name 取件人姓名
     * @return 包含取件人資料的 Map
     * @throws IllegalArgumentException 當輸入資料驗證失敗時拋出
     */
    public Map<String, String> generatePickupPersonData(String phone, String name) {
        // 驗證輸入資料
        List<String> errors = validationService.validateReceiverData(phone, name);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
        
        // 產生取件人資料
        Map<String, String> data = new HashMap<>();
        data.put("phone", phone);
        data.put("name", name);
        
        return data;
    }
} 