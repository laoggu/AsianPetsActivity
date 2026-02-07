# API功能分析文档

## 后端已提供的API（根据api.txt）

### 认证管理
| 方法 | 路径 | 功能 | 状态 |
|------|------|------|------|
| POST | /api/auth/register | 用户注册 | ✅ 已提供 |
| POST | /api/auth/login | 用户登录 | ✅ 已提供 |

### 会员管理
| 方法 | 路径 | 功能 | 状态 |
|------|------|------|------|
| GET | /api/member/profile | 获取个人信息 | ✅ 已提供 |
| PUT | /api/member/profile | 更新个人信息 | ✅ 已提供 |
| GET | /api/member/{id}/status | 获取会员状态 | ✅ 已提供 |
| POST | /api/member/upload | 文件上传 | ✅ 已提供 |
| POST | /api/member/apply | 会员申请 | ✅ 已提供 |

### 管理员接口
| 方法 | 路径 | 功能 | 状态 |
|------|------|------|------|
| PUT | /api/admin/member/{id}/suspend | 暂停会员资格 | ✅ 已提供 |
| PUT | /api/admin/member/{id}/activate | 激活会员资格 | ✅ 已提供 |
| PUT | /api/admin/apply/{id}/audit | 审核会员申请 | ✅ 已提供 |
| GET | /api/admin/member/export | 导出会员列表 | ✅ 已提供 |
| GET | /api/admin/apply/list | 获取申请列表 | ✅ 已提供 |
| DELETE | /api/admin/member/{id} | 删除会员 | ✅ 已提供 |

### 通用接口
| 方法 | 路径 | 功能 | 状态 |
|------|------|------|------|
| GET | /api/common/system-config | 获取系统配置 | ✅ 已提供 |
| GET | /api/common/level-rights | 获取会员权益 | ✅ 已提供 |
| GET | /api/common/announcement | 获取公告 | ✅ 已提供 |
| GET | /api/common/activity-types | 获取活动类型 | ✅ 已提供 |

---

## 后端需要实现的API（前端已实现调用点）

### 一、活动管理模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/activities | 获取活动列表 | ⭐⭐⭐ 高 | 分页、筛选、搜索 |
| POST | /api/admin/activities | 创建活动 | ⭐⭐⭐ 高 | 包含活动详情、时间、地点等 |
| PUT | /api/admin/activities/{id} | 更新活动 | ⭐⭐⭐ 高 | 修改活动信息 |
| DELETE | /api/admin/activities/{id} | 删除活动 | ⭐⭐⭐ 高 | 软删除 |
| PUT | /api/admin/activities/{id}/publish | 发布活动 | ⭐⭐⭐ 高 | 状态变更为已发布 |
| PUT | /api/admin/activities/{id}/cancel | 取消活动 | ⭐⭐⭐ 高 | 状态变更为已取消 |
| GET | /api/admin/activities/{id}/signups | 获取报名列表 | ⭐⭐⭐ 高 | 活动报名人员 |
| PUT | /api/admin/activities/{id}/signups/{userId}/audit | 审核报名 | ⭐⭐⭐ 高 | 通过/拒绝报名 |
| GET | /api/admin/activities/{id}/checkin | 签到数据 | ⭐⭐ 中 | 签到统计 |
| POST | /api/admin/activities/{id}/checkin | 现场签到 | ⭐⭐ 中 | 扫码/手动签到 |

### 二、蓝皮书管理模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/bluebooks | 获取蓝皮书列表 | ⭐⭐⭐ 高 | 分页、年份筛选 |
| POST | /api/admin/bluebooks | 发布蓝皮书 | ⭐⭐⭐ 高 | 上传文件、填写信息 |
| PUT | /api/admin/bluebooks/{id} | 更新蓝皮书 | ⭐⭐ 中 | 修改信息 |
| DELETE | /api/admin/bluebooks/{id} | 删除蓝皮书 | ⭐⭐ 中 | 软删除 |
| GET | /api/admin/bluebooks/{id}/download-stats | 下载统计 | ⭐ 低 | 下载次数统计 |

### 三、公告管理模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/announcements | 获取公告列表 | ⭐⭐⭐ 高 | 分页、类型筛选 |
| POST | /api/admin/announcements | 发布公告 | ⭐⭐⭐ 高 | 创建新公告 |
| PUT | /api/admin/announcements/{id} | 更新公告 | ⭐⭐⭐ 高 | 修改公告 |
| DELETE | /api/admin/announcements/{id} | 删除公告 | ⭐⭐ 中 | 软删除 |
| PUT | /api/admin/announcements/{id}/top | 置顶公告 | ⭐⭐ 中 | 设置置顶状态 |

### 四、权益管理模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/rights | 获取权益列表 | ⭐⭐⭐ 高 | 按会员等级分组 |
| PUT | /api/admin/rights/{level} | 更新权益 | ⭐⭐⭐ 高 | 修改某个等级的权益 |
| POST | /api/admin/rights | 添加权益 | ⭐⭐ 中 | 新增权益项目 |
| DELETE | /api/admin/rights/{id} | 删除权益 | ⭐ 低 | 删除权益项目 |

### 五、数据统计模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/dashboard/overview | 概览数据 | ⭐⭐⭐ 高 | 总会员数、待审核数等 |
| GET | /api/admin/dashboard/member-stats | 会员统计 | ⭐⭐⭐ 高 | 增长趋势、分布数据 |
| GET | /api/admin/dashboard/activity-stats | 活动统计 | ⭐⭐ 中 | 活动参与度 |
| GET | /api/admin/dashboard/geographic | 地域分布 | ⭐⭐ 中 | 会员地域分布 |
| GET | /api/admin/dashboard/business-scope | 业务范畴分布 | ⭐⭐ 中 | 各业务范畴占比 |

### 六、消息推送模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/messages | 获取消息列表 | ⭐⭐⭐ 高 | 历史推送记录 |
| POST | /api/admin/messages | 发送消息 | ⭐⭐⭐ 高 | 站内信/模板消息 |
| GET | /api/admin/messages/stats | 消息统计 | ⭐⭐ 中 | 送达率、打开率 |
| POST | /api/admin/messages/schedule | 定时发送 | ⭐ 低 | 预约发送 |

### 七、权限管理模块（角色权限）

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/roles | 获取角色列表 | ⭐⭐⭐ 高 | 所有角色 |
| POST | /api/admin/roles | 创建角色 | ⭐⭐ 中 | 新增角色 |
| PUT | /api/admin/roles/{id} | 更新角色 | ⭐⭐ 中 | 修改角色信息 |
| DELETE | /api/admin/roles/{id} | 删除角色 | ⭐ 低 | 删除角色 |
| GET | /api/admin/roles/{id}/permissions | 获取角色权限 | ⭐⭐⭐ 高 | 角色的权限列表 |
| PUT | /api/admin/roles/{id}/permissions | 更新角色权限 | ⭐⭐⭐ 高 | 设置权限 |
| GET | /api/admin/permissions | 获取权限列表 | ⭐⭐⭐ 高 | 所有可用权限 |

### 八、后台用户管理模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/users | 获取用户列表 | ⭐⭐⭐ 高 | 后台管理员列表 |
| POST | /api/admin/users | 创建用户 | ⭐⭐ 中 | 添加管理员 |
| PUT | /api/admin/users/{id} | 更新用户 | ⭐⭐ 中 | 修改用户信息 |
| DELETE | /api/admin/users/{id} | 删除用户 | ⭐ 低 | 删除管理员 |
| PUT | /api/admin/users/{id}/status | 修改用户状态 | ⭐⭐ 中 | 启用/禁用 |
| PUT | /api/admin/users/{id}/password | 重置密码 | ⭐ 低 | 密码重置 |

### 九、操作日志模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| GET | /api/admin/logs | 获取操作日志 | ⭐⭐⭐ 高 | 分页、筛选 |
| GET | /api/admin/logs/export | 导出日志 | ⭐ 低 | 导出为Excel |

### 十、系统配置模块

| 方法 | 路径 | 功能 | 优先级 | 说明 |
|------|------|------|--------|------|
| PUT | /api/admin/config | 更新系统配置 | ⭐⭐ 中 | 全局配置项 |
| GET | /api/admin/config/audit | 获取审核配置 | ⭐ 低 | 审核流程设置 |
| PUT | /api/admin/config/audit | 更新审核配置 | ⭐ 低 | 修改审核流程 |

---

## 前端已实现但未调用API的页面

### 已完全实现的页面（纯前端，使用模拟数据）
1. ✅ 管理后台首页 (pages/admin/dashboard/dashboard)
2. ✅ 会员列表 (pages/admin/members/list)
3. ✅ 会员详情 (pages/admin/members/detail)
4. ✅ 申请审核 (pages/admin/members/application)
5. ✅ 活动管理 (pages/admin/activities/list)
6. ✅ 创建活动 (pages/admin/activities/create)
7. ✅ 报名管理 (pages/admin/activities/signup-management)
8. ✅ 现场签到 (pages/admin/activities/onsite-checkin)
9. ✅ 蓝皮书管理 (pages/admin/content/bluebook-management)
10. ✅ 创建蓝皮书 (pages/admin/content/bluebook-create)
11. ✅ 权益管理 (pages/admin/content/rights-management)
12. ✅ 公告管理 (pages/admin/content/announcement-management)
13. ✅ 消息推送 (pages/admin/message/push-management)
14. ✅ 权限管理 (pages/admin/system/permission-management)
15. ✅ 角色详情 (pages/admin/system/role-detail) ⭐ 新增
16. ✅ 系统设置 (pages/admin/system/settings)
17. ✅ 数据分析 (pages/admin/data-analysis/dashboard)

---

## 下一步建议

### 优先级排序

#### 第一阶段（核心功能）
1. 活动管理API (活动CRUD、报名管理)
2. 数据统计API (概览、会员统计)
3. 申请审核API优化（需要补充列表接口参数）

#### 第二阶段（内容管理）
1. 蓝皮书管理API
2. 公告管理API
3. 权益管理API

#### 第三阶段（系统管理）
1. 角色权限API
2. 后台用户管理API
3. 操作日志API

#### 第四阶段（增值功能）
1. 消息推送API
2. 系统配置API
3. 定时任务API
