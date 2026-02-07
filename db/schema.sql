-- =====================================================
-- Asian Pets System Database Schema
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 系统基础表
-- =====================================================

-- 管理员用户表 (修改现有表)
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，DISABLED-禁用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- 角色表
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '权限描述',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '所属模块',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 审核配置表
DROP TABLE IF EXISTS `audit_config`;
CREATE TABLE `audit_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `auto_audit` TINYINT NOT NULL DEFAULT 0 COMMENT '是否自动审核：0-否，1-是',
    `require_materials` TINYINT NOT NULL DEFAULT 1 COMMENT '是否需要材料：0-否，1-是',
    `audit_flow` TEXT COMMENT '审核流程配置JSON',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核配置表';

-- =====================================================
-- 2. 会员相关表
-- =====================================================

-- 会员表 (修改现有表)
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_no` VARCHAR(50) NOT NULL COMMENT '会员编号',
    `company_name` VARCHAR(200) NOT NULL COMMENT '公司名称',
    `company_short_name` VARCHAR(100) DEFAULT NULL COMMENT '公司简称',
    `company_type` VARCHAR(50) DEFAULT NULL COMMENT '公司类型',
    `company_scale` VARCHAR(50) DEFAULT NULL COMMENT '公司规模',
    `business_scope` TEXT COMMENT '业务范围',
    `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
    `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
    `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
    `address` VARCHAR(500) DEFAULT NULL COMMENT '详细地址',
    `website` VARCHAR(200) DEFAULT NULL COMMENT '公司网站',
    `registered_capital` DECIMAL(18,2) DEFAULT NULL COMMENT '注册资本(万元)',
    `established_date` DATE DEFAULT NULL COMMENT '成立日期',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_mobile` VARCHAR(20) NOT NULL COMMENT '联系人手机号',
    `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系人邮箱',
    `password` VARCHAR(100) DEFAULT NULL COMMENT '登录密码',
    `level` VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '会员等级',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
    `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_member_no` (`member_no`),
    UNIQUE KEY `uk_contact_mobile` (`contact_mobile`),
    KEY `idx_company_name` (`company_name`),
    KEY `idx_level` (`level`),
    KEY `idx_status` (`status`),
    KEY `idx_province_city` (`province`, `city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';

-- 会员权益表
DROP TABLE IF EXISTS `member_right`;
CREATE TABLE `member_right` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `level` VARCHAR(20) NOT NULL COMMENT '会员等级',
    `title` VARCHAR(100) NOT NULL COMMENT '权益标题',
    `description` TEXT COMMENT '权益描述',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '权益图标',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_level` (`level`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员权益表';

-- 联系人表
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
    `is_primary` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主要联系人：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_is_primary` (`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系人表';

-- =====================================================
-- 3. 活动相关表
-- =====================================================

-- 活动表
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `description` TEXT COMMENT '活动简介',
    `agenda` TEXT COMMENT '活动议程',
    `location` VARCHAR(300) DEFAULT NULL COMMENT '活动地点',
    `activity_type` VARCHAR(50) NOT NULL COMMENT '活动类型',
    `target_audience` VARCHAR(200) DEFAULT NULL COMMENT '参与对象',
    `max_participants` INT DEFAULT 0 COMMENT '人数限制，0表示不限',
    `fee` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '活动费用',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，CANCELLED-已取消，ENDED-已结束',
    `start_time` DATETIME NOT NULL COMMENT '活动开始时间',
    `end_time` DATETIME NOT NULL COMMENT '活动结束时间',
    `registration_start` DATETIME DEFAULT NULL COMMENT '报名开始时间',
    `registration_end` DATETIME DEFAULT NULL COMMENT '报名结束时间',
    `need_audit` TINYINT NOT NULL DEFAULT 0 COMMENT '是否需要审核：0-否，1-是',
    `created_by` BIGINT NOT NULL COMMENT '创建人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_activity_type` (`activity_type`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- 活动报名表
DROP TABLE IF EXISTS `activity_signup`;
CREATE TABLE `activity_signup` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_mobile` VARCHAR(20) NOT NULL COMMENT '联系人手机号',
    `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系人邮箱',
    `company_name` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝，CANCELLED-已取消',
    `signup_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '报名备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_member` (`activity_id`, `member_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_status` (`status`),
    KEY `idx_signup_time` (`signup_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- 活动签到表
DROP TABLE IF EXISTS `activity_checkin`;
CREATE TABLE `activity_checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `signup_id` BIGINT NOT NULL COMMENT '报名ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `checkin_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
    `checkin_type` VARCHAR(20) NOT NULL DEFAULT 'QR_CODE' COMMENT '签到方式：QR_CODE-二维码，MANUAL-人工',
    `checkin_code` VARCHAR(100) DEFAULT NULL COMMENT '签到码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_member` (`activity_id`, `member_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_signup_id` (`signup_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_checkin_time` (`checkin_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动签到表';

-- =====================================================
-- 4. 内容相关表
-- =====================================================

-- 蓝皮书表
DROP TABLE IF EXISTS `bluebook`;
CREATE TABLE `bluebook` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `year` INT NOT NULL COMMENT '年份',
    `description` TEXT COMMENT '描述',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_size` BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    `download_count` INT NOT NULL DEFAULT 0 COMMENT '下载次数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布',
    `is_member_only` TINYINT NOT NULL DEFAULT 1 COMMENT '是否仅限会员：0-否，1-是',
    `created_by` BIGINT NOT NULL COMMENT '创建人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_year` (`year`),
    KEY `idx_status` (`status`),
    KEY `idx_is_member_only` (`is_member_only`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='蓝皮书表';

-- 公告表
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `type` VARCHAR(20) NOT NULL DEFAULT 'NOTICE' COMMENT '类型：NOTICE-通知，NEWS-新闻，ACTIVITY-活动预告',
    `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `top_order` INT NOT NULL DEFAULT 0 COMMENT '置顶排序',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，ARCHIVED-已归档',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `created_by` BIGINT NOT NULL COMMENT '创建人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_is_top` (`is_top`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- =====================================================
-- 5. 消息相关表
-- =====================================================

-- 消息表
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `type` VARCHAR(20) NOT NULL COMMENT '消息类型：SYSTEM-系统消息，ACTIVITY-活动通知，ANNOUNCEMENT-公告通知',
    `send_type` VARCHAR(20) NOT NULL DEFAULT 'IMMEDIATE' COMMENT '发送类型：IMMEDIATE-立即发送，SCHEDULED-定时发送',
    `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型：ALL-全部会员，LEVEL-指定等级，SPECIFIC-指定会员',
    `target_value` VARCHAR(500) DEFAULT NULL COMMENT '目标值，根据target_type存储对应值',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，SENDING-发送中，SENT-已发送，FAILED-发送失败',
    `scheduled_time` DATETIME DEFAULT NULL COMMENT '定时发送时间',
    `sent_time` DATETIME DEFAULT NULL COMMENT '实际发送时间',
    `total_count` INT NOT NULL DEFAULT 0 COMMENT '总接收人数',
    `success_count` INT NOT NULL DEFAULT 0 COMMENT '发送成功人数',
    `fail_count` INT NOT NULL DEFAULT 0 COMMENT '发送失败人数',
    `created_by` BIGINT NOT NULL COMMENT '创建人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_send_type` (`send_type`),
    KEY `idx_target_type` (`target_type`),
    KEY `idx_scheduled_time` (`scheduled_time`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 消息接收人表
DROP TABLE IF EXISTS `message_recipient`;
CREATE TABLE `message_recipient` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `message_id` BIGINT NOT NULL COMMENT '消息ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'UNREAD' COMMENT '状态：UNREAD-未读，READ-已读',
    `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_member` (`message_id`, `member_id`),
    KEY `idx_message_id` (`message_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息接收人表';

-- =====================================================
-- 6. 附件与日志表
-- =====================================================

-- 附件表
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_type` VARCHAR(100) DEFAULT NULL COMMENT '文件类型',
    `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '所属模块',
    `ref_id` BIGINT DEFAULT NULL COMMENT '关联记录ID',
    `uploaded_by` BIGINT DEFAULT NULL COMMENT '上传人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_module` (`module`),
    KEY `idx_ref_id` (`ref_id`),
    KEY `idx_uploaded_by` (`uploaded_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件表';

-- 审核日志表
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module` VARCHAR(50) NOT NULL COMMENT '模块',
    `ref_id` BIGINT NOT NULL COMMENT '关联记录ID',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型：APPLY-申请，APPROVE-通过，REJECT-拒绝',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_module` (`module`),
    KEY `idx_ref_id` (`ref_id`),
    KEY `idx_action` (`action`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核日志表';

-- =====================================================
-- 7. 外键约束
-- =====================================================

-- 管理员用户外键
ALTER TABLE `admin_user` 
    ADD CONSTRAINT `fk_admin_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- 角色权限外键
ALTER TABLE `role_permission` 
    ADD CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- 联系人外键
ALTER TABLE `contact` 
    ADD CONSTRAINT `fk_contact_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- 活动相关外键
ALTER TABLE `activity_signup` 
    ADD CONSTRAINT `fk_signup_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_signup_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `activity_checkin` 
    ADD CONSTRAINT `fk_checkin_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_checkin_signup` FOREIGN KEY (`signup_id`) REFERENCES `activity_signup` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_checkin_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- 消息相关外键
ALTER TABLE `message_recipient` 
    ADD CONSTRAINT `fk_recipient_message` FOREIGN KEY (`message_id`) REFERENCES `message` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_recipient_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- =====================================================
-- 8. 初始化数据
-- =====================================================

-- 初始化角色
INSERT INTO `role` (`name`, `code`, `description`, `is_active`) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
('普通管理员', 'ADMIN', '普通管理员，拥有部分管理权限', 1),
('运营人员', 'OPERATOR', '负责日常运营管理', 1);

-- 初始化权限（示例）
INSERT INTO `permission` (`name`, `code`, `description`, `module`) VALUES
('会员管理', 'MEMBER_MANAGE', '会员管理相关权限', 'MEMBER'),
('会员审核', 'MEMBER_AUDIT', '会员审核权限', 'MEMBER'),
('活动管理', 'ACTIVITY_MANAGE', '活动管理相关权限', 'ACTIVITY'),
('活动审核', 'ACTIVITY_AUDIT', '活动报名审核权限', 'ACTIVITY'),
('内容管理', 'CONTENT_MANAGE', '内容管理相关权限', 'CONTENT'),
('消息管理', 'MESSAGE_MANAGE', '消息管理相关权限', 'MESSAGE'),
('系统设置', 'SYSTEM_CONFIG', '系统设置权限', 'SYSTEM'),
('角色管理', 'ROLE_MANAGE', '角色管理权限', 'SYSTEM');

-- 初始化超级管理员角色权限（赋予所有权限）
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;

-- 初始化超级管理员账户（密码需要替换为实际加密后的值）
-- 默认密码: admin123，建议使用 BCrypt 加密
INSERT INTO `admin_user` (`username`, `password`, `role_id`, `real_name`, `mobile`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 1, '系统管理员', '13800138000', 'admin@example.com', 'ACTIVE');

-- 初始化审核配置
INSERT INTO `audit_config` (`auto_audit`, `require_materials`, `audit_flow`) VALUES
(0, 1, '[{"step": 1, "name": "初审", "handler": "admin"}, {"step": 2, "name": "复审", "handler": "super_admin"}]');

-- 初始化系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('site_name', '亚洲宠物系统', '网站名称'),
('site_logo', '/static/logo.png', '网站Logo'),
('contact_phone', '400-123-4567', '联系电话'),
('contact_email', 'contact@example.com', '联系邮箱'),
('icp_record', '', 'ICP备案号'),
('copyright', '© 2024 亚洲宠物系统 版权所有', '版权信息');

SET FOREIGN_KEY_CHECKS = 1;
