const api = require('../../../utils/api');
const util = require('../../../utils/util');
const app = getApp();

Page({
  data: {
    isEdit: false,
    editId: null,
    form: {
      name: '',
      genericName: '',
      batchNumber: '',
      produceDate: '',
      expiryDate: '',
      quantity: 1,
      unit: '盒',
      location: '',
      manufacturer: '',
      category: '',
      remark: ''
    },
    unitList: ['盒', '瓶', '片', '支', '袋', '罐', '粒', '包', '盒装', '瓶装'],
    unitIndex: 0,
    categoryList: ['感冒药', '肠胃药', '止痛药', '抗生素', '慢性病药', '外用药', '保健品', '中药', '其他'],
    categoryIndex: -1,
    locationList: ['客厅药箱', '卧室', '冰箱', '厨房', '书房', '随身包', '办公室', '其他'],
    locationIndex: -1
  },

  onLoad(options) {
    if (options.id) {
      // 编辑模式
      this.setData({ isEdit: true, editId: options.id });
      wx.setNavigationBarTitle({ title: '编辑药品' });
      this.loadMedicine(options.id);
    }
    if (options.barcode) {
      this.scanBarcode(options.barcode);
    }
  },

  async loadMedicine(id) {
    wx.showLoading({ title: '加载中...' });
    try {
      const med = await api.getMedicine(app.globalData.userId, id);
      const form = { ...this.data.form };
      form.name = med.name || '';
      form.genericName = med.genericName || '';
      form.batchNumber = med.batchNumber || '';
      form.produceDate = med.produceDate || '';
      form.expiryDate = med.expiryDate || '';
      form.quantity = med.quantity || 1;
      form.unit = med.unit || '盒';
      form.location = med.location || '';
      form.manufacturer = med.manufacturer || '';
      form.category = med.category || '';
      form.remark = med.remark || '';

      const unitIndex = this.data.unitList.indexOf(med.unit);
      const categoryIndex = this.data.categoryList.indexOf(med.category);
      const locationIndex = this.data.locationList.indexOf(med.location);

      this.setData({
        form,
        unitIndex: unitIndex >= 0 ? unitIndex : 0,
        categoryIndex: categoryIndex >= 0 ? categoryIndex : -1,
        locationIndex: locationIndex >= 0 ? locationIndex : -1
      });
    } catch (e) {
      console.error('加载药品信息失败', e);
    } finally {
      wx.hideLoading();
    }
  },

  async scanBarcode(barcode) {
    try {
      const result = await api.scanMedicine(barcode);
      if (result) {
        const form = { ...this.data.form };
        if (result.name) form.name = result.name;
        if (result.batchNumber) form.batchNumber = result.batchNumber;
        this.setData({ form });
      }
    } catch (e) {
      console.error('扫码查询失败', e);
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({ ['form.' + field]: value });
  },

  onDateChange(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({ ['form.' + field]: value });
  },

  onUnitChange(e) {
    const index = e.detail.value;
    this.setData({
      unitIndex: index,
      'form.unit': this.data.unitList[index]
    });
  },

  onCategoryChange(e) {
    const index = e.detail.value;
    this.setData({
      categoryIndex: parseInt(index),
      'form.category': this.data.categoryList[index]
    });
  },

  onLocationChange(e) {
    const index = e.detail.value;
    this.setData({
      locationIndex: parseInt(index),
      'form.location': this.data.locationList[index]
    });
  },

  async onSubmit() {
    const form = this.data.form;
    if (!form.name) {
      wx.showToast({ title: '请输入药品名称', icon: 'none' });
      return;
    }
    if (!form.batchNumber) {
      wx.showToast({ title: '请输入生产批号', icon: 'none' });
      return;
    }
    if (!form.expiryDate) {
      wx.showToast({ title: '请选择有效期', icon: 'none' });
      return;
    }

    wx.showLoading({ title: this.data.isEdit ? '保存中...' : '录入中...' });

    try {
      const payload = { ...form };
      payload.quantity = parseInt(payload.quantity) || 1;
      payload.produceDate = payload.produceDate || null;
      payload.genericName = payload.genericName || null;
      payload.manufacturer = payload.manufacturer || null;
      payload.remark = payload.remark || null;
      payload.location = payload.location || null;
      payload.category = payload.category || null;
      payload.unit = payload.unit || '盒';

      if (this.data.isEdit) {
        await api.updateMedicine(app.globalData.userId, this.data.editId, payload);
        wx.showToast({ title: '保存成功', icon: 'success' });
      } else {
        await api.addMedicine(app.globalData.userId, payload);
        wx.showToast({ title: '录入成功', icon: 'success' });
      }

      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (e) {
      console.error('提交失败', e);
    } finally {
      wx.hideLoading();
    }
  }
});
