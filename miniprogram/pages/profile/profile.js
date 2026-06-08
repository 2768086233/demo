const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    isLoggedIn: false,
    userInfo: null,
    unreadCount: 0
  },

  onShow() {
    const userInfo = app.globalData.userInfo;
    this.setData({
      isLoggedIn: !!app.globalData.userId,
      userInfo: userInfo
    });
    if (app.globalData.userId) {
      this.loadUnreadCount();
    }
  },

  async loadUnreadCount() {
    try {
      const result = await api.getUnreadCount(app.globalData.userId);
      this.setData({ unreadCount: result.unreadCount });
    } catch (e) {
      console.error('加载未读数失败', e);
    }
  },

  // 微信登录
  login() {
    wx.login({
      success: async (res) => {
        if (res.code) {
          wx.showLoading({ title: '登录中...' });
          try {
            const user = await api.login(res.code);
            app.setUserInfo(user);
            this.setData({
              isLoggedIn: true,
              userInfo: user
            });
            wx.showToast({ title: '登录成功', icon: 'success' });
            this.loadUnreadCount();
          } catch (e) {
            console.error('登录失败', e);
          } finally {
            wx.hideLoading();
          }
        }
      },
      fail: () => {
        // 开发环境：使用模拟登录
        this.mockLogin();
      }
    });
  },

  // 开发环境模拟登录
  async mockLogin() {
    wx.showLoading({ title: '模拟登录中...' });
    try {
      const user = await api.login('dev_mock_code');
      app.setUserInfo(user);
      this.setData({
        isLoggedIn: true,
        userInfo: user
      });
      wx.showToast({ title: '登录成功（模拟）', icon: 'success' });
      this.loadUnreadCount();
    } catch (e) {
      console.error('模拟登录失败', e);
    } finally {
      wx.hideLoading();
    }
  },

  goToNotifications() {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    wx.switchTab({ url: '/pages/notification/notification' });
  },

  goToStatistics() {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    wx.switchTab({ url: '/pages/home/home' });
  },

  onLogout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('userId');
          wx.removeStorageSync('userInfo');
          app.globalData.userId = null;
          app.globalData.userInfo = null;
          this.setData({
            isLoggedIn: false,
            userInfo: null,
            unreadCount: 0
          });
          wx.showToast({ title: '已退出', icon: 'success' });
        }
      }
    });
  }
});
