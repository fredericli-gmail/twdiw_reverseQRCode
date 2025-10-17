// 全域變數：用於追蹤欄位計數器
let fieldCounter = 0;

// 文件載入完成後初始化
document.addEventListener('DOMContentLoaded', function() {
    initPickupPerson();
});

// 初始化函數
function initPickupPerson() {
    // 綁定「新增欄位」按鈕事件
    const addFieldButton = document.getElementById('add-field-btn');
    if (addFieldButton) {
        addFieldButton.addEventListener('click', function() {
            addDynamicField('', '');
        });
    }

    // 綁定「產生資料」按鈕事件
    const generateButton = document.getElementById('generate-pickup-person-btn');
    if (generateButton) {
        generateButton.addEventListener('click', handlePickupPersonData);
    }
}

// 新增動態欄位函數
function addDynamicField(defaultKey, defaultValue) {
    // 欄位計數器遞增
    fieldCounter++;

    // 取得容器
    const container = document.getElementById('dynamic-fields-container');
    if (!container) {
        console.error('找不到動態欄位容器');
        return;
    }

    // 建立欄位群組
    const fieldGroup = document.createElement('div');
    fieldGroup.className = 'dynamic-field-group';
    fieldGroup.dataset.fieldId = fieldCounter;

    // 建立 HTML 結構
    fieldGroup.innerHTML = `
        <div class="dynamic-field-row">
            <div class="dynamic-field-key">
                <label>欄位名稱（KEY）：</label>
                <input type="text"
                       class="field-key"
                       placeholder="例如：phone、name、email"
                       value="${defaultKey}"
                       pattern="[a-zA-Z0-9_]+"
                       title="只允許英文字母、數字和底線">
            </div>
            <div class="dynamic-field-value">
                <label>欄位值（VALUE）：</label>
                <input type="text"
                       class="field-value"
                       placeholder="請輸入值"
                       value="${defaultValue}">
            </div>
            <div class="dynamic-field-actions">
                <button type="button"
                        class="btn-remove-field"
                        onclick="removeDynamicField(${fieldCounter})"
                        title="刪除此欄位">
                    <span class="btn-icon">🗑️</span>
                </button>
            </div>
        </div>
    `;

    // 加入容器
    container.appendChild(fieldGroup);
}

// 刪除動態欄位函數
function removeDynamicField(fieldId) {
    // 找到對應的欄位群組
    const fieldGroup = document.querySelector(`[data-field-id="${fieldId}"]`);
    if (fieldGroup) {
        // 加上淡出動畫
        fieldGroup.style.opacity = '0';
        fieldGroup.style.transform = 'translateX(-20px)';

        // 延遲後移除 DOM 元素
        setTimeout(function() {
            fieldGroup.remove();
        }, 300);
    }
}

// 驗證動態欄位資料
function validateDynamicFields() {
    // 取得所有欄位群組
    const fieldGroups = document.querySelectorAll('.dynamic-field-group');

    // 檢查是否至少有一個欄位
    if (fieldGroups.length === 0) {
        return {
            isValid: false,
            message: '請至少新增一個欄位'
        };
    }

    // 檢查每個欄位
    const keys = new Set();
    for (let i = 0; i < fieldGroups.length; i++) {
        const group = fieldGroups[i];
        const keyInput = group.querySelector('.field-key');
        const valueInput = group.querySelector('.field-value');

        const key = keyInput.value.trim();
        const value = valueInput.value.trim();

        // 檢查欄位名稱是否為空
        if (!key) {
            return {
                isValid: false,
                message: '欄位名稱（KEY）不可為空'
            };
        }

        // 檢查欄位名稱格式（只允許英數字和底線）
        if (!/^[a-zA-Z0-9_]+$/.test(key)) {
            return {
                isValid: false,
                message: '欄位名稱「' + key + '」格式錯誤，只允許英文字母、數字和底線'
            };
        }

        // 檢查欄位名稱是否重複
        if (keys.has(key)) {
            return {
                isValid: false,
                message: '欄位名稱「' + key + '」重複，請使用不同的名稱'
            };
        }
        keys.add(key);

        // 檢查欄位值是否為空
        if (!value) {
            return {
                isValid: false,
                message: '欄位「' + key + '」的值不可為空'
            };
        }

        // 檢查欄位值長度（防止過長）
        if (value.length > 500) {
            return {
                isValid: false,
                message: '欄位「' + key + '」的值過長（最多 500 字元）'
            };
        }
    }

    return {
        isValid: true
    };
}

// 收集動態欄位資料
function collectDynamicFieldsData() {
    // 建立資料物件
    const data = {};

    // 取得所有欄位群組
    const fieldGroups = document.querySelectorAll('.dynamic-field-group');

    // 遍歷每個欄位群組
    for (let i = 0; i < fieldGroups.length; i++) {
        const group = fieldGroups[i];
        const keyInput = group.querySelector('.field-key');
        const valueInput = group.querySelector('.field-value');

        const key = keyInput.value.trim();
        const value = valueInput.value.trim();

        // 將資料加入物件
        if (key && value) {
            data[key] = value;
        }
    }

    return data;
}

// 處理產生資料請求
async function handlePickupPersonData() {
    const button = document.getElementById('generate-pickup-person-btn');
    const totpKeyInput = document.getElementById('totp-key');
    const hmacKeyInput = document.getElementById('hmac-key');
    const rsaPublicKeyInput = document.getElementById('rsa-public-key');
    const keyCodeInput = document.getElementById('key-code');
    const dataTypeInput = document.getElementById('data-type');

    // 檢查所有必要的元素是否存在
    if (!button || !totpKeyInput || !hmacKeyInput || !rsaPublicKeyInput) {
        console.error('找不到必要的輸入欄位');
        showNotification('系統錯誤：找不到必要的輸入欄位', 'error');
        return;
    }

    try {
        // 顯示載入狀態
        button.disabled = true;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 處理中...';

        // 驗證動態欄位
        const validation = validateDynamicFields();
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

        // 驗證資料類型 t 值
        const dataType = dataTypeInput ? dataTypeInput.value.trim() : '';
        if (!dataType) {
            showNotification('請輸入資料類型 (t)', 'error');
            return;
        }
        if (!/^[A-Za-z0-9_-]{1,32}$/.test(dataType)) {
            showNotification('資料類型 (t) 只允許英數字、底線、連字號，長度 1-32 字元', 'error');
            return;
        }

        // 收集動態欄位資料
        const dynamicData = collectDynamicFieldsData();

        // 準備請求資料
        const requestData = {
            dynamicFields: dynamicData,
            totpKey: totpKeyInput.value,
            hmacKey: hmacKeyInput.value,
            rsaPublicKey: rsaPublicKeyInput.value,
            keyCode: keyCodeInput ? keyCodeInput.value : 'default',
            dataType: dataType
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
        button.innerHTML = '<span class="btn-icon">👤</span><span class="btn-text">產生掃描的資料</span>';
    }
}
