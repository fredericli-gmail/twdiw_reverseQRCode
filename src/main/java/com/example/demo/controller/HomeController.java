package com.example.demo.controller;

// 引入日誌相關類別
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 引入 Spring MVC 相關類別
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首頁控制器
 * 此控制器負責處理網站首頁相關的請求
 */
@Controller
public class HomeController {
    
    // 設定日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    /**
     * 處理首頁請求
     * 
     * @param model Spring MVC 的資料模型，用於傳遞資料到視圖
     * @return 回傳視圖名稱 "home"，對應到 home.html 模板
     */
    @GetMapping("/")
    public String home(Model model) {
        // 記錄進入首頁的日誌
        logger.info("進入首頁");
        
        // 設定歡迎訊息
        model.addAttribute("message", "歡迎使用 Spring Boot！");
        
        // 回傳視圖名稱
        return "home";
    }
} 