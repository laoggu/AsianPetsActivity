# 亚宠会小程序 API 文档

## 概述

本文档提供亚宠会小程序后端API的详细说明。

- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token (Bearer)
- **数据格式**: JSON

---

## 认证相关

### 1. 用户登录
```http
POST /api/auth/login
```

**请求体:**
```json
{
  "username": "string",
  "password": "string"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "type": "Bearer",
    "expiresIn": 86400
  }
}
```

### 2. 用户注册
```http
POST /api/auth/register
```

---

## 会员管理

### 1. 获取会员列表
```http
GET /api/admin/members?page=0&size=20&status=APPROVED
```

### 2. 获取会员详情
```http
GET /api/admin/members/{id}
```

### 3. 更新会员状态
```http
PUT /api/admin/members/{id}/status?status=SUSPENDED
```

### 4. 删除会员
```http
DELETE /api/admin/members/{id}
```

### 5. 导出会员列表
```http
GET /api/admin/member/export
```

---

## 会员续费管理 (新增)

### 1. 创建续费记录
```http
POST /api/admin/renewals
```

**请求体:**
```json
{
  "memberId": 1,
  "newExpireDate": "2025-12-31T23:59:59",
  "amount": 5000.00,
  "level": "GOLD",
  "remark": "2025年度续费"
}
```

### 2. 获取续费列表
```http
GET /api/admin/renewals?memberId=1&status=PENDING_PAYMENT&page=0&size=20
```

### 3. 处理续费支付
```http
PUT /api/admin/renewals/{id}/payment
```

**请求体:**
```json
{
  "paymentMethod": "BANK_TRANSFER",
  "transactionNo": "TRX202401150001"
}
```

### 4. 取消续费
```http
PUT /api/admin/renewals/{id}/cancel
```

### 5. 获取会员续费记录
```http
GET /api/admin/renewals/member/{memberId}
```

---

## 活动管理

### 1. 获取活动列表
```http
GET /api/admin/activities?status=PUBLISHED&keyword=峰会&page=0&size=20
```

### 2. 创建活动
```http
POST /api/admin/activities
```

**请求体:**
```json
{
  "title": "2024年宠物行业峰会",
  "coverImage": "https://xxx.jpg",
  "description": "活动简介",
  "agenda": "详细议程",
  "location": "上海会展中心",
  "activityType": "会议",
  "targetAudience": "会员单位",
  "maxParticipants": 500,
  "fee": 0.00,
  "startTime": "2024-03-15T09:00:00",
  "endTime": "2024-03-15T17:00:00",
  "registrationStart": "2024-02-01T00:00:00",
  "registrationEnd": "2024-03-10T23:59:59",
  "needAudit": true
}
```

### 3. 更新活动
```http
PUT /api/admin/activities/{id}
```

### 4. 删除活动
```http
DELETE /api/admin/activities/{id}
```

### 5. 发布活动
```http
PUT /api/admin/activities/{id}/publish
```

### 6. 取消活动
```http
PUT /api/admin/activities/{id}/cancel
```

### 7. 获取报名列表
```http
GET /api/admin/activities/{id}/signups?status=PENDING&page=0&size=20
```

### 8. 审核报名
```http
PUT /api/admin/activities/{id}/signups/{userId}/audit
```

**请求体:**
```json
{
  "status": "APPROVED",
  "remark": "审核通过"
}
```

### 9. 获取签到数据
```http
GET /api/admin/activities/{id}/checkin
```

### 10. 现场签到（手动）
```http
POST /api/admin/activities/{id}/checkin?checkinCode=XXX
```

### 11. 生成签到二维码
```http
GET /api/admin/activities/{id}/qrcode?width=300&height=300
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "qrCode": "data:image/png;base64,iVBORw0KGgo...",
    "activityId": "1"
  }
}
```

---

## 活动评价 (新增)

### 1. 提交活动评价
```http
POST /api/evaluations
```

**请求体:**
```json
{
  "activityId": 1,
  "signupId": 1,
  "overallRating": 5,
  "contentRating": 4,
  "organizationRating": 5,
  "speakerRating": 4,
  "venueRating": 5,
  "comment": "活动组织得很好，收获很大！",
  "isAnonymous": false,
  "hasSuggestion": true,
  "suggestion": "希望增加更多互动环节"
}
```

### 2. 获取活动评价列表
```http
GET /api/activities/{activityId}/evaluations?page=0&size=20
```

### 3. 获取我的评价
```http
GET /api/activities/{activityId}/my-evaluation
```

### 4. 检查是否可以评价
```http
GET /api/activities/{activityId}/can-evaluate
```

### 5. 获取评价统计（管理端）
```http
GET /api/admin/activities/{activityId}/evaluation-stats
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
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

## 扫码签到

### 1. 扫描二维码签到
```http
GET /api/checkin/scan?code=A1B2C3D4
```

**响应:**
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
    "checkinTime": "2024-03-15 09:30:25",
    "checkinType": "QR"
  }
}
```

### 2. 验证签到码
```http
GET /api/checkin/validate?code=A1B2C3D4
```

---

## 蓝皮书管理

### 1. 获取蓝皮书列表
```http
GET /api/admin/bluebooks?year=2024&page=0&size=20
```

### 2. 创建蓝皮书
```http
POST /api/admin/bluebooks
```

**请求体:**
```json
{
  "title": "2024年亚洲宠物行业白皮书",
  "year": 2024,
  "description": "详细描述",
  "fileUrl": "https://xxx.com/bluebook.pdf",
  "fileSize": 10245760,
  "isMemberOnly": true
}
```

### 3. 更新蓝皮书
```http
PUT /api/admin/bluebooks/{id}
```

### 4. 删除蓝皮书
```http
DELETE /api/admin/bluebooks/{id}
```

### 5. 获取下载统计
```http
GET /api/admin/bluebooks/{id}/download-stats
```

---

## 公告管理

### 1. 获取公告列表
```http
GET /api/admin/announcements?type=SYSTEM&page=0&size=20
```

### 2. 获取置顶公告
```http
GET /api/admin/announcements/top
```

### 3. 创建公告
```http
POST /api/admin/announcements
```

**请求体:**
```json
{
  "title": "重要通知",
  "content": "公告内容",
  "type": "SYSTEM",
  "isTop": true
}
```

### 4. 更新公告
```http
PUT /api/admin/announcements/{id}
```

### 5. 删除公告
```http
DELETE /api/admin/announcements/{id}
```

### 6. 置顶/取消置顶
```http
PUT /api/admin/announcements/{id}/top?isTop=true
```

---

## 权益管理

### 1. 获取权益列表
```http
GET /api/admin/rights
```

### 2. 获取指定等级权益
```http
GET /api/admin/rights/{level}
```

### 3. 添加权益
```http
POST /api/admin/rights
```

**请求体:**
```json
{
  "level": "GOLD",
  "title": "专属顾问服务",
  "description": "享受一对一专属顾问服务",
  "icon": "service",
  "sortOrder": 1
}
```

### 4. 更新权益
```http
PUT /api/admin/rights/{id}
```

### 5. 删除权益
```http
DELETE /api/admin/rights/{id}
```

---

## 数据统计

### 1. 获取概览数据
```http
GET /api/admin/dashboard/overview
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalMembers": 1500,
    "pendingApplications": 25,
    "totalActivities": 48,
    "ongoingActivities": 3,
    "totalBluebooks": 12,
    "totalAnnouncements": 28
  }
}
```

### 2. 获取会员统计
```http
GET /api/admin/dashboard/member-stats
```

### 3. 获取活动统计
```http
GET /api/admin/dashboard/activity-stats
```

### 4. 获取地域分布
```http
GET /api/admin/dashboard/geographic
```

### 5. 获取业务范畴分布
```http
GET /api/admin/dashboard/business-scope
```

---

## 消息推送

### 1. 获取消息列表
```http
GET /api/admin/messages?type=SYSTEM&status=SENT&page=0&size=20
```

### 2. 发送消息
```http
POST /api/admin/messages
```

**请求体:**
```json
{
  "title": "活动提醒",
  "content": "您报名的活动即将开始",
  "type": "ACTIVITY",
  "targetType": "ALL",
  "sendType": "IMMEDIATE"
}
```

### 3. 定时发送消息
```http
POST /api/admin/messages/schedule
```

### 4. 获取消息统计
```http
GET /api/admin/messages/stats
```

---

## 批量操作 (新增)

### 1. 批量更新会员状态
```http
POST /api/admin/batch/members/status
```

**请求体:**
```json
{
  "ids": [1, 2, 3, 4, 5],
  "status": "SUSPENDED",
  "remark": "批量暂停"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 5,
    "failCount": 0,
    "totalCount": 5
  }
}
```

### 2. 批量发送消息
```http
POST /api/admin/batch/messages/send
```

**请求体:**
```json
{
  "memberIds": [1, 2, 3, 4, 5],
  "title": "系统通知",
  "content": "这是一则批量消息",
  "type": "SYSTEM"
}
```

### 3. 批量删除
```http
POST /api/admin/batch/delete/member
```

**请求体:**
```json
[1, 2, 3, 4, 5]
```

---

## 权限管理

### 1. 获取角色列表
```http
GET /api/admin/roles
```

### 2. 创建角色
```http
POST /api/admin/roles
```

### 3. 获取角色权限
```http
GET /api/admin/roles/{id}/permissions
```

### 4. 更新角色权限
```http
PUT /api/admin/roles/{id}/permissions
```

**请求体:**
```json
[1, 2, 3, 4, 5]
```

### 5. 获取权限列表
```http
GET /api/admin/permissions
```

---

## 系统配置

### 1. 获取系统配置
```http
GET /api/admin/config/{key}
```

### 2. 更新系统配置
```http
PUT /api/admin/config
```

### 3. 批量更新配置
```http
PUT /api/admin/config/batch
```

### 4. 获取审核配置
```http
GET /api/admin/config/audit
```

### 5. 更新审核配置
```http
PUT /api/admin/config/audit
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/未登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 会员已存在 |
| 1002 | 统一社会信用代码已存在 |
| 1003 | 会员不存在 |
| 1007 | 权限不足 |
| 1009 | 蓝皮书不存在 |
| 1010 | 公告不存在 |
| 1020 | 活动不存在 |
| 1024 | 续费记录不存在 |
| 1026 | 评价记录不存在 |
| 1027 | 活动尚未结束，无法评价 |
| 1028 | 您已评价过该活动 |

---

## 接口路径汇总

### 公共接口（需要登录）
- `GET /api/checkin/scan` - 扫码签到
- `POST /api/evaluations` - 提交评价
- `GET /api/activities/{id}/evaluations` - 获取评价列表

### 管理端接口（需要ADMIN角色）
- `/api/admin/members/**` - 会员管理
- `/api/admin/renewals/**` - 续费管理
- `/api/admin/activities/**` - 活动管理
- `/api/admin/bluebooks/**` - 蓝皮书管理
- `/api/admin/announcements/**` - 公告管理
- `/api/admin/rights/**` - 权益管理
- `/api/admin/dashboard/**` - 数据统计
- `/api/admin/messages/**` - 消息推送
- `/api/admin/batch/**` - 批量操作
- `/api/admin/roles/**` - 角色管理
- `/api/admin/permissions/**` - 权限管理
- `/api/admin/config/**` - 系统配置
