const api = require('../../../utils/api');
const util = require('../../../utils/util');
const app = getApp();

Page({
  data: {
    util: util,
    medicine: null,
    showModal: false,
    modalType: 1, // 1-增加 2-减少
    modalQuantity: 1
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ 'medicine.id': options.id });
      this.loadDetail(options.id);
    }
  },

  async loadDetail(id) {
    wx.showLoading({ title: '加载中...' });
    try {
      const medicine = await api.getMedicine(app.globalData.userId, id);
      this.setData({ medicine });
    } catch (e) {
      console.error('加载药品详情失败', e);
      wx.navigateBack();
    } finally {
      wx.hideLoading();
    }
  },

  showQuantityModal(e) {
    const type = parseInt(e.currentTarget.dataset.type);
    this.setData({
      showModal: true,
      modalType: type,
      modalQuantity: 1
    });
  },

  hideModal() {
    this.setData({ showModal: false });
  },

  stopPropagation() {},

  onModalInput(e) {
    this.setData({ modalQuantity: parseInt(e.detail.value) || 0 });
  },

  async confirmQuantity() {
    const qty = this.data.modalQuantity;
    if (qty <= 0) {
      wx.showToast({ title: '请输入有效数量', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '操作中...' });
    try {
      await api.updateQuantity(app.globalData.userId, this.data.medicine.id, {
        changeType: this.data.modalType,
        quantityChange: qty
      });
      wx.showToast({ title: '操作成功', icon: 'success' });
      this.hideModal();
      this.loadDetail(this.data.medicine.id);
    } catch (e) {
      console.error('数量变更失败', e);
    } finally {
      wx.hideLoading();
    }
  },

  goToEdit() {
    wx.navigateTo({
      url: '/pages/medicine/add/add?id=' + this.data.medicine.id
    });
  },

  onDelete() {
    wx.showModal({
      title: '确认删除',
      content: '确定要删除「' + this.data.medicine.name + '」吗？',
      success: async (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '删除中...' });
          try {
            await api.deleteMedicine(app.globalData.userId, this.data.medicine.id);
            wx.showToast({ title: '删除成功', icon: 'success' });
            wx.navigateBack();
          } catch (e) {
            console.error('删除失败', e);
          } finally {
            wx.hideLoading();
          }
        }
      }
    });
  }
});
