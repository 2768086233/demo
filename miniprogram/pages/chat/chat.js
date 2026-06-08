const api = require('../../utils/api');

Page({
  data: {
    messages: [],
    inputText: '',
    isLoading: false
  },

  onInput(e) {
    this.setData({ inputText: e.detail.value });
  },

  sendMessage() {
    const text = this.data.inputText.trim();
    if (!text || this.data.isLoading) return;

    const messages = [...this.data.messages];
    // 添加用户消息
    messages.push({ role: 'user', content: text });
    // 添加占位 AI 回复
    messages.push({ role: 'assistant', content: '', loading: true });

    this.setData({
      messages,
      inputText: '',
      isLoading: true
    });

    this.callAi(text);
  },

  sendQuick(e) {
    const msg = e.currentTarget.dataset.msg;
    this.setData({ inputText: msg });
    this.sendMessage();
  },

  async callAi(userMessage) {
    try {
      // 构建历史消息（不含最后的 loading 消息）
      const history = this.data.messages
        .filter((m, i) => i < this.data.messages.length - 1 && m.role !== 'system')
        .slice(0, -1) // 去掉刚发的用户消息
        .map(m => ({ role: m.role, content: m.content }));

      const result = await api.chatAi(userMessage, history);
      const messages = [...this.data.messages];
      // 替换 loading 消息
      messages[messages.length - 1] = {
        role: 'assistant',
        content: result.reply || '抱歉，我没有理解你的问题。',
        loading: false
      };
      this.setData({ messages, isLoading: false });
    } catch (e) {
      const messages = [...this.data.messages];
      messages[messages.length - 1] = {
        role: 'assistant',
        content: '网络开小差了，请稍后重试 🙏',
        loading: false
      };
      this.setData({ messages, isLoading: false });
    }
  }
});
