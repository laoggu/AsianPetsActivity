# Asian Pets System 数据字典

> 文档版本：v1.0  
> 最后更新：2026-02-05  
> 数据库字符集：utf8mb4  
> 存储引擎：InnoDB

---

## 一、表清单

| 序号 | 表名 | 中文名 | 说明 | 记录数估计 |
|:---:|:---|:---|:---|:---:|
| 1 | admin_user | 管理员用户表 | 存储后台管理系统用户账号信息 | 10-50 |
| 2 | member | 会员表 | 存储企业会员基本信息 | 1,000-10,000 |
| 3 | contact | 联系人表 | 存储会员关联的联系人信息 | 2,000-20,000 |
| 4 | attachment | 附件表 | 存储系统上传的附件文件信息 | 5,000-50,000 |
| 5 | audit_log | 审核日志表 | 记录各类审核操作日志 | 10,000+ |
| 6 | activity | 活动表 | 存储活动基本信息 | 100-1,000 |
| 7 | activity_signup | 活动报名表 | 存储会员活动报名信息 | 1,000-10,000 |
| 8 | activity_checkin | 活动签到表 | 存储活动签到记录 | 500-5,000 |
| 9 | bluebook | 蓝皮书表 | 存储行业蓝皮书/白皮书资料 | 50-500 |
| 10 | announcement | 公告表 | 存储系统公告和新闻 | 100-1,000 |
| 11 | member_right | 会员权益表 | 存储各等级会员权益配置 | 10-50 |
| 12 | role | 角色表 | 存储管理员角色定义 | 5-20 |
| 13 | permission | 权限表 | 存储系统权限定义 | 20-100 |
| 14 | role_permission | 角色权限关联表 | 角色与权限的多对多关联 | 50-200 |
| 15 | message | 消息表 | 存储系统消息/通知 | 1,000-10,000 |
| 16 | message_recipient | 消息接收人表 | 存储消息与接收人的关联 | 10,000+ |
| 17 | system_config | 系统配置表 | 存储系统运行时配置项 | 20-100 |
| 18 | audit_config | 审核配置表 | 存储审核流程配置 | 1-5 |

---

## 二、详细字段说明

### 1. admin_user - 管理员用户表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID，唯一标识 |
| username | VARCHAR(50) | 否 | - | 用户名，用于登录 |
| password | VARCHAR(100) | 否 | - | 加密后的登录密码 |
| role_id | BIGINT | 是 | NULL | 关联角色ID |
| real_name | VARCHAR(50) | 是 | NULL | 真实姓名 |
| mobile | VARCHAR(20) | 是 | NULL | 手机号 |
| email | VARCHAR(100) | 是 | NULL | 邮箱地址 |
| status | VARCHAR(20) | 否 | 'ACTIVE' | 状态：ACTIVE-启用，DISABLED-禁用 |
| last_login_time | DATETIME | 是 | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(50) | 是 | NULL | 最后登录IP地址 |
| is_deleted | TINYINT | 否 | 0 | 软删除标记：0-否，1-是 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 记录创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 记录更新时间 |

---

### 2. member - 会员表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| member_no | VARCHAR(50) | 否 | - | 会员编号，唯一标识 |
| company_name | VARCHAR(200) | 否 | - | 公司全称 |
| company_short_name | VARCHAR(100) | 是 | NULL | 公司简称 |
| company_type | VARCHAR(50) | 是 | NULL | 公司类型 |
| company_scale | VARCHAR(50) | 是 | NULL | 公司规模 |
| business_scope | TEXT | 是 | NULL | 业务范围描述 |
| province | VARCHAR(50) | 是 | NULL | 所在省份 |
| city | VARCHAR(50) | 是 | NULL | 所在城市 |
| district | VARCHAR(50) | 是 | NULL | 所在区县 |
| address | VARCHAR(500) | 是 | NULL | 详细地址 |
| website | VARCHAR(200) | 是 | NULL | 公司官网 |
| registered_capital | DECIMAL(18,2) | 是 | NULL | 注册资本（万元） |
| established_date | DATE | 是 | NULL | 成立日期 |
| contact_name | VARCHAR(50) | 否 | - | 主要联系人姓名 |
| contact_mobile | VARCHAR(20) | 否 | - | 联系人手机号，唯一 |
| contact_email | VARCHAR(100) | 是 | NULL | 联系人邮箱 |
| password | VARCHAR(100) | 是 | NULL | 登录密码 |
| level | VARCHAR(20) | 否 | 'NORMAL' | 会员等级，见枚举值说明 |
| status | VARCHAR(20) | 否 | 'PENDING' | 审核状态，见枚举值说明 |
| audit_remark | VARCHAR(500) | 是 | NULL | 审核备注/原因 |
| is_deleted | TINYINT | 否 | 0 | 软删除标记 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 3. contact - 联系人表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| member_id | BIGINT | 否 | - | 所属会员ID |
| name | VARCHAR(50) | 否 | - | 联系人姓名 |
| mobile | VARCHAR(20) | 是 | NULL | 联系人手机号 |
| email | VARCHAR(100) | 是 | NULL | 联系人邮箱 |
| position | VARCHAR(50) | 是 | NULL | 职位/头衔 |
| is_primary | TINYINT | 否 | 0 | 是否主要联系人：0-否，1-是 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 4. attachment - 附件表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| file_name | VARCHAR(255) | 否 | - | 原始文件名 |
| file_url | VARCHAR(500) | 否 | - | 文件存储URL |
| file_type | VARCHAR(100) | 是 | NULL | 文件MIME类型 |
| file_size | BIGINT | 否 | 0 | 文件大小（字节） |
| module | VARCHAR(50) | 是 | NULL | 所属业务模块 |
| ref_id | BIGINT | 是 | NULL | 关联记录ID |
| uploaded_by | BIGINT | 是 | NULL | 上传人ID |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 上传时间 |

---

### 5. audit_log - 审核日志表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| module | VARCHAR(50) | 否 | - | 业务模块标识 |
| ref_id | BIGINT | 否 | - | 关联记录ID |
| action | VARCHAR(50) | 否 | - | 操作类型：APPLY/APPROVE/REJECT |
| operator_id | BIGINT | 否 | - | 操作人ID |
| operator_name | VARCHAR(50) | 是 | NULL | 操作人姓名 |
| remark | VARCHAR(500) | 是 | NULL | 操作备注 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 操作时间 |

---

### 6. activity - 活动表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| title | VARCHAR(200) | 否 | - | 活动标题 |
| cover_image | VARCHAR(500) | 是 | NULL | 封面图片URL |
| description | TEXT | 是 | NULL | 活动简介 |
| agenda | TEXT | 是 | NULL | 活动议程 |
| location | VARCHAR(300) | 是 | NULL | 活动地点 |
| activity_type | VARCHAR(50) | 否 | - | 活动类型 |
| target_audience | VARCHAR(200) | 是 | NULL | 参与对象描述 |
| max_participants | INT | 是 | 0 | 人数限制，0表示不限 |
| fee | DECIMAL(10,2) | 否 | 0.00 | 活动费用 |
| status | VARCHAR(20) | 否 | 'DRAFT' | 活动状态，见枚举值说明 |
| start_time | DATETIME | 否 | - | 活动开始时间 |
| end_time | DATETIME | 否 | - | 活动结束时间 |
| registration_start | DATETIME | 是 | NULL | 报名开始时间 |
| registration_end | DATETIME | 是 | NULL | 报名结束时间 |
| need_audit | TINYINT | 否 | 0 | 报名是否需要审核 |
| created_by | BIGINT | 否 | - | 创建人ID |
| is_deleted | TINYINT | 否 | 0 | 软删除标记 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 7. activity_signup - 活动报名表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| activity_id | BIGINT | 否 | - | 活动ID |
| member_id | BIGINT | 否 | - | 报名会员ID |
| contact_name | VARCHAR(50) | 否 | - | 联系人姓名 |
| contact_mobile | VARCHAR(20) | 否 | - | 联系人手机号 |
| contact_email | VARCHAR(100) | 是 | NULL | 联系人邮箱 |
| company_name | VARCHAR(200) | 是 | NULL | 公司名称 |
| status | VARCHAR(20) | 否 | 'PENDING' | 报名状态，见枚举值说明 |
| signup_time | DATETIME | 否 | CURRENT_TIMESTAMP | 报名时间 |
| audit_time | DATETIME | 是 | NULL | 审核时间 |
| audit_remark | VARCHAR(500) | 是 | NULL | 审核备注 |
| remark | VARCHAR(500) | 是 | NULL | 报名备注 |

---

### 8. activity_checkin - 活动签到表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| activity_id | BIGINT | 否 | - | 活动ID |
| signup_id | BIGINT | 否 | - | 报名记录ID |
| member_id | BIGINT | 否 | - | 会员ID |
| checkin_time | DATETIME | 否 | CURRENT_TIMESTAMP | 签到时间 |
| checkin_type | VARCHAR(20) | 否 | 'QR_CODE' | 签到方式：QR_CODE-二维码，MANUAL-人工 |
| checkin_code | VARCHAR(100) | 是 | NULL | 签到码 |

---

### 9. bluebook - 蓝皮书表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| title | VARCHAR(200) | 否 | - | 蓝皮书标题 |
| year | INT | 否 | - | 所属年份 |
| description | TEXT | 是 | NULL | 描述/摘要 |
| file_url | VARCHAR(500) | 否 | - | 文件下载URL |
| file_size | BIGINT | 是 | 0 | 文件大小（字节） |
| download_count | INT | 否 | 0 | 下载次数统计 |
| status | VARCHAR(20) | 否 | 'PUBLISHED' | 状态：DRAFT/PUBLISHED |
| is_member_only | TINYINT | 否 | 1 | 是否仅限会员下载 |
| created_by | BIGINT | 否 | - | 创建人ID |
| is_deleted | TINYINT | 否 | 0 | 软删除标记 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 10. announcement - 公告表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| title | VARCHAR(200) | 否 | - | 公告标题 |
| content | TEXT | 否 | - | 公告内容（支持富文本） |
| type | VARCHAR(20) | 否 | 'NOTICE' | 公告类型，见枚举值说明 |
| is_top | TINYINT | 否 | 0 | 是否置顶 |
| top_order | INT | 否 | 0 | 置顶排序号 |
| publish_time | DATETIME | 是 | NULL | 发布时间 |
| status | VARCHAR(20) | 否 | 'DRAFT' | 状态：DRAFT/PUBLISHED/ARCHIVED |
| view_count | INT | 否 | 0 | 浏览次数 |
| created_by | BIGINT | 否 | - | 创建人ID |
| is_deleted | TINYINT | 否 | 0 | 软删除标记 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 11. member_right - 会员权益表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| level | VARCHAR(20) | 否 | - | 适用会员等级 |
| title | VARCHAR(100) | 否 | - | 权益标题 |
| description | TEXT | 是 | NULL | 权益详细描述 |
| icon | VARCHAR(255) | 是 | NULL | 权益图标URL |
| sort_order | INT | 否 | 0 | 显示排序号 |
| is_active | TINYINT | 否 | 1 | 是否启用 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 12. role - 角色表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| name | VARCHAR(50) | 否 | - | 角色名称（显示用） |
| code | VARCHAR(50) | 否 | - | 角色编码（系统标识） |
| description | VARCHAR(255) | 是 | NULL | 角色描述 |
| is_active | TINYINT | 否 | 1 | 是否启用 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 13. permission - 权限表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| name | VARCHAR(50) | 否 | - | 权限名称 |
| code | VARCHAR(100) | 否 | - | 权限编码，用于代码控制 |
| description | VARCHAR(255) | 是 | NULL | 权限描述 |
| module | VARCHAR(50) | 是 | NULL | 所属模块 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |

---

### 14. role_permission - 角色权限关联表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| role_id | BIGINT | 否 | - | 角色ID |
| permission_id | BIGINT | 否 | - | 权限ID |

---

### 15. message - 消息表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| title | VARCHAR(200) | 否 | - | 消息标题 |
| content | TEXT | 否 | - | 消息内容 |
| type | VARCHAR(20) | 否 | - | 消息类型，见枚举值说明 |
| send_type | VARCHAR(20) | 否 | 'IMMEDIATE' | 发送类型：IMMEDIATE/SCHEDULED |
| target_type | VARCHAR(20) | 否 | - | 目标类型：ALL/LEVEL/SPECIFIC |
| target_value | VARCHAR(500) | 是 | NULL | 目标值，根据target_type存储 |
| status | VARCHAR(20) | 否 | 'DRAFT' | 消息状态，见枚举值说明 |
| scheduled_time | DATETIME | 是 | NULL | 定时发送时间 |
| sent_time | DATETIME | 是 | NULL | 实际发送时间 |
| total_count | INT | 否 | 0 | 总接收人数 |
| success_count | INT | 否 | 0 | 发送成功人数 |
| fail_count | INT | 否 | 0 | 发送失败人数 |
| created_by | BIGINT | 否 | - | 创建人ID |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 16. message_recipient - 消息接收人表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| message_id | BIGINT | 否 | - | 消息ID |
| member_id | BIGINT | 否 | - | 接收会员ID |
| status | VARCHAR(20) | 否 | 'UNREAD' | 阅读状态：UNREAD/READ |
| read_time | DATETIME | 是 | NULL | 阅读时间 |

---

### 17. system_config - 系统配置表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| config_key | VARCHAR(100) | 否 | - | 配置键，唯一标识 |
| config_value | TEXT | 是 | NULL | 配置值 |
| description | VARCHAR(255) | 是 | NULL | 配置说明 |
| updated_by | BIGINT | 是 | NULL | 最后更新人ID |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 18. audit_config - 审核配置表

| 字段名 | 数据类型 | 是否可空 | 默认值 | 说明/用途 |
|:---|:---|:---:|:---|:---|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键ID |
| auto_audit | TINYINT | 否 | 0 | 是否自动审核：0-否，1-是 |
| require_materials | TINYINT | 否 | 1 | 是否需要审核材料 |
| audit_flow | TEXT | 是 | NULL | 审核流程配置JSON |
| updated_by | BIGINT | 是 | NULL | 最后更新人ID |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

## 三、枚举值说明

### 3.1 member.level - 会员等级

| 枚举值 | 中文名 | 说明 |
|:---:|:---|:---|
| NORMAL | 普通会员 | 基础会员等级 |
| REGULAR | 标准会员 | 标准会员等级 |
| GOLD | 黄金会员 | 黄金会员等级，享有更多权益 |
| PLATINUM | 白金会员 | 白金会员等级，最高权益 |

### 3.2 member.status - 会员审核状态

| 枚举值 | 中文名 | 说明 |
|:---:|:---|:---|
| PENDING | 待审核 | 会员申请已提交，等待审核 |
| APPROVED | 已通过 | 会员申请已通过审核 |
| REJECTED | 已拒绝 | 会员申请未通过审核 |
| SUSPENDED | 已暂停 | 会员资格被暂停 |

### 3.3 activity.status - 活动状态

| 枚举值 | 中文名 | 说明 |
|:---:|:---|:---|
| DRAFT | 草稿 | 活动编辑中，未发布 |
| PUBLISHED | 已发布 | 活动已发布，可报名 |
| CANCELLED | 已取消 | 活动已取消 |
| ENDED | 已结束 | 活动已结束 |

### 3.4 activity_signup.status - 报名状态

| 枚举值 | 中文名 | 说明 |
|:---:|:---|:---|
| PENDING | 待审核 | 报名已提交，等待审核 |
| APPROVED | 已通过 | 报名已通过审核 |
| REJECTED | 已拒绝 | 报名未通过审核 |
| CANCELLED | 已取消 | 报名已取消 |

### 3.5 announcement.type - 公告类型

| 枚举值 | 中文名 | 说明 |
|:---:|:---|:---|
| SYSTEM | 系统公告 | 系统重要通知 |
| ACTIVITY | 活动公告 | 活动相关公告 |
| BLUEBOOK | 蓝皮书公告 | 蓝皮书发布相关公告 |
| NOTICE | 通知 | 普通通知 |
| NEWS | 新闻 | 行业新闻 |

### 3.6 message.type - 消息类型

| 枚举值 | 中文名 | 说明 |
|:---:|:---|:---|
| SYSTEM | 系统消息 | 系统发送的通知 |
| ACTIVITY | 活动通知 | 活动相关通知 |
| BLUEBOOK | 蓝皮书通知 | 蓝皮书发布通知 |
| ANNOUNCEMENT | 公告通知 | 公告相关通知 |

### 3.7 message.status - 消息发送状态

| 枚举值 | 中文名 | 说明 |
|:---:|:---|:---|
| DRAFT | 草稿 | 消息编辑中 |
| SENDING | 发送中 | 消息正在发送 |
| SENT | 已发送 | 消息已发送完成 |
| FAILED | 发送失败 | 消息发送失败 |

### 3.8 其他常用枚举

| 字段 | 枚举值 | 中文名 | 说明 |
|:---|:---:|:---|:---|
| admin_user.status | ACTIVE | 启用 | 账号正常使用 |
| admin_user.status | DISABLED | 禁用 | 账号已被禁用 |
| message_recipient.status | UNREAD | 未读 | 消息未阅读 |
| message_recipient.status | READ | 已读 | 消息已阅读 |
| bluebook.status | DRAFT | 草稿 | 资料编辑中 |
| bluebook.status | PUBLISHED | 已发布 | 资料已发布 |
| activity_checkin.checkin_type | QR_CODE | 二维码 | 通过二维码签到 |
| activity_checkin.checkin_type | MANUAL | 人工 | 人工手动签到 |
| message.send_type | IMMEDIATE | 立即发送 | 消息立即发送 |
| message.send_type | SCHEDULED | 定时发送 | 消息定时发送 |
| message.target_type | ALL | 全部会员 | 发送给所有会员 |
| message.target_type | LEVEL | 指定等级 | 发送给指定等级会员 |
| message.target_type | SPECIFIC | 指定会员 | 发送给指定会员 |

---

## 四、索引清单

### 4.1 admin_user - 管理员用户表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_username | 唯一 | username | 用户名唯一 |
| idx_role_id | 普通 | role_id | 角色ID索引 |
| idx_status | 普通 | status | 状态索引 |

### 4.2 member - 会员表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_member_no | 唯一 | member_no | 会员编号唯一 |
| uk_contact_mobile | 唯一 | contact_mobile | 手机号唯一 |
| idx_company_name | 普通 | company_name | 公司名称索引 |
| idx_level | 普通 | level | 会员等级索引 |
| idx_status | 普通 | status | 审核状态索引 |
| idx_province_city | 普通 | province, city | 省市联合索引 |

### 4.3 contact - 联系人表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_member_id | 普通 | member_id | 会员ID索引 |
| idx_is_primary | 普通 | is_primary | 主要联系人索引 |

### 4.4 attachment - 附件表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_module | 普通 | module | 模块索引 |
| idx_ref_id | 普通 | ref_id | 关联ID索引 |
| idx_uploaded_by | 普通 | uploaded_by | 上传人索引 |

### 4.5 audit_log - 审核日志表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_module | 普通 | module | 模块索引 |
| idx_ref_id | 普通 | ref_id | 关联ID索引 |
| idx_action | 普通 | action | 操作类型索引 |
| idx_operator_id | 普通 | operator_id | 操作人索引 |
| idx_created_at | 普通 | created_at | 时间索引 |

### 4.6 activity - 活动表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_status | 普通 | status | 状态索引 |
| idx_activity_type | 普通 | activity_type | 活动类型索引 |
| idx_start_time | 普通 | start_time | 开始时间索引 |
| idx_created_by | 普通 | created_by | 创建人索引 |
| idx_is_deleted | 普通 | is_deleted | 删除标记索引 |
| idx_time_range | 普通 | start_time, end_time | 时间范围索引 |

### 4.7 activity_signup - 活动报名表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_activity_member | 唯一 | activity_id, member_id | 活动会员唯一 |
| idx_activity_id | 普通 | activity_id | 活动ID索引 |
| idx_member_id | 普通 | member_id | 会员ID索引 |
| idx_status | 普通 | status | 报名状态索引 |
| idx_signup_time | 普通 | signup_time | 报名时间索引 |

### 4.8 activity_checkin - 活动签到表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_activity_member | 唯一 | activity_id, member_id | 活动会员唯一 |
| idx_activity_id | 普通 | activity_id | 活动ID索引 |
| idx_signup_id | 普通 | signup_id | 报名ID索引 |
| idx_member_id | 普通 | member_id | 会员ID索引 |
| idx_checkin_time | 普通 | checkin_time | 签到时间索引 |

### 4.9 bluebook - 蓝皮书表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_year | 普通 | year | 年份索引 |
| idx_status | 普通 | status | 状态索引 |
| idx_is_member_only | 普通 | is_member_only | 会员专享索引 |
| idx_is_deleted | 普通 | is_deleted | 删除标记索引 |
| idx_created_by | 普通 | created_by | 创建人索引 |

### 4.10 announcement - 公告表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_type | 普通 | type | 类型索引 |
| idx_is_top | 普通 | is_top | 置顶索引 |
| idx_status | 普通 | status | 状态索引 |
| idx_publish_time | 普通 | publish_time | 发布时间索引 |
| idx_is_deleted | 普通 | is_deleted | 删除标记索引 |
| idx_created_by | 普通 | created_by | 创建人索引 |

### 4.11 member_right - 会员权益表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_level | 普通 | level | 会员等级索引 |
| idx_is_active | 普通 | is_active | 启用状态索引 |
| idx_sort_order | 普通 | sort_order | 排序索引 |

### 4.12 role - 角色表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_code | 唯一 | code | 角色编码唯一 |
| idx_is_active | 普通 | is_active | 启用状态索引 |

### 4.13 permission - 权限表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_code | 唯一 | code | 权限编码唯一 |
| idx_module | 普通 | module | 模块索引 |

### 4.14 role_permission - 角色权限关联表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_role_permission | 唯一 | role_id, permission_id | 角色权限联合唯一 |
| idx_permission_id | 普通 | permission_id | 权限ID索引 |

### 4.15 message - 消息表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| idx_type | 普通 | type | 消息类型索引 |
| idx_status | 普通 | status | 状态索引 |
| idx_send_type | 普通 | send_type | 发送类型索引 |
| idx_target_type | 普通 | target_type | 目标类型索引 |
| idx_scheduled_time | 普通 | scheduled_time | 定时时间索引 |
| idx_created_by | 普通 | created_by | 创建人索引 |

### 4.16 message_recipient - 消息接收人表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_message_member | 唯一 | message_id, member_id | 消息会员联合唯一 |
| idx_message_id | 普通 | message_id | 消息ID索引 |
| idx_member_id | 普通 | member_id | 会员ID索引 |
| idx_status | 普通 | status | 阅读状态索引 |

### 4.17 system_config - 系统配置表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |
| uk_config_key | 唯一 | config_key | 配置键唯一 |

### 4.18 audit_config - 审核配置表

| 索引名 | 类型 | 字段 | 说明 |
|:---|:---:|:---|:---|
| PRIMARY | 主键 | id | 主键索引 |

---

## 五、外键关系

### 5.1 外键关系图（文字表示）

```
┌─────────────┐         ┌─────────────┐         ┌─────────────────┐
│    role     │◄────────│  admin_user │         │   permission    │
│   (角色表)   │    1:N  │ (管理员用户表)│         │    (权限表)      │
└──────┬──────┘         └─────────────┘         └────────┬────────┘
       │                                                  │
       │  N:M                                             │
       └──────────────────┬───────────────────────────────┘
                          │
                   ┌──────┴──────┐
                   │role_permission│
                   │(角色权限关联表)│
                   └─────────────┘


┌─────────────┐         ┌─────────────┐         ┌─────────────────┐
│   member    │◄────────│   contact   │         │  activity_signup │
│   (会员表)   │   1:N   │  (联系人表)  │◄────────│  (活动报名表)    │
└──────┬──────┘         └─────────────┘   N:1   └────────┬────────┘
       │                                                  │
       │                                                  │ 1:1
       │                                          ┌──────┴──────┐
       │                                          │activity_checkin│
       │                                          │  (活动签到表)  │
       │                                          └─────────────┘
       │
       │ N:M                                  ┌─────────────────┐
       └──────────────────┬───────────────────►│    message      │
                          │                    │    (消息表)      │
                   ┌──────┴──────┐             └────────┬────────┘
                   │message_recipient│                   │
                   │(消息接收人表)   │◄──────────────────┘
                   └─────────────┘              1:N


┌─────────────┐         ┌─────────────┐         ┌─────────────────┐
│  activity   │◄────────│activity_signup│       │   bluebook      │
│   (活动表)   │   1:N   │  (活动报名表)  │       │   (蓝皮书表)     │
└─────────────┘         └─────────────┘         └─────────────────┘


┌─────────────┐         ┌─────────────────┐     ┌─────────────────┐
│ announcement│         │   system_config │     │   audit_config  │
│   (公告表)   │         │   (系统配置表)   │     │   (审核配置表)   │
└─────────────┘         └─────────────────┘     └─────────────────┘


┌─────────────┐         ┌─────────────────┐
│  audit_log  │         │   attachment    │
│  (审核日志表) │         │    (附件表)      │
└─────────────┘         └─────────────────┘
```

### 5.2 外键关系明细表

| 序号 | 外键名 | 子表 | 子表字段 | 父表 | 父表字段 | 删除策略 | 更新策略 |
|:---:|:---|:---|:---|:---|:---|:---:|:---:|
| 1 | fk_admin_user_role | admin_user | role_id | role | id | SET NULL | CASCADE |
| 2 | fk_role_permission_role | role_permission | role_id | role | id | CASCADE | CASCADE |
| 3 | fk_role_permission_permission | role_permission | permission_id | permission | id | CASCADE | CASCADE |
| 4 | fk_contact_member | contact | member_id | member | id | CASCADE | CASCADE |
| 5 | fk_signup_activity | activity_signup | activity_id | activity | id | CASCADE | CASCADE |
| 6 | fk_signup_member | activity_signup | member_id | member | id | CASCADE | CASCADE |
| 7 | fk_checkin_activity | activity_checkin | activity_id | activity | id | CASCADE | CASCADE |
| 8 | fk_checkin_signup | activity_checkin | signup_id | activity_signup | id | CASCADE | CASCADE |
| 9 | fk_checkin_member | activity_checkin | member_id | member | id | CASCADE | CASCADE |
| 10 | fk_recipient_message | message_recipient | message_id | message | id | CASCADE | CASCADE |
| 11 | fk_recipient_member | message_recipient | member_id | member | id | CASCADE | CASCADE |

### 5.3 关联关系说明

| 关联类型 | 父表 | 子表 | 业务说明 |
|:---|:---|:---|:---|
| 一对多 | role | admin_user | 一个角色可分配给多个管理员 |
| 多对多 | role + permission | role_permission | 角色与权限的关联关系 |
| 一对多 | member | contact | 一个会员可有多个联系人 |
| 一对多 | member | activity_signup | 一个会员可报名多个活动 |
| 一对多 | activity | activity_signup | 一个活动可有多个报名 |
| 一对一 | activity_signup | activity_checkin | 一个报名对应一个签到记录 |
| 一对多 | activity | activity_checkin | 一个活动可有多个签到 |
| 一对多 | member | activity_checkin | 一个会员可签到多个活动 |
| 一对多 | member | message_recipient | 一个会员可接收多条消息 |
| 一对多 | message | message_recipient | 一条消息可发送给多个会员 |

---

## 六、设计规范

### 6.1 命名规范

| 类型 | 规范 | 示例 |
|:---|:---|:---|
| 表名 | 小写，单数形式，下划线分隔 | `admin_user`, `member_right` |
| 字段名 | 小写，下划线分隔 | `created_at`, `is_deleted` |
| 索引名 | 前缀+下划线+描述 | `uk_username`, `idx_status` |
| 外键名 | fk_子表_父表 | `fk_contact_member` |

### 6.2 通用字段说明

以下字段出现在大多数表中，统一说明：

| 字段名 | 说明 |
|:---|:---|
| id | 主键，自增BIGINT |
| is_deleted | 软删除标记，0-正常，1-已删除 |
| created_at | 记录创建时间，自动填充 |
| updated_at | 记录最后更新时间，自动更新 |
| created_by | 创建人ID，关联admin_user.id |
| updated_by | 更新人ID，关联admin_user.id |

### 6.3 索引命名规范

| 前缀 | 含义 | 示例 |
|:---|:---|:---|
| pk_ | 主键索引（实际使用PRIMARY） | PRIMARY |
| uk_ | 唯一索引 | `uk_username` |
| idx_ | 普通索引 | `idx_status` |
| fk_ | 外键约束 | `fk_contact_member` |

---

*文档结束*
