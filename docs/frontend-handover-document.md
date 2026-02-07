# 亚宠会小程序前端交接文档

## 📌 文档说明

本文档用于后端开发团队向前端开发团队交接项目，包含已完成的后端API、前端需要开发的功能模块以及接口对接说明。

**后端版本**：v1.0  
**交接日期**：2024年  
**Swagger地址**：http://localhost:8080/swagger-ui.html

---

## 一、前端需要开发的功能清单

### 1.1 用户端（小程序）必须开发页面

| 序号 | 页面名称 | 页面路径 | 功能说明 | 优先级 |
|------|---------|---------|---------|--------|
| 1 | **微信登录页** | `pages/login/wechat` | 微信小程序一键登录 | ⭐⭐⭐ |
| 2 | **会员注册/申请** | `pages/member-apply/apply` | 填写企业信息申请入会 | ⭐⭐⭐ |
| 3 | **申请状态查询** | `pages/apply-status/status` | 查看审核进度和结果 | ⭐⭐⭐ |
| 4 | **会员中心** | `pages/member-center/center` | 会员信息、权益展示 | ⭐⭐⭐ |
| 5 | **个人中心** | `pages/personal/center` | 个人信息、设置 | ⭐⭐⭐ |
| 6 | **活动列表** | `pages/activity/list` | 查看所有活动 | ⭐⭐⭐ |
| 7 | **活动详情** | `pages/activity/detail` | 活动详情展示 | ⭐⭐⭐ |
| 8 | **活动报名** | `pages/activity/register` | 报名参加活动 | ⭐⭐⭐ |
| 9 | **扫码签到** | `pages/checkin/scan` | 扫描二维码签到 | ⭐⭐⭐ |
| 10 | **活动评价** | `pages/activity/evaluation` | 对活动进行评价 | ⭐⭐⭐ |
| 11 | **蓝皮书列表** | `pages/blue-book/list` | 查看蓝皮书列表 | ⭐⭐⭐ |
| 12 | **蓝皮书详情** | `pages/blue-book/detail` | 蓝皮书详情和下载 | ⭐⭐⭐ |
| 13 | **消息中心** | `pages/message/list` | 站内信列表 | ⭐⭐⭐ |
| 14 | **续费中心** | `pages/renewal/center` | 会员续费管理 | ⭐⭐ |
| 15 | **权益展示** | `pages/rights/display` | 会员权益介绍 | ⭐⭐ |
| 16 | **我的活动** | `pages/profile/my-activities` | 我参加的活动记录 | ⭐⭐ |
| 17 | **我的评价** | `pages/profile/my-evaluations` | 我的评价记录 | ⭐⭐ |

### 1.2 管理端（后台管理系统）必须开发页面

| 序号 | 页面名称 | 页面路径 | 功能说明 | 优先级 |
|------|---------|---------|---------|--------|
| 1 | **登录页** | `pages/admin/login` | 管理员登录 | ⭐⭐⭐ |
| 2 | **首页/仪表盘** | `pages/admin/dashboard/dashboard` | 数据统计概览 | ⭐⭐⭐ |
| 3 | **会员列表** | `pages/admin/members/list` | 会员管理和筛选 | ⭐⭐⭐ |
| 4 | **会员详情** | `pages/admin/members/detail` | 会员详细信息 | ⭐⭐⭐ |
| 5 | **申请审核** | `pages/admin/members/application` | 入会申请审核 | ⭐⭐⭐ |
| 6 | **活动管理** | `pages/admin/activities/list` | 活动列表管理 | ⭐⭐⭐ |
| 7 | **创建活动** | `pages/admin/activities/create` | 创建新活动 | ⭐⭐⭐ |
| 8 | **报名管理** | `pages/admin/activities/signup-management` | 管理报名人员 | ⭐⭐⭐ |
| 9 | **签到二维码** | `pages/admin/activities/qrcode` | 生成签到二维码 | ⭐⭐⭐ |
| 10 | **签到统计** | `pages/admin/activities/checkin-stats` | 签到数据统计 | ⭐⭐⭐ |
| 11 | **活动评价管理** | `pages/admin/activities/evaluations` | 查看活动评价 | ⭐⭐⭐ |
| 12 | **活动资料管理** | `pages/admin/activities/materials` | 上传PPT和照片 | ⭐⭐⭐ |
| 13 | **蓝皮书管理** | `pages/admin/bluebooks/list` | 蓝皮书列表 | ⭐⭐⭐ |
| 14 | **创建蓝皮书** | `pages/admin/bluebooks/create` | 发布蓝皮书 | ⭐⭐⭐ |
| 15 | **公告管理** | `pages/admin/announcements/list` | 公告列表 | ⭐⭐⭐ |
| 16 | **创建公告** | `pages/admin/announcements/create` | 发布公告 | ⭐⭐⭐ |
| 17 | **权益管理** | `pages/admin/rights/list` | 会员权益配置 | ⭐⭐ |
| 18 | **续费管理** | `pages/admin/renewals/list` | 续费记录管理 | ⭐⭐ |
| 19 | **消息推送** | `pages/admin/messages/push` | 发送站内信 | ⭐⭐ |
| 20 | **批量操作** | `pages/admin/members/batch` | 批量处理会员 | ⭐⭐ |
| 21 | **权限管理** | `pages/admin/system/permission` | 角色权限配置 | ⭐⭐ |
| 22 | **操作日志** | `pages/admin/system/logs` | 查看操作日志 | ⭐ |
| 23 | **数据统计** | `pages/admin/data-analysis/dashboard` | 详细数据统计 | ⭐ |

---

## 二、API接口清单与对接说明

### 2.1 认证相关接口

#### 2.1.1 微信小程序登录
```http
POST /api/auth/wechat/login
```

**请求体：**
```json
{
  "code": "微信登录临时code",
  "encryptedData": "加密数据（可选）",
  "iv": "初始向量（可选）"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "isNewUser": false,
    "openid": "oXGxxxxxxxxxx",
    "memberStatus": "APPROVED",
    "memberLevel": "GOLD"
  }
}
```

**前端实现注意：**
1. 调用 `wx.login()` 获取 code
2. 将 code 发送到后端换取 token
3. 保存 token 到本地存储
4. 后续请求在 Header 中添加 `Authorization: Bearer {token}`

```javascript
// 微信登录示例
wx.login({
  success: (res) => {
    if (res.code) {
      wx.request({
        url: 'http://localhost:8080/api/auth/wechat/login',
        method: 'POST',
        data: { code: res.code },
        success: (response) => {
          if (response.data.code === 200) {
            wx.setStorageSync('token', response.data.data.token);
            // 跳转首页
          }
        }
      });
    }
  }
});
```

---

### 2.2 会员相关接口

#### 2.2.1 获取会员列表（管理端）
```http
GET /api/admin/members?page=0&size=20&status=APPROVED&keyword=公司名称
```

#### 2.2.2 获取会员详情
```http
GET /api/admin/members/{id}
```

#### 2.2.3 更新会员状态
```http
PUT /api/admin/members/{id}/status?status=SUSPENDED
```

#### 2.2.4 批量更新会员状态
```http
POST /api/admin/batch/members/status
```

**请求体：**
```json
{
  "ids": [1, 2, 3, 4, 5],
  "status": "SUSPENDED",
  "remark": "批量暂停"
}
```

#### 2.2.5 导出会员列表
```http
GET /api/admin/member/export
```

**响应**：Excel文件下载

---

### 2.3 活动相关接口

#### 2.3.1 获取活动列表
```http
GET /api/admin/activities?status=PUBLISHED&keyword=峰会&page=0&size=20
```

**用户端使用：**
```http
GET /api/activities?page=0&size=20
```

#### 2.3.2 获取活动详情
```http
GET /api/activities/{id}
```

#### 2.3.3 报名活动
```http
POST /api/activities/{id}/signups
```

**请求体：**
```json
{
  "contactName": "张三",
  "contactMobile": "13800138000",
  "contactEmail": "zhangsan@example.com",
  "companyName": "某某宠物公司",
  "remark": "备注信息"
}
```

#### 2.3.4 生成签到二维码（管理端）
```http
GET /api/admin/activities/{id}/qrcode?width=300&height=300
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "qrCode": "data:image/png;base64,iVBORw0KGgo...",
    "activityId": "1"
  }
}
```

**前端实现：**
```javascript
// 显示二维码
this.setData({
  qrCodeImage: response.data.data.qrCode  // Base64图片直接显示
});
```

#### 2.3.5 用户扫码签到
```http
GET /api/checkin/scan?code=A1B2C3D4
```

**前端实现：**
```javascript
wx.scanCode({
  success: (res) => {
    // 解析二维码中的code参数
    const url = new URL(res.result);
    const code = url.searchParams.get('code');
    
    wx.request({
      url: `http://localhost:8080/api/checkin/scan?code=${code}`,
      header: { 'Authorization': 'Bearer ' + wx.getStorageSync('token') },
      success: (response) => {
        if (response.data.code === 200) {
          wx.showModal({
            title: '签到成功',
            content: `欢迎参加${response.data.data.activityTitle}`,
            showCancel: false
          });
        } else {
          wx.showToast({ title: response.data.message, icon: 'none' });
        }
      }
    });
  }
});
```

---

### 2.4 活动评价接口

#### 2.4.1 提交评价
```http
POST /api/evaluations
```

**请求体：**
```json
{
  "activityId": 1,
  "signupId": 1,
  "overallRating": 5,
  "contentRating": 4,
  "organizationRating": 5,
  "speakerRating": 4,
  "venueRating": 5,
  "comment": "活动组织得很好！",
  "isAnonymous": false,
  "hasSuggestion": true,
  "suggestion": "希望增加更多互动环节"
}
```

#### 2.4.2 检查是否可以评价
```http
GET /api/activities/{id}/can-evaluate
```

#### 2.4.3 获取活动评价列表
```http
GET /api/activities/{id}/evaluations?page=0&size=20
```

#### 2.4.4 获取评价统计（管理端）
```http
GET /api/admin/activities/{id}/evaluation-stats
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "activityId": 1,
    "activityTitle": "2024年宠物行业峰会",
    "averageOverallRating": 4.5,
    "totalEvaluations": 128,
    "ratingDistribution": {
      "5": 80,
      "4": 35,
      "3": 10,
      "2": 2,
      "1": 1
    }
  }
}
```

---

### 2.5 蓝皮书接口

#### 2.5.1 获取蓝皮书列表
```http
GET /api/bluebooks?page=0&size=20&year=2024
```

#### 2.5.2 获取蓝皮书详情
```http
GET /api/bluebooks/{id}
```

#### 2.5.3 下载蓝皮书（带水印）
```http
GET /api/bluebooks/{id}/download
```

**注意：**
- 需要登录且为正式会员
- 每个会员最多下载10次
- 下载的文件会自动添加水印（会员编号+公司名称）

#### 2.5.4 检查下载权限
```http
GET /api/bluebooks/{id}/can-download
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "canDownload": true,
    "remainingDownloads": 5,
    "maxDownloads": 10,
    "downloadedCount": 5
  }
}
```

---

### 2.6 消息中心接口

#### 2.6.1 获取消息列表
```http
GET /api/messages?page=0&size=20
```

#### 2.6.2 获取未读消息数量
```http
GET /api/messages/unread-count
```

**前端实现（显示红点）：**
```javascript
// 在首页或消息图标处调用
wx.request({
  url: 'http://localhost:8080/api/messages/unread-count',
  header: { 'Authorization': 'Bearer ' + token },
  success: (res) => {
    if (res.data.code === 200) {
      const unreadCount = res.data.data;
      // 显示红点或数字角标
      if (unreadCount > 0) {
        wx.setTabBarBadge({
          index: 3,  // 消息tab的索引
          text: String(unreadCount)
        });
      }
    }
  }
});
```

#### 2.6.3 标记消息已读
```http
PUT /api/messages/{id}/read
```

#### 2.6.4 标记全部已读
```http
PUT /api/messages/read-all
```

#### 2.6.5 批量发送消息（管理端）
```http
POST /api/admin/batch/messages/send
```

**请求体：**
```json
{
  "memberIds": [1, 2, 3, 4, 5],
  "title": "活动提醒",
  "content": "您报名的活动即将开始",
  "type": "ACTIVITY"
}
```

---

### 2.7 文件上传接口

#### 2.7.1 上传图片
```http
POST /api/files/upload/image
```

**Content-Type**: `multipart/form-data`

**表单参数：**
- `file`: 图片文件

**响应：**
```json
{
  "code": 200,
  "data": {
    "imageUrl": "http://localhost:8080/uploads/images/xxx.jpg",
    "originalName": "photo.jpg"
  }
}
```

**前端实现：**
```javascript
wx.chooseImage({
  success: (res) => {
    const tempFilePath = res.tempFilePaths[0];
    wx.uploadFile({
      url: 'http://localhost:8080/api/files/upload/image',
      filePath: tempFilePath,
      name: 'file',
      header: { 'Authorization': 'Bearer ' + token },
      success: (response) => {
        const data = JSON.parse(response.data);
        console.log('图片URL:', data.data.imageUrl);
      }
    });
  }
});
```

#### 2.7.2 通用文件上传
```http
POST /api/files/upload
```

**表单参数：**
- `file`: 文件
- `directory`: 存储目录（可选，默认general）

---

### 2.8 数据统计接口（管理端）

#### 2.8.1 获取概览数据
```http
GET /api/admin/dashboard/overview
```

#### 2.8.2 获取会员统计
```http
GET /api/admin/dashboard/member-stats
```

#### 2.8.3 获取活动统计
```http
GET /api/admin/dashboard/activity-stats
```

#### 2.8.4 获取地域分布
```http
GET /api/admin/dashboard/geographic
```

#### 2.8.5 获取业务范畴分布
```http
GET /api/admin/dashboard/business-scope
```

---

## 三、前端缺少的功能模块

### 3.1 必须完成的核心功能

| 模块 | 缺少内容 | 重要程度 |
|------|---------|---------|
| **微信登录** | 完整的微信登录流程，包括用户信息获取 | ⭐⭐⭐ |
| **扫码签到** | 二维码扫描、签到结果展示 | ⭐⭐⭐ |
| **活动评价** | 评价表单、星级评分组件 | ⭐⭐⭐ |
| **消息中心** | 消息列表、未读红点、消息详情 | ⭐⭐⭐ |
| **蓝皮书下载** | 下载按钮、下载次数提示 | ⭐⭐⭐ |

### 3.2 建议完成的增值功能

| 模块 | 缺少内容 | 重要程度 |
|------|---------|---------|
| **续费管理** | 续费页面、支付流程 | ⭐⭐ |
| **数据可视化** | 图表组件（echarts/d3） | ⭐⭐ |
| **批量操作** | 批量选择、批量操作确认 | ⭐⭐ |
| **富文本编辑** | 公告/活动内容编辑器 | ⭐⭐ |

---

## 四、前后端数据格式约定

### 4.1 统一响应格式

```json
{
  "code": 200,        // 状态码，200表示成功
  "message": "success", // 消息
  "data": {}          // 数据
}
```

### 4.2 分页数据格式

```json
{
  "code": 200,
  "data": {
    "content": [],      // 数据列表
    "totalElements": 100, // 总记录数
    "totalPages": 10,    // 总页数
    "number": 0,         // 当前页码
    "size": 10,          // 每页大小
    "first": true,       // 是否第一页
    "last": false        // 是否最后一页
  }
}
```

### 4.3 枚举值定义

**会员状态**：
- `PENDING` - 待审核
- `APPROVED` - 已通过
- `REJECTED` - 已拒绝
- `SUSPENDED` - 已暂停

**会员等级**：
- `REGULAR` - 普通会员
- `GOLD` - 黄金会员
- `PLATINUM` - 白金会员

**活动状态**：
- `DRAFT` - 草稿
- `PUBLISHED` - 已发布
- `CANCELLED` - 已取消
- `ENDED` - 已结束

**报名状态**：
- `PENDING` - 待审核
- `APPROVED` - 已通过
- `REJECTED` - 已拒绝
- `CANCELLED` - 已取消

---

## 五、开发注意事项

### 5.1 认证相关

1. **Token存储**：使用 `wx.setStorageSync('token', token)` 存储
2. **Token使用**：每个请求Header中添加 `Authorization: Bearer {token}`
3. **Token过期**：后端返回401时，清除本地token并跳转到登录页

### 5.2 文件上传

1. **图片压缩**：建议在上传前压缩图片
2. **进度显示**：大文件上传显示进度条
3. **格式限制**：仅允许上传指定格式文件

### 5.3 二维码签到

1. **二维码展示**：使用Base64图片直接渲染
2. **扫码时机**：建议在活动开始前15-30分钟展示二维码
3. **签到提示**：签到成功后显示成功提示和签到时间

### 5.4 消息推送

1. **轮询机制**：建议每30秒轮询一次未读消息数量
2. **红点提示**：在tabBar或消息图标上显示未读数量
3. **推送类型**：系统消息、活动通知、蓝皮书通知、公告

---

## 六、接口调试工具

### 6.1 Postman Collection

建议导入以下环境变量：
```json
{
  "baseUrl": "http://localhost:8080",
  "token": "your_jwt_token_here"
}
```

### 6.2 Swagger在线调试

访问 `http://localhost:8080/swagger-ui.html` 进行在线调试

---

## 七、常见问题

### Q1: 如何获取微信登录code？
```javascript
wx.login({
  success: (res) => {
    console.log(res.code); // 发送到后端
  }
});
```

### Q2: 如何处理Token过期？
```javascript
// 在请求拦截器中处理401错误
if (response.statusCode === 401) {
  wx.removeStorageSync('token');
  wx.redirectTo({ url: '/pages/login/login' });
}
```

### Q3: 如何显示Base64图片？
```html
<image src="data:image/png;base64,iVBORw0KGgo..." mode="aspectFit" />
```

### Q4: 如何实现下拉刷新？
```javascript
onPullDownRefresh() {
  this.loadData().then(() => {
    wx.stopPullDownRefresh();
  });
}
```

---

## 八、联系方式

如有接口问题或需要后端支持，请联系后端开发团队。

**后端API文档**：http://localhost:8080/swagger-ui.html  
**数据字典**：docs/data-dictionary.md  
**开发指南**：docs/frontend-development-guide.md
