package com.example.demo.service;

// 引入 ZXing 相關的類別，用於產生 QR Code
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

// 引入 Java 標準庫相關類別
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Code 產生服務
 * 此服務類別負責將文字內容轉換為 QR Code 圖片，並以 Base64 格式回傳
 */
@Service
public class QRCodeService {
    /**
     * 將文字內容轉換為 QR Code 圖片，並回傳 Base64 編碼的字串
     * 
     * @param text 要轉換成 QR Code 的文字內容
     * @return 回傳 QR Code 圖片的 Base64 編碼字串（PNG 格式）
     * @throws Exception 當 QR Code 產生過程發生錯誤時拋出例外
     */
    public String generateQRCodeBase64(String text) throws Exception {
        // 設定 QR Code 的編碼提示，使用 UTF-8 字元集
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        
        // 使用 ZXing 產生 QR Code 的點陣圖
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 400, 400, hints);
        
        // 建立位元組輸出串流，用於儲存 QR Code 圖片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 將點陣圖轉換為 PNG 格式的圖片，並寫入輸出串流
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        
        // 將圖片資料轉換為 Base64 字串並回傳
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
} 