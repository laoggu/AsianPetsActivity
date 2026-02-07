# 活动现场二维码签到功能指南

## 功能概述

支持**一个通用签到码，多人可扫**的活动现场签到模式。

### 使用场景
1. 活动开始前，管理员在后台生成签到二维码
2. 将二维码展示在活动现场（大屏幕、海报、易拉宝等）
3. 参会人员打开小程序扫描二维码
4. 系统根据扫码用户的登录态自动完成签到

---

## 核心特性

| 特性 | 说明 |
|------|------|
| **通用性** | 一个二维码，所有参会人员均可扫描 |
| **时效性** | 二维码默认2小时有效，过期后自动失效 |
| **安全性** | 签到码存储在Redis，过期自动清除 |
| **防重复** | 自动检测已签到用户，防止重复签到 |
| **身份识别** | 扫码时自动识别用户身份，无需手动输入 |

---

## API接口

### 1. 生成活动签到二维码（管理端）

```http
GET /api/admin/activities/{activityId}/qrcode?width=300&height=300
```

**请求头：**
```
Authorization: Bearer {token}
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| activityId | Long | 是 | 活动ID |
| width | Int | 否 | 二维码宽度，默认300像素 |
| height | Int | 否 | 二维码高度，默认300像素 |

**成功响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "activityId": "1"
  }
}
```

**使用说明：**
- 二维码有效期默认为**2小时**（可在配置中调整）
- 二维码过期后需要重新生成
- 建议活动开始前15-30分钟生成二维码

---

### 2. 扫描二维码签到（用户端）

```http
GET /api/checkin/scan?code={checkinCode}
```

**请求头：**
```
Authorization: Bearer {token}  // 用户登录Token
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | String | 是 | 签到码（从二维码解析获得） |

**成功响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "message": "签到成功！欢迎参加2024年宠物行业峰会",
    "activityId": 1,
    "activityTitle": "2024年宠物行业峰会",
    "memberName": "某某宠物公司",
    "checkinTime": "2024-01-15 09:30:25",
    "checkinType": "QR"
  }
}
```

**重复签到响应：**
```json
{
  "code": 400,
  "message": "您已完成签到，无需重复签到",
  "data": {
    "success": false,
    "message": "您已完成签到，无需重复签到",
    "activityId": 1,
    "activityTitle": "2024年宠物行业峰会",
    "memberName": "某某宠物公司",
    "checkinTime": "2024-01-15 09:15:30"
  }
}
```

**失败响应：**
```json
{
  "code": 400,
  "message": "签到码无效或已过期，请刷新二维码重试",
  "data": null
}
```

```json
{
  "code": 400,
  "message": "您未报名该活动，无法签到",
  "data": null
}
```

```json
{
  "code": 400,
  "message": "您的报名尚未通过审核，无法签到",
  "data": null
}
```

---

### 3. 验证签到码有效性

```http
GET /api/checkin/validate?code={checkinCode}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "valid": true,
    "activityId": 1
  }
}
```

---

## 签到流程

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  管理员后台  │     │   活动现场   │     │   参会人员   │     │   后端系统   │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │                   │
       │ 1.生成签到二维码    │                   │                   │
       │────────────────────────────────────────>│                   │
       │                   │                   │                   │
       │ 2.返回二维码图片    │                   │                   │
       │<────────────────────────────────────────│                   │
       │                   │                   │                   │
       │ 3.展示二维码        │                   │                   │
       │──────────────────>│                   │                   │
       │                   │                   │                   │
       │                   │ 4.打开小程序扫码   │                   │
       │                   │<──────────────────│                   │
       │                   │                   │                   │
       │                   │ 5.解析签到码       │                   │
       │                   │────────────────────────────────────────>│
       │                   │                   │                   │
       │                   │                   │ 6.验证签到码+用户身份 │
       │                   │                   │<──────────────────│
       │                   │                   │                   │
       │                   │                   │ 7.完成签到          │
       │                   │                   │<──────────────────│
       │                   │                   │                   │
       │                   │ 8.返回签到结果     │                   │
       │                   │<────────────────────────────────────────│
       │                   │                   │                   │
```

---

## 二维码内容格式

二维码内容示例：
```
pages/checkin/checkin?code=A1B2C3D4
```

说明：
- `pages/checkin/checkin` - 小程序签到页面路径
- `code=A1B2C3D4` - 8位签到码

前端扫描后解析code参数，调用签到接口即可。

---

## 前端使用示例

### 微信小程序扫码签到

```javascript
// 调用微信扫码API
wx.scanCode({
  success: (res) => {
    console.log('扫码结果:', res.result);
    
    // 解析二维码内容，获取签到码
    // 例如: pages/checkin/checkin?code=A1B2C3D4
    const url = new URL(res.result);
    const checkinCode = url.searchParams.get('code');
    
    if (!checkinCode) {
      wx.showToast({ title: '无效的签到码', icon: 'none' });
      return;
    }
    
    // 调用签到接口
    wx.request({
      url: 'https://api.asiapets.org/api/checkin/scan?code=' + checkinCode,
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (response) => {
        const data = response.data;
        if (data.code === 200) {
          wx.showModal({
            title: '签到成功',
            content: `欢迎参加${data.data.activityTitle}！\n签到时间：${data.data.checkinTime}`,
            showCancel: false
          });
        } else {
          wx.showToast({
            title: data.message,
            icon: 'none',
            duration: 3000
          });
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误，请重试', icon: 'none' });
      }
    });
  },
  fail: () => {
    wx.showToast({ title: '扫码取消', icon: 'none' });
  }
});
```

---

## 配置说明

### application.yml

```yaml
app:
  qrcode:
    # 二维码有效期（分钟），默认2小时
    expire-minutes: 120

spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

---

## 签到码存储说明

签到码存储在Redis中：

```
Key: checkin:code:{checkinCode}
Value: {activityId}:{expireTime}
TTL: 120分钟（可配置）
```

示例：
```
Key: checkin:code:A1B2C3D4
Value: 1:2024-01-15 11:30:00
TTL: 7200秒
```

---

## 错误处理

| 场景 | 错误提示 | 处理建议 |
|------|----------|----------|
| 未登录 | 请先登录后再进行签到 | 引导用户登录 |
| 二维码过期 | 签到码无效或已过期，请刷新二维码重试 | 管理员重新生成二维码 |
| 未报名 | 您未报名该活动，无法签到 | 提示用户先报名活动 |
| 未审核 | 您的报名尚未通过审核，无法签到 | 提示等待审核 |
| 重复签到 | 您已完成签到，无需重复签到 | 提示已签到及签到时间 |
| 系统错误 | 签到失败：xxx | 记录日志，联系管理员 |

---

## 使用建议

### 1. 二维码展示
- 建议尺寸：屏幕展示500x500像素以上，印刷品10x10cm以上
- 展示位置：入口处、签到处、大屏幕
- 配合文字："请使用小程序扫码签到"

### 2. 时效控制
- 活动前30分钟生成二维码
- 设置2小时有效期，覆盖整个活动时长
- 多场次活动每场生成新码

### 3. 备用方案
- 准备手动签到作为备用
- 二维码失效时及时重新生成
- 网络不佳时支持离线签到+后续同步

### 4. 现场支持
- 安排工作人员引导扫码
- 准备操作指引图
- 设置签到问题处理点

---

## 后续优化建议

1. **地理位置校验**：签到时校验GPS是否在活动场地范围内
2. **签到统计大屏**：实时展示签到人数、到场率
3. **分批签到**：支持分时段生成不同二维码，统计各时段到场情况
4. **候补签到**：人数已满后标记为候补签到
5. **签到提醒**：活动开始前向未签到人员发送提醒
