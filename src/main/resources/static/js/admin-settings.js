// 產生 RSA 金鑰對
async function generateRSAKeys() {
    try {
        // 顯示載入中狀態
        const button = document.querySelector('.key-generator-buttons .btn-generate:nth-child(1)');
        const originalText = button.innerHTML;
        button.innerHTML = '<span class="btn-icon">⏳</span><span class="btn-text">產生中...</span>';
        button.disabled = true;

        // 呼叫後端 API
        const response = await fetch('./api/admin/generate/rsa', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || '產生金鑰時發生錯誤');
        }

        const data = await response.json();
        
        // 更新輸入欄位
        document.getElementById('rsa-public-key').value = data.publicKey;
        document.getElementById('rsa-private-key').value = data.privateKey;

        // 顯示成功訊息
        showNotification('RSA 金鑰對產生成功！', 'success');
        
    } catch (error) {
        console.error('產生 RSA 金鑰對時發生錯誤:', error);
        showNotification('產生金鑰時發生錯誤，請稍後再試', 'error');
    } finally {
        // 恢復按鈕狀態
        const button = document.querySelector('.key-generator-buttons .btn-generate:nth-child(1)');
        button.innerHTML = '<span class="btn-icon">🔑</span><span class="btn-text">RSA2048 公私鑰自動產生</span>';
        button.disabled = false;
    }
}

// 產生 TOTP 金鑰
async function generateTOTPKey() {
    try {
        // 顯示載入中狀態
        const button = document.querySelector('.key-generator-buttons .btn-generate:nth-child(2)');
        const originalText = button.innerHTML;
        button.innerHTML = '<span class="btn-icon">⏳</span><span class="btn-text">產生中...</span>';
        button.disabled = true;

        // 呼叫後端 API
        const response = await fetch('./api/admin/generate/totp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || '產生 TOTP 金鑰時發生錯誤');
        }

        const data = await response.json();
        
        // 更新輸入欄位
        document.getElementById('totp-key').value = data.totpKey;

        // 顯示成功訊息
        showNotification('TOTP 金鑰產生成功！', 'success');
        
    } catch (error) {
        console.error('產生 TOTP 金鑰時發生錯誤:', error);
        showNotification('產生金鑰時發生錯誤，請稍後再試', 'error');
    } finally {
        // 恢復按鈕狀態
        const button = document.querySelector('.key-generator-buttons .btn-generate:nth-child(2)');
        button.innerHTML = '<span class="btn-icon">⏱️</span><span class="btn-text">TOTP 金鑰產生</span>';
        button.disabled = false;
    }
}

// 產生 HMAC 金鑰
async function generateHMACKey() {
    try {
        // 顯示載入中狀態
        const button = document.querySelector('.key-generator-buttons .btn-generate:nth-child(3)');
        button.disabled = true;
        button.innerHTML = '<span class="btn-icon">⏳</span><span class="btn-text">產生中...</span>';
        
        const response = await fetch('./api/admin/generate/hmac', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || '產生 HMAC 金鑰失敗');
        }
        
        const data = await response.json();
        document.getElementById('hmac-key').value = data.hmacKey;
        
        // 顯示成功通知
        showNotification('HMAC 金鑰產生成功！', 'success');
        
    } catch (error) {
        console.error('產生 HMAC 金鑰時發生錯誤:', error);
        showNotification('產生 HMAC 金鑰失敗，請稍後再試', 'error');
    } finally {
        // 恢復按鈕狀態
        const button = document.querySelector('.key-generator-buttons .btn-generate:nth-child(3)');
        button.disabled = false;
        button.innerHTML = '<span class="btn-icon">🔐</span><span class="btn-text">HMAC 金鑰產生</span>';
    }
}

// 初始化管理員設定區塊
function initAdminSettings() {
    // 綁定 RSA 金鑰產生按鈕事件
    const rsaButton = document.querySelector('.key-generator-buttons .btn-generate:nth-child(1)');
    if (rsaButton) {
        rsaButton.addEventListener('click', generateRSAKeys);
    }

    // 綁定 TOTP 金鑰產生按鈕事件
    const totpButton = document.querySelector('.key-generator-buttons .btn-generate:nth-child(2)');
    if (totpButton) {
        totpButton.addEventListener('click', generateTOTPKey);
    }

    // 綁定 HMAC 金鑰產生按鈕事件
    const hmacButton = document.querySelector('.key-generator-buttons .btn-generate:nth-child(3)');
    if (hmacButton) {
        hmacButton.addEventListener('click', generateHMACKey);
    }
}

// 當文件載入完成時初始化
document.addEventListener('DOMContentLoaded', function() {
    initAdminSettings();
}); 