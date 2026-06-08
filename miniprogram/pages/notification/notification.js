const api = require('../../utils/api');
const util = require('../../utils/util');
const app = getApp();

Page({
  data: {
    util: util,
    notificationList: [],
    unreadCount: 0,
    page: 1,
    hasMore: true,
    swipedIndex: -1,
    touchStartX: 0,
    touchStartT: 0
  },

  onShow() {
    this.loadNotifications(true);
    this.loadUnreadCount();
  },

  async loadNotifications(refresh = false) {
    if (refresh) {
      this.setData({ page: 1, hasMore: true });
    }
    try {
      const result = await api.listNotifications(
        app.globalData.userId,
        refresh ? 1 : this.data.page,
        20
      );
      const list = result.records || [];
      const newList = refresh ? list : [...this.data.notificationList, ...list];
      this.setData({
        notificationList: newList,
        page: (refresh ? 1 : this.data.page) + 1,
        hasMore: newList.length < result.total
      });
    } catch (e) {
      console.error('加载通知失败', e);
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

  async markRead(e) {
    if (this.data.swipedIndex >= 0) return;
    const id = e.currentTarget.dataset.id;
    try {
      await api.markNotificationRead(app.globalData.userId, id);
      this.loadNotifications(true);
      this.loadUnreadCount();
    } catch (e) {
      console.error('标记已读失败', e);
    }
  },

  async deleteItem(e) {
    const id = e.currentTarget.dataset.id;
    const index = e.currentTarget.dataset.index;
    wx.showLoading({ title: '删除中...' });
    try {
      await api.deleteNotification(app.globalData.userId, id);
      const list = [...this.data.notificationList];
      list.splice(index, 1);
      this.setData({
        notificationList: list,
        swipedIndex: -1
      });
      wx.showToast({ title: '已删除', icon: 'success' });
      this.loadUnreadCount();
    } catch (e) {
      console.error('删除失败', e);
    } finally {
      wx.hideLoading();
    }
  },

  markAllRead() {
    const unreadItems = this.data.notificationList.filter(n => !n.isRead);
    if (unreadItems.length === 0) {
      wx.showToast({ title: '暂无未读消息', icon: 'none' });
      return;
    }
    wx.showLoading({ title: '处理中...' });
    const promises = unreadItems.map(n =>
      api.markNotificationRead(app.globalData.userId, n.id)
    );
    Promise.all(promises).then(() => {
      wx.showToast({ title: '已全部标记已读', icon: 'success' });
      this.loadNotifications(true);
      this.loadUnreadCount();
    }).catch(() => {
      wx.hideLoading();
    });
  },

  loadMore() {
    this.loadNotifications(false);
  },

  // 滑动删除手势
  onTouchStart(e) {
    this.data.touchStartX = e.touches[0].clientX;
    this.data.touchStartT = Date.now();
  },

  onTouchEnd(e) {
    const endX = e.changedTouches[0].clientX;
    const diffX = this.data.touchStartX - endX;
    const index = e.currentTarget.dataset.index;
    const currentSwiped = this.data.swipedIndex;

    // 如果其他项已滑开，先关闭
    if (currentSwiped >= 0 && currentSwiped !== index) {
      this.setData({ swipedIndex: -1 });
      return;
    }

    // 左滑距离 > 50px 显示删除按钮
    if (diffX > 50) {
      this.setData({ swipedIndex: index });
    }
    // 右滑距离 > 50px 或点击关闭
    else if (diffX < -50 || Math.abs(diffX) < 20) {
      this.setData({ swipedIndex: -1 });
    }
  }
});
