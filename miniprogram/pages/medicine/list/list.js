const api = require('../../../utils/api');
const util = require('../../../utils/util');
const app = getApp();

Page({
  data: {
    util: util,
    keyword: '',
    currentStatus: '',
    sortField: 'expiry_date',
    sortOrder: 'asc',
    medicineList: [],
    page: 1,
    hasMore: true,
    loading: false
  },

  onLoad(options) {
    if (options.status !== undefined) {
      this.setData({ currentStatus: options.status });
    }
    this.loadMedicines(true);
  },

  onShow() {
    // 检查是否有从其他页面传过来的筛选条件（tabBar 无法传参，用 storage 中转）
    const filter = wx.getStorageSync('listFilter');
    if (filter) {
      wx.removeStorageSync('listFilter');
      this.setData({
        currentStatus: filter.status !== undefined ? String(filter.status) : '',
        sortField: filter.sortField || 'expiry_date',
        sortOrder: filter.sortOrder || 'asc',
        keyword: filter.keyword || ''
      });
    }
    // 每次显示都刷新列表
    this.loadMedicines(true);
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    this.loadMedicines(true);
  },

  filterByStatus(e) {
    const status = e.currentTarget.dataset.status;
    this.setData({ currentStatus: status }, () => {
      this.loadMedicines(true);
    });
  },

  changeSort(e) {
    const field = e.currentTarget.dataset.field;
    let order = 'asc';
    if (this.data.sortField === field) {
      order = this.data.sortOrder === 'asc' ? 'desc' : 'asc';
    }
    this.setData({ sortField: field, sortOrder: order }, () => {
      this.loadMedicines(true);
    });
  },

  async loadMedicines(refresh = false) {
    if (this.data.loading) return;
    this.setData({ loading: true });

    if (refresh) {
      this.setData({ page: 1, hasMore: true });
    }

    try {
      const result = await api.listMedicines(app.globalData.userId, {
        page: refresh ? 1 : this.data.page,
        size: 20,
        keyword: this.data.keyword,
        status: this.data.currentStatus !== '' ? parseInt(this.data.currentStatus) : undefined,
        sortField: this.data.sortField,
        sortOrder: this.data.sortOrder
      });

      const list = result.records || [];
      // 为每个药品预计算剩余天数，避免 WXML 中重复调用函数导致的时区问题
      list.forEach(item => {
        item._daysUntilExpiry = util.getDaysUntilExpiry(item.expiryDate);
      });
      const newList = refresh ? list : [...this.data.medicineList, ...list];
      this.setData({
        medicineList: newList,
        page: (refresh ? 1 : this.data.page) + 1,
        hasMore: newList.length < result.total
      });
    } catch (e) {
      console.error('加载药品列表失败', e);
    } finally {
      this.setData({ loading: false });
    }
  },

  loadMore() {
    this.loadMedicines(false);
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/medicine/detail/detail?id=' + id });
  },

  goToAdd() {
    wx.navigateTo({ url: '/pages/medicine/add/add' });
  }
});
