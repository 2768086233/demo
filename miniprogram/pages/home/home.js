const api = require('../../utils/api');
const util = require('../../utils/util');
const app = getApp();

Page({
  data: {
    util: util,
    dashboard: {},
    distribution: {}
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    if (!app.globalData.userId) return;
    wx.showLoading({ title: '加载中...' });
    try {
      const [dashboard, distribution] = await Promise.all([
        api.getDashboard(app.globalData.userId),
        api.getExpiryDistribution(app.globalData.userId)
      ]);
      this.setData({ dashboard, distribution });
    } catch (e) {
      console.error('加载首页数据失败', e);
    } finally {
      wx.hideLoading();
    }
  },

  goToMedicineList(e) {
    const status = e.currentTarget.dataset.status;
    wx.navigateTo({ url: '/pages/medicine/list/list?status=' + (status || '') });
  },

  goToExpiringList() {
    // tabBar 页无法传参，用全局变量传递筛选条件
    wx.setStorageSync('listFilter', { status: 1, sortField: 'expiry_date', sortOrder: 'asc' });
    wx.switchTab({ url: '/pages/medicine/list/list' });
  },

  goToAddMedicine() {
    wx.navigateTo({ url: '/pages/medicine/add/add' });
  },

  goToScan() {
    wx.scanCode({
      success: (res) => {
        wx.navigateTo({
          url: '/pages/medicine/add/add?barcode=' + encodeURIComponent(res.result)
        });
      },
      fail: () => {
        // 电脑模拟器不支持摄像头，弹窗手动输入
        wx.showModal({
          title: '手动录入',
          content: '模拟器不支持扫码，请输入条码（或直接点确定跳过）',
          editable: true,
          placeholderText: '输入条形码/二维码内容',
          success: (modalRes) => {
            const input = modalRes.content;
            if (input && input.trim()) {
              wx.navigateTo({
                url: '/pages/medicine/add/add?barcode=' + encodeURIComponent(input.trim())
              });
            } else {
              // 没输入就直接跳转新增页
              wx.navigateTo({ url: '/pages/medicine/add/add' });
            }
          }
        });
      }
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/medicine/detail/detail?id=' + id });
  },

  openAiChat() {
    wx.navigateTo({ url: '/pages/chat/chat' });
  }
});
