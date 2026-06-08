const app = getApp();

const BASE_URL = 'http://localhost:8899';

// 通用请求封装
function request(method, url, data) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: { 'Content-Type': 'application/json' },
      success(res) {
        if (res.data.code === 200) {
          resolve(res.data.data);
        } else {
          wx.showToast({ title: res.data.message || '请求失败', icon: 'none' });
          reject(res.data);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常，请检查服务器', icon: 'none' });
        reject(err);
      }
    });
  });
}

/** ========== 用户模块 ========== */
function login(code) {
  return request('POST', '/api/user/login', { code });
}
function getUserInfo(userId) {
  return request('GET', '/api/user/info', { userId });
}
function updateUserInfo(userId, data) {
  return request('PUT', '/api/user/info', { userId, ...data });
}

/** ========== 药品模块 ========== */
function addMedicine(userId, data) {
  return request('POST', '/api/medicine?userId=' + userId, data);
}
function deleteMedicine(userId, id) {
  return request('DELETE', '/api/medicine/' + id + '?userId=' + userId);
}
function updateMedicine(userId, id, data) {
  return request('PUT', '/api/medicine/' + id + '?userId=' + userId, data);
}
function getMedicine(userId, id) {
  return request('GET', '/api/medicine/' + id + '?userId=' + userId);
}
function listMedicines(userId, params) {
  let query = '?userId=' + userId;
  if (params) {
    if (params.page) query += '&page=' + params.page;
    if (params.size) query += '&size=' + params.size;
    if (params.keyword) query += '&keyword=' + encodeURIComponent(params.keyword);
    if (params.status !== undefined && params.status !== '') query += '&status=' + params.status;
    if (params.category) query += '&category=' + encodeURIComponent(params.category);
    if (params.location) query += '&location=' + encodeURIComponent(params.location);
    if (params.sortField) query += '&sortField=' + params.sortField;
    if (params.sortOrder) query += '&sortOrder=' + params.sortOrder;
  }
  return request('GET', '/api/medicine/list' + query);
}
function updateQuantity(userId, id, data) {
  return request('PUT', '/api/medicine/' + id + '/quantity?userId=' + userId, data);
}
function scanMedicine(barcode) {
  return request('POST', '/api/medicine/scan?barcode=' + encodeURIComponent(barcode));
}
function getMedicineLogs(id, page = 1, size = 20) {
  return request('GET', '/api/medicine/' + id + '/logs?page=' + page + '&size=' + size);
}

/** ========== 统计模块 ========== */
function getDashboard(userId) {
  return request('GET', '/api/statistics/dashboard?userId=' + userId);
}
function getExpiryDistribution(userId) {
  return request('GET', '/api/statistics/expiry-distribution?userId=' + userId);
}

/** ========== 通知模块 ========== */
function listNotifications(userId, page = 1, size = 20) {
  return request('GET', '/api/notification/list?userId=' + userId + '&page=' + page + '&size=' + size);
}
function markNotificationRead(userId, id) {
  return request('PUT', '/api/notification/' + id + '/read?userId=' + userId);
}
function deleteNotification(userId, id) {
  return request('DELETE', '/api/notification/' + id + '?userId=' + userId);
}
function getUnreadCount(userId) {
  return request('GET', '/api/notification/unread-count?userId=' + userId);
}

/** ========== AI 助手 ========== */
function chatAi(message, history) {
  return request('POST', '/api/ai/chat', { message, history });
}

module.exports = {
  login, getUserInfo, updateUserInfo,
  addMedicine, deleteMedicine, updateMedicine, getMedicine,
  listMedicines, updateQuantity, scanMedicine, getMedicineLogs,
  getDashboard, getExpiryDistribution,
  listNotifications, markNotificationRead, deleteNotification, getUnreadCount,
  chatAi
};
