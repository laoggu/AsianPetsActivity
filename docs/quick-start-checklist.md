# 前端开发快速开始清单

## 📋 必做事项清单

### 第1步：环境准备
- [ ] 安装微信开发者工具
- [ ] 配置后端API地址（localhost:8080或服务器地址）
- [ ] 获取微信小程序AppID
- [ ] 配置微信开发者工具

### 第2步：项目初始化
- [ ] 创建小程序项目
- [ ] 配置app.json页面路由
- [ ] 创建API请求封装（utils/request.js）
- [ ] 配置全局样式（app.wxss）

### 第3步：核心功能开发（按优先级）

#### 阶段1：基础功能（1-2周）
- [ ] **微信登录页** - 调用wx.login获取code，调用后端登录接口
- [ ] **首页** - 展示活动列表、公告
- [ ] **活动列表/详情** - 展示活动和报名入口
- [ ] **个人中心** - 个人信息展示

#### 阶段2：业务功能（2-3周）
- [ ] **会员申请** - 填写表单提交申请
- [ ] **扫码签到** - 使用wx.scanCode扫描二维码签到
- [ ] **消息中心** - 显示消息列表和未读数量
- [ ] **蓝皮书** - 列表展示和下载

#### 阶段3：高级功能（1-2周）
- [ ] **活动评价** - 星级评分和评论
- [ ] **续费管理** - 续费流程
- [ ] **数据可视化** - 图表展示（管理端）

---

## 🔧 API请求封装示例

```javascript
// utils/request.js
const BASE_URL = 'http://localhost:8080/api';

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.statusCode === 401) {
          // Token过期，清除并跳转登录
          wx.removeStorageSync('token');
          wx.redirectTo({ url: '/pages/login/login' });
          reject(new Error('登录已过期'));
        } else if (res.data.code === 200) {
          resolve(res.data);
        } else {
          wx.showToast({ title: res.data.message, icon: 'none' });
          reject(res.data);
        }
      },
      fail: reject
    });
  });
};

module.exports = { request };
```

---

## 📱 页面路由配置（app.json）

```json
{
  "pages": [
    "pages/index/index",
    "pages/login/login",
    "pages/activity/list",
    "pages/activity/detail",
    "pages/checkin/scan",
    "pages/message/list",
    "pages/profile/profile",
    "pages/blue-book/list"
  ],
  "tabBar": {
    "list": [
      { "pagePath": "pages/index/index", "text": "首页" },
      { "pagePath": "pages/activity/list", "text": "活动" },
      { "pagePath": "pages/message/list", "text": "消息" },
      { "pagePath": "pages/profile/profile", "text": "我的" }
    ]
  }
}
```

---

## 🚀 快速测试API

使用curl测试后端接口是否正常：

```bash
# 测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# 测试获取活动列表
curl http://localhost:8080/api/activities

# 测试Swagger文档
curl http://localhost:8080/swagger-ui.html
```

---

## ⚠️ 重要提醒

1. **跨域问题**：开发时如果遇到跨域，可以开启微信开发者工具的"不校验合法域名"选项
2. **HTTPS**：上线后必须使用HTTPS
3. **Base64图片**：二维码Base64图片可以直接用image标签显示
4. **Token管理**：登录后保存token，每次请求带上Authorization头

---

## 📞 问题反馈

遇到问题先看Swagger文档：http://localhost:8080/swagger-ui.html

还是解决不了？查看详细文档：
- 完整API文档：docs/api-documentation.md
- 前端交接文档：docs/frontend-handover-document.md
- 开发指南：docs/frontend-development-guide.md
