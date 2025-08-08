document.addEventListener('DOMContentLoaded', function() {
    initPickupPerson();
});

function initPickupPerson() {
    const generateButton = document.getElementById('generate-pickup-person-btn');
    if (generateButton) {
        generateButton.addEventListener('click', handlePickupPersonData);
    }
}

function validatePickupPersonData(phone, name) {
    if (!name.trim()) {
        return {
            isValid: false,
            message: '請輸入姓名'
        };
    }
    
    // 移除所有非數字字元
    const cleanPhone = phone.replace(/\D/g, '');
    
    if (!cleanPhone || (cleanPhone.length !== 3 && cleanPhone.length !== 10)) {
        return {
            isValid: false,
            message: '請輸入正確的電話號碼（3碼或10碼）'
        };
    }
    
    return {
        isValid: true
    };
}

async function handlePickupPersonData() {
    const button = document.getElementById('generate-pickup-person-btn');
    const phoneInput = document.getElementById('phone');
    const nameInput = document.getElementById('name');
    const totpKeyInput = document.getElementById('totp-key');
    const hmacKeyInput = document.getElementById('hmac-key');
    const rsaPublicKeyInput = document.getElementById('rsa-public-key');
    const keyCodeInput = document.getElementById('key-code');
    
    // 檢查所有必要的元素是否存在
    if (!button || !phoneInput || !nameInput || !totpKeyInput || !hmacKeyInput || !rsaPublicKeyInput) {
        console.error('找不到必要的輸入欄位');
        showNotification('系統錯誤：找不到必要的輸入欄位', 'error');
        return;
    }
    
    try {
        // 顯示載入狀態
        button.disabled = true;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 處理中...';
        
        // 驗證輸入
        const validation = validatePickupPersonData(phoneInput.value, nameInput.value);
        if (!validation.isValid) {
            showNotification(validation.message, 'error');
            return;
        }
        
        // 檢查金鑰是否已設定
        if (!totpKeyInput.value) {
            showNotification('請先產生 TOTP 金鑰', 'error');
            return;
        }
        if (!hmacKeyInput.value) {
            showNotification('請先產生 HMAC 金鑰', 'error');
            return;
        }
        if (!rsaPublicKeyInput.value) {
            showNotification('請先產生 RSA 公鑰', 'error');
            return;
        }
        
        // 準備請求資料
        const requestData = {
            phone: phoneInput.value,
            name: nameInput.value,
            totpKey: totpKeyInput.value,
            hmacKey: hmacKeyInput.value,
            rsaPublicKey: rsaPublicKeyInput.value,
            keyCode: keyCodeInput ? keyCodeInput.value : 'default'
        };
        
        // 發送請求到後端
        const response = await fetch('./api/pickup-person/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });
        
        if (!response.ok) {
            throw new Error('資料產生失敗');
        }
        
        const data = await response.json();
        
        // 顯示明碼 JSON 資料
        const jsonDisplay = document.getElementById('json-display');
        if (jsonDisplay) {
            jsonDisplay.textContent = JSON.stringify(data.plainData, null, 2);
        }
        
        // 顯示加密後的 JSON 資料
        const jsonDisplayEncrypt = document.getElementById('json-display-encrypt');
        if (jsonDisplayEncrypt) {
            jsonDisplayEncrypt.textContent = JSON.stringify(data.encryptedData, null, 2);
        }
        
        // 產生 QR Code
        const qrcodeContainer = document.getElementById('qrcode');
        if (qrcodeContainer) {
            // 清除舊的 QR Code
            qrcodeContainer.innerHTML = '';

            // 直接顯示後端傳來的 Base64 QRCode
            if (data.qrCode) {
                const img = document.createElement('img');
                img.src = 'data:image/png;base64,' + data.qrCode;
                img.alt = 'QRCode';
                img.width = 400;
                img.height = 400;
                qrcodeContainer.appendChild(img);
            }
        }
        
        // 顯示成功通知
        showNotification('資料產生成功', 'success');
        
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || '資料產生失敗', 'error');
    } finally {
        // 恢復按鈕狀態
        button.disabled = false;
        button.innerHTML = '<i class="fas fa-sync"></i> 產生超商資料';
    }
} 