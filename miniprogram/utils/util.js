/** 工具函数 */

// 格式化日期为 yyyy-MM-dd
function formatDate(date) {
  if (!date) return '';
  const d = new Date(date);
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

// 格式化日期时间
function formatDateTime(date) {
  if (!date) return '';
  const d = new Date(date);
  return formatDate(date) + ' ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0');
}

// 计算剩余天数
function getDaysUntilExpiry(expiryDate) {
  if (!expiryDate) return null;
  const now = new Date();
  now.setHours(0, 0, 0, 0);
  const expiry = new Date(expiryDate);
  expiry.setHours(0, 0, 0, 0);
  const diff = Math.floor((expiry - now) / (1000 * 60 * 60 * 24));
  return diff;
}

// 获取状态文本
function getStatusText(status) {
  const map = { 0: '正常', 1: '临期', 2: '过期', 3: '已用完' };
  return map[status] || '未知';
}

// 获取状态样式类
function getStatusClass(status) {
  const map = { 0: 'tag-normal', 1: 'tag-expiring', 2: 'tag-expired', 3: 'tag-usedup' };
  return map[status] || 'tag-normal';
}

// 通知类型文本
function getNotifyTypeText(type) {
  const map = { 1: '临期提醒', 2: '过期提醒', 3: '补充提醒' };
  return map[type] || '未知';
}

// 检查是否有药品即将过期（30天内）
function isExpiringSoon(expiryDate) {
  const days = getDaysUntilExpiry(expiryDate);
  return days !== null && days >= 0 && days <= 30;
}

module.exports = {
  formatDate,
  formatDateTime,
  getDaysUntilExpiry,
  getStatusText,
  getStatusClass,
  getNotifyTypeText,
  isExpiringSoon
};
