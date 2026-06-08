// app.js
App({
  globalData: {
    userInfo: null,
    userId: null,
    baseUrl: 'http://localhost:8080'
  },

  onLaunch() {
    const userId = wx.getStorageSync('userId');
    const userInfo = wx.getStorageSync('userInfo');
    if (userId) {
      this.globalData.userId = userId;
      this.globalData.userInfo = userInfo;
    }
  },

  // 检查登录状态
  checkLogin() {
    if (!this.globalData.userId) {
      wx.redirectTo({ url: '/pages/profile/profile' });
      return false;
    }
    return true;
  },

  // 设置用户信息
  setUserInfo(user) {
    this.globalData.userId = user.id;
    this.globalData.userInfo = user;
    wx.setStorageSync('userId', user.id);
    wx.setStorageSync('userInfo', user);
  }
});
