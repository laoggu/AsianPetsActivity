# 前端开发对接指南

## 一、Swagger API 文档访问方式

### 本地访问
启动后端服务后，访问以下地址查看Swagger文档：

```
http://localhost:8080/swagger-ui.html
```

或

```
http://localhost:8080/swagger-ui/index.html
```

**注意**：确保后端服务已启动，端口为8080。

---

## 二、认证方式

### 1. 登录获取Token
```javascript
// 登录接口
const login = async (username, password) => {
  const res = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await res.json();
  
  if (data.code === 200) {
    // 保存token
    wx.setStorageSync('token', data.data.token);
    return data.data;
  }
};
```

### 2. 请求头设置
```javascript
// 封装请求函数
const request = async (url, options = {}) => {
  const token = wx.getStorageSync('token');
  
  const defaultOptions = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  };
  
  return fetch(url, { ...defaultOptions, ...options });
};
```

---

## 三、核心功能对接

### 1. 会员续费功能

#### 续费提醒
```javascript
// 会员中心页面 - 检查是否需要续费提醒
const checkRenewalReminder = async (memberId) => {
  const res = await request(`/api/admin/renewals/member/${memberId}`);
  const data = await res.json();
  
  if (data.code === 200) {
    const renewals = data.data;
    // 判断是否需要显示续费提醒
    const hasPending = renewals.some(r => r.status === 'PENDING_PAYMENT');
    return hasPending;
  }
};
```

#### 创建续费订单
```javascript
// 点击续费按钮
const createRenewal = async (memberInfo) => {
  const res = await request('/api/admin/renewals', {
    method: 'POST',
    body: JSON.stringify({
      memberId: memberInfo.id,
      newExpireDate: '2025-12-31T23:59:59',
      amount: 5000,
      level: memberInfo.level,
      remark: '2025年度会员续费'
    })
  });
  return res.json();
};
```

### 2. 活动扫码签到

#### 生成签到二维码（管理端）
```javascript
// 活动管理页面 - 生成签到二维码
const generateCheckinQRCode = async (activityId) => {
  const res = await request(`/api/admin/activities/${activityId}/qrcode`);
  const data = await res.json();
  
  if (data.code === 200) {
    // 显示二维码
    this.setData({
      qrCodeImage: data.data.qrCode  // Base64图片
    });
  }
};
```

#### 用户扫码签到
```javascript
// 用户端扫码签到
const scanAndCheckin = () => {
  wx.scanCode({
    success: async (res) => {
      // 从二维码URL解析签到码
      const url = new URL(res.result);
      const checkinCode = url.searchParams.get('code');
      
      if (!checkinCode) {
        wx.showToast({ title: '无效的二维码', icon: 'none' });
        return;
      }
      
      // 调用签到接口
      const checkinRes = await request(`/api/checkin/scan?code=${checkinCode}`);
      const checkinData = await checkinRes.json();
      
      if (checkinData.code === 200 && checkinData.data.success) {
        wx.showModal({
          title: '签到成功',
          content: `欢迎参加${checkinData.data.activityTitle}\n签到时间：${checkinData.data.checkinTime}`,
          showCancel: false
        });
      } else {
        wx.showToast({ 
          title: checkinData.message || checkinData.data.message, 
          icon: 'none',
          duration: 3000
        });
      }
    }
  });
};
```

### 3. 活动评价功能

#### 检查是否可以评价
```javascript
// 活动详情页 - 检查是否可以评价
const checkCanEvaluate = async (activityId) => {
  const res = await request(`/api/activities/${activityId}/can-evaluate`);
  const data = await res.json();
  return data.data; // true/false
};
```

#### 提交评价
```javascript
// 评价页面
const submitEvaluation = async (evaluationData) => {
  const res = await request('/api/evaluations', {
    method: 'POST',
    body: JSON.stringify({
      activityId: evaluationData.activityId,
      signupId: evaluationData.signupId,
      overallRating: evaluationData.overallRating, // 1-5星
      contentRating: evaluationData.contentRating,
      organizationRating: evaluationData.organizationRating,
      speakerRating: evaluationData.speakerRating,
      venueRating: evaluationData.venueRating,
      comment: evaluationData.comment,
      isAnonymous: evaluationData.isAnonymous,
      hasSuggestion: evaluationData.hasSuggestion,
      suggestion: evaluationData.suggestion
    })
  });
  return res.json();
};
```

#### 展示评价列表
```javascript
// 获取活动评价
const getActivityEvaluations = async (activityId, page = 0) => {
  const res = await request(`/api/activities/${activityId}/evaluations?page=${page}&size=10`);
  const data = await res.json();
  
  if (data.code === 200) {
    // 处理评价列表
    const evaluations = data.data.content.map(item => ({
      id: item.id,
      memberName: item.isAnonymous ? '匿名用户' : item.memberName,
      overallRating: item.overallRating,
      comment: item.comment,
      createdAt: item.createdAt
    }));
    return evaluations;
  }
};
```

#### 展示评价统计（管理端）
```javascript
// 管理端获取评价统计
const getEvaluationStats = async (activityId) => {
  const res = await request(`/api/admin/activities/${activityId}/evaluation-stats`);
  const data = await res.json();
  
  if (data.code === 200) {
    const stats = data.data;
    // 渲染评分分布图
    renderRatingChart(stats.ratingDistribution);
    // 显示平均评分
    this.setData({
      averageRating: stats.averageOverallRating,
      totalEvaluations: stats.totalEvaluations
    });
  }
};
```

### 4. 批量操作功能

#### 批量更新会员状态
```javascript
// 会员列表页 - 批量操作
const batchUpdateStatus = async (memberIds, newStatus) => {
  const res = await request('/api/admin/batch/members/status', {
    method: 'POST',
    body: JSON.stringify({
      ids: memberIds,
      status: newStatus, // 'SUSPENDED', 'APPROVED' 等
      remark: '批量操作'
    })
  });
  const data = await res.json();
  
  if (data.code === 200) {
    wx.showToast({ 
      title: `成功更新${data.data.successCount}个会员`, 
      icon: 'success' 
    });
  }
};
```

#### 批量发送消息
```javascript
// 消息推送页 - 批量发送
const batchSendMessage = async (memberIds, message) => {
  const res = await request('/api/admin/batch/messages/send', {
    method: 'POST',
    body: JSON.stringify({
      memberIds: memberIds,
      title: message.title,
      content: message.content,
      type: message.type // 'SYSTEM', 'ACTIVITY', 'BLUEBOOK'
    })
  });
  return res.json();
};
```

---

## 四、新增页面开发清单

### 用户端（小程序）

| 页面 | 路径 | 功能 | 需要对接的API |
|------|------|------|---------------|
| 扫码签到 | pages/checkin/scan | 扫描二维码签到 | GET /api/checkin/scan |
| 活动评价 | pages/activity/evaluation | 评价参加的活动 | POST /api/evaluations |
| 我的评价 | pages/profile/my-evaluations | 查看我的所有评价 | GET /api/activities/{id}/my-evaluation |
| 续费中心 | pages/renewal/center | 会员续费 | POST /api/admin/renewals |
| 消息中心 | pages/message/list | 查看站内消息 | GET /api/admin/messages |

### 管理端（后台管理系统）

| 页面 | 路径 | 功能 | 需要对接的API |
|------|------|------|---------------|
| 续费管理 | pages/admin/renewals/list | 管理会员续费 | GET/POST /api/admin/renewals |
| 活动签到二维码 | pages/admin/activities/qrcode | 生成签到二维码 | GET /api/admin/activities/{id}/qrcode |
| 签到数据统计 | pages/admin/activities/checkin-stats | 查看签到统计 | GET /api/admin/activities/{id}/checkin |
| 活动评价管理 | pages/admin/activities/evaluations | 查看活动评价 | GET /api/admin/activities/{id}/evaluation-stats |
| 批量操作 | pages/admin/members/batch | 批量操作会员 | POST /api/admin/batch/** |

---

## 五、数据字典

### 会员状态 (MemberStatus)
```javascript
const MemberStatus = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  SUSPENDED: '已暂停'
};
```

### 续费状态 (RenewalStatus)
```javascript
const RenewalStatus = {
  PENDING_PAYMENT: '待支付',
  PAID: '已支付',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  OVERDUE: '已过期'
};
```

### 活动状态 (ActivityStatus)
```javascript
const ActivityStatus = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  CANCELLED: '已取消',
  ENDED: '已结束'
};
```

### 报名状态 (SignupStatus)
```javascript
const SignupStatus = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  CANCELLED: '已取消'
};
```

---

## 六、常见问题

### 1. 二维码显示问题
如果二维码Base64图片无法显示，请检查：
```javascript
// 确保data URI格式正确
const imageUrl = data.data.qrCode; // data:image/png;base64,xxx
```

### 2. 扫码签到失败
- 确保用户已登录
- 确保二维码在有效期内（默认2小时）
- 确保用户已报名且报名已通过审核

### 3. 评价提交失败
- 确保活动已结束
- 确保用户未重复评价
- 检查评分范围（1-5星）

---

## 七、开发顺序建议

### 第一阶段（核心功能）
1. 会员续费管理
2. 活动扫码签到
3. 批量操作功能

### 第二阶段（增值功能）
1. 活动评价系统
2. 数据统计大屏
3. 消息推送优化

### 第三阶段（完善功能）
1. 蓝皮书下载限制
2. 操作日志完善
3. 权限精细化管理
