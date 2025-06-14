package com.example.demo.service;

// 引入 Spring 框架相關類別
import org.springframework.stereotype.Service;

// 引入 Java 標準庫相關類別
import java.util.ArrayList;
import java.util.List;

/**
 * 取件人資料驗證服務
 * 此服務類別負責驗證取件人的基本資料，包含：
 * 1. 姓名驗證
 * 2. 手機號碼驗證
 */
@Service
public class ReceiverValidationService {
    
    /**
     * 驗證取件人資料
     * 
     * @param phone 取件人電話號碼
     * @param name 取件人姓名
     * @return 驗證錯誤訊息列表，如果列表為空則表示驗證通過
     */
    public List<String> validateReceiverData(String phone, String name) {
        List<String> errors = new ArrayList<>();
        
        // 驗證姓名：不能為空
        if (name == null || name.trim().isEmpty()) {
            errors.add("姓名不能為空");
        }
        
        // 驗證手機號碼
        if (phone == null || phone.trim().isEmpty()) {
            errors.add("手機號碼不能為空");
        } else {
            // 移除所有非數字字元（如：空格、連字號等）
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            
            // 檢查手機號碼長度是否為 3 位數（市話）或 10 位數（手機）
            if (cleanPhone.length() != 3 && cleanPhone.length() != 10) {
                errors.add("手機號碼必須為 3 位數或 10 位數");
            }
        }
        
        return errors;
    }
} 