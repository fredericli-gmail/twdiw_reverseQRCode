document.addEventListener('DOMContentLoaded', function() {
    // 取得必要的 DOM 元素
    const verifyButton = document.getElementById('verify-qr-code-btn');
    const qrCodeInput = document.getElementById('qr-code-input');
    const decryptedDataDisplay = document.getElementById('decrypted-data');
    const validationStatus = document.getElementById('validation-status');

    // 從管理者設定區取得金鑰
    function getKeys() {
        const adminPrivateKey = document.getElementById('rsa-private-key');
        const adminTotpKey = document.getElementById('totp-key');
        const adminHmacKey = document.getElementById('hmac-key');

        return {
            privateKey: adminPrivateKey ? adminPrivateKey.value : null,
            totpKey: adminTotpKey ? adminTotpKey.value : null,
            hmacKey: adminHmacKey ? adminHmacKey.value : null
        };
    }

    // 更新驗證狀態顯示
    function updateValidationStatus(status, message) {
        console.log("updateValidationStatus");
        console.log(status, message);

        const validationStatus = document.getElementById('validation-status');
        if (!validationStatus) {
            console.error('找不到 validation-status 元素');
            return;
        }

        const statusIcon = validationStatus.querySelector('.status-icon');
        const statusText = validationStatus.querySelector('.status-text');
        
        if (!statusIcon || !statusText) {
            console.error('找不到狀態圖示或文字元素');
            return;
        }

        // 更新圖示和文字
        statusIcon.textContent = status === 'valid' ? '✅' : '❌';
        statusText.textContent = message || (status === 'valid' ? '驗證成功' : '驗證失敗');
        
        // 更新樣式
        validationStatus.className = 'validation-status ' + status;
    }

    // 格式化 JSON 顯示
    function formatJSON(json) {
        try {
            return JSON.stringify(json, null, 2);
        } catch (error) {
            return '無效的 JSON 格式';
        }
    }

    // 驗證 QR Code 內容
    async function verifyQRCode() {
        const qrCodeContent = document.getElementById('qr-code-input').value.trim();
        const resultDiv = document.getElementById('validation-status');
        let resultHtml = '';  // 宣告 resultHtml 變數
        
        if (!qrCodeContent) {
            resultDiv.innerHTML = `
                <div class="alert alert-warning">
                    <span style="color: orange;">⚠️</span> 請輸入 QR Code 內容
                </div>`;
            return;
        }

        try {
            // 取得所有金鑰
            const keys = getKeys();
            if (!keys.privateKey || !keys.totpKey || !keys.hmacKey) {
                throw new Error('請先在管理者設定區產生所有必要的金鑰');
            }

            // 更新狀態為處理中
            updateValidationStatus('pending', '正在驗證...');

            // 解析 QR Code 內容
            let qrCodeData;
            try {
                qrCodeData = JSON.parse(qrCodeContent);
            } catch (error) {
                throw new Error('QR Code 內容格式無效');
            }

            // 檢查必要欄位
            if (!qrCodeData.DATA) {
                throw new Error('QR Code 內容缺少 DATA 欄位');
            }

            // 發送驗證請求到後端
            const response = await fetch('./api/verify-qrcode', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    encryptedData: qrCodeData.DATA,
                    privateKey: keys.privateKey,
                    totpKey: keys.totpKey,
                    hmacKey: keys.hmacKey
                })
            });

            const result = await response.json();
            console.log('後端回應：', result);
            
            // 顯示解密後的資料
            if (result.data) {
                decryptedDataDisplay.textContent = formatJSON(result.data);
            }
            
            // 更新驗證狀態
            updateValidationStatus(result.valid ? 'valid' : 'invalid', result.message);

            if (result.valid) {
                resultHtml += `
                    <div class="alert alert-success">
                        <span style="color: green;">✔️</span> ${result.message}
                    </div>`;
                
                // 如果有資料，顯示詳細資訊
                if (result.data) {
                    try {
                        const data = JSON.parse(result.data);
                        resultHtml += `
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">驗證結果</h5>
                                    <p><strong>姓名：</strong>${data.name}</p>
                                    <p><strong>電話：</strong>${data.phone}</p>
                                    <p><strong>驗證時間：</strong>${new Date().toLocaleString()}</p>
                                </div>
                            </div>`;
                    } catch (e) {
                        console.error('解析資料時發生錯誤：', e);
                    }
                }
            } else {
                resultHtml += `
                    <div class="alert alert-danger">
                        <span style="color: red;">❌</span> ${result.message}
                    </div>`;
            }

        } catch (error) {
            console.error('驗證過程發生錯誤:', error);
            updateValidationStatus('invalid', error.message);
            decryptedDataDisplay.textContent = '';
            resultHtml = `
                <div class="alert alert-danger">
                    <span style="color: red;">❌</span> ${error.message}
                </div>`;
        }

        resultDiv.innerHTML = resultHtml;
    }

    // 綁定驗證按鈕事件
    if (verifyButton) {
        verifyButton.addEventListener('click', verifyQRCode);
    }
}); 