# 亚宠会小程序后端服务

## 项目简介

亚洲宠物协会小程序后端服务，基于Spring Boot 3.2.0 + JPA + MySQL开发。

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+（用于二维码签到、缓存）
- Gradle 8.0+

### 2. 配置数据库

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/asian_pets_system?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  
  redis:
    host: localhost
    port: 6379
```

### 3. 初始化数据库

```bash
mysql -u root -p asian_pets_system < db/schema.sql
```

### 4. 启动服务

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### 5. 访问Swagger文档

启动成功后，访问：

```
http://localhost:8080/swagger-ui.html
```

或

```
http://localhost:8080/swagger-ui/index.html
```

## 主要功能模块

| 模块 | 功能 |
|------|------|
| 会员管理 | 会员CRUD、审核、状态管理 |
| 续费管理 | 续费记录、支付、到期提醒 |
| 活动管理 | 活动CRUD、报名、签到、评价 |
| 扫码签到 | 二维码签到、通用签到码 |
| 蓝皮书管理 | 蓝皮书发布、下载统计 |
| 公告管理 | 公告发布、置顶 |
| 权益管理 | 会员权益配置 |
| 消息推送 | 站内信、批量发送 |
| 数据统计 | 概览、趋势、分布 |
| 批量操作 | 批量更新、批量发送 |
| 权限管理 | 角色、权限控制 |

## API文档

详见 `docs/api-documentation.md`

## 前端开发指南

详见 `docs/frontend-development-guide.md`

## 数据字典

详见 `docs/data-dictionary.md`

## 项目结构

```
src/main/java/org/example/asianpetssystem/
├── common/           # 公共类、枚举
├── config/           # 配置类
├── controller/       # 控制器层
├── dto/              # 数据传输对象
│   ├── request/      # 请求DTO
│   └── response/     # 响应DTO
├── entity/           # 实体类
├── exception/        # 异常处理
├── repository/       # 数据访问层
├── security/         # 安全配置
└── service/          # 业务逻辑层
    └── impl/         # 实现类
```

## 端口配置

默认端口为 **8080**，如需修改：

```yaml
server:
  port: 8080
```

## 联系方式

如有问题，请联系开发团队。
