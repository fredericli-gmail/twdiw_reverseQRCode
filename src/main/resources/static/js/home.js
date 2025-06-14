// 主題切換功能
function toggleTheme() {
    const html = document.documentElement;
    const currentTheme = html.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    
    html.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    
    // 更新按鈕圖示
    const themeIcon = document.querySelector('.theme-switch-icon');
    themeIcon.textContent = newTheme === 'dark' ? '☀️' : '🌙';
}

// 初始化主題
function initTheme() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    
    // 設定初始按鈕圖示
    const themeIcon = document.querySelector('.theme-switch-icon');
    themeIcon.textContent = savedTheme === 'dark' ? '☀️' : '🌙';
}

/**
 * 顯示通知訊息
 * @param {string} title - 通知標題
 * @param {string} message - 通知內容
 * @param {boolean} isError - 是否為錯誤通知
 */
function showNotification(title, message, isError = false) {
    const notification = document.createElement('div');
    notification.className = `notification ${isError ? 'error' : 'success'}`;
    
    const icon = isError ? '❌' : '✅';
    
    notification.innerHTML = `
        <div class="notification-content">
            <div class="notification-icon">${icon}</div>
            <div class="notification-message">
                <div class="notification-title">${title}</div>
                <div class="notification-text">${message}</div>
            </div>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    // 強制重繪以觸發動畫
    notification.offsetHeight;
    notification.classList.add('show');
    
    // 3 秒後移除通知
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// 在頁面載入時初始化主題
document.addEventListener('DOMContentLoaded', initTheme); 