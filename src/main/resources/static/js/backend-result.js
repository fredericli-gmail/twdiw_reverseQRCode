// 處理後端計算
async function handleBackendCalculation() {
    const button = document.getElementById('calculate-result');
    
    try {
        // 顯示載入中狀態
        button.disabled = true;
        button.innerHTML = '<span class="btn-icon">⏳</span><span class="btn-text">計算中...</span>';
        
        // 呼叫後端 API
        const response = await fetch('./api/backend/calculate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || '計算結果時發生錯誤');
        }
        
        const data = await response.json();
        
        // 更新結果顯示
        updateResultDisplay(data);
        
        // 顯示成功訊息
        showNotification('成功', '計算結果產生成功！');
        
    } catch (error) {
        console.error('計算結果時發生錯誤:', error);
        showNotification('錯誤', '計算結果失敗，請稍後再試', true);
    } finally {
        // 恢復按鈕狀態
        button.disabled = false;
        button.innerHTML = '<span class="btn-icon">📊</span><span class="btn-text">計算結果</span>';
    }
}

// 更新結果顯示
function updateResultDisplay(data) {
    // 更新 JSON 顯示
    const jsonDisplay = document.getElementById('json-display');
    if (jsonDisplay) {
        jsonDisplay.textContent = JSON.stringify(data, null, 2);
    }
    
    // 更新 QR Code 顯示
    const qrcodeDisplay = document.getElementById('qrcode-display');
    if (qrcodeDisplay && data.qrcode) {
        qrcodeDisplay.innerHTML = `<img src="${data.qrcode}" alt="QR Code">`;
    }
}

// 初始化後端計算結果區塊
function initBackendResult() {
    const button = document.getElementById('calculate-result');
    if (button) {
        button.addEventListener('click', handleBackendCalculation);
    }
}

// 在頁面載入時初始化後端計算結果區塊
document.addEventListener('DOMContentLoaded', function() {
    initBackendResult();
});

function updateResultDisplay() {
    const jsonDisplay = document.getElementById('json-display');
    const qrcodeDisplay = document.getElementById('qrcode-display');

    if (jsonDisplay) {
        jsonDisplay.innerHTML = '';
    }

    if (qrcodeDisplay) {
        qrcodeDisplay.innerHTML = '';
    }
} 