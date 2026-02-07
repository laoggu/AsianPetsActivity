# Asian Pets System - Docker 生产部署指南

## 📋 部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                         服务器                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                Docker Compose 网络                   │   │
│  │                                                     │   │
│  │   ┌──────────┐      ┌──────────┐      ┌─────────┐  │   │
│  │   │  Nginx   │──────│   App    │──────│  Redis  │  │   │
│  │   │  (443)   │      │  (8081)  │      │ (6379)  │  │   │
│  │   │  (80)    │      └──────────┘      └─────────┘  │   │
│  │   └──────────┘                                     │   │
│  │        │                                           │   │
│  │        ▼                                           │   │
│  │   SSL证书挂载: /opt/ssl/                           │   │
│  └─────────────────────────────────────────────────────┘   │
│                            │                                │
│                            ▼                                │
│                  ┌──────────────────┐                       │
│                  │  MySQL (宿主机)   │                       │
│                  │    3306端口      │                       │
│                  └──────────────────┘                       │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 快速开始

### 1. 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- MySQL 8.0+（已安装在服务器上）
- 域名和 SSL 证书

### 2. 文件结构

```
AsianPetsSystem/
├── docker-compose.yml      # Docker Compose 配置
├── Dockerfile              # 应用镜像构建
├── deploy-docker.sh        # 部署脚本
├── .env                    # 环境变量配置
├── nginx/
│   ├── conf/
│   │   └── nginx.conf      # Nginx 配置（已包含 HTTPS）
│   ├── ssl/
│   │   ├── cailanzikzh.xin.pem    # SSL 证书
│   │   └── cailanzikzh.xin.key    # SSL 私钥
│   └── html/               # 静态页面（可选）
├── redis/
│   └── redis.conf          # Redis 配置
└── uploads/                # 文件上传目录
```

### 3. 环境准备

#### 3.1 配置环境变量

复制并编辑 `.env` 文件：

```bash
cp .env.example .env
vim .env
```

关键配置项：

```env
# JWT 密钥（生产环境必须修改！）
JWT_SECRET=your-production-secret-key-here

# 数据库配置（连接宿主机 MySQL）
# 使用 host.docker.internal 或服务器内网 IP
SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/asian_pets_system
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password
```

#### 3.2 准备 SSL 证书

确保证书文件位于正确位置：

```bash
mkdir -p nginx/ssl
cp /path/to/your/cailanzikzh.xin.pem nginx/ssl/
cp /path/to/your/cailanzikzh.xin.key nginx/ssl/
```

#### 3.3 配置 MySQL 允许 Docker 访问

如果 MySQL 在宿主机上，需要允许 Docker 网络访问：

```sql
-- 登录 MySQL
mysql -u root -p

-- 创建或修改用户，允许从任何主机连接
CREATE USER 'AsainPetsAdmin'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON asian_pets_system.* TO 'AsainPetsAdmin'@'%';
FLUSH PRIVILEGES;

-- 或者修改现有用户的 host
UPDATE mysql.user SET Host='%' WHERE User='AsainPetsAdmin';
FLUSH PRIVILEGES;
```

编辑 MySQL 配置文件（通常是 `/etc/mysql/mysql.conf.d/mysqld.cnf`）：

```ini
# 注释掉或修改 bind-address，允许远程连接
# bind-address = 127.0.0.1
bind-address = 0.0.0.0
```

重启 MySQL：

```bash
sudo systemctl restart mysql
```

### 4. 部署

#### 4.1 使用部署脚本（推荐）

```bash
chmod +x deploy-docker.sh
./deploy-docker.sh
```

#### 4.2 手动部署

```bash
# 构建并启动
docker-compose up -d --build

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 5. 验证部署

```bash
# 检查应用健康状态
curl http://localhost:8081/actuator/health

# 检查 Nginx
curl http://localhost/health
curl -k https://localhost/health  # 如果证书不受信任，使用 -k

# 检查 Redis
docker-compose exec redis redis-cli ping
```

## 🔧 常见问题

### 1. 应用无法连接 MySQL

**症状**: 应用启动失败，日志显示数据库连接错误

**解决方案**:

```bash
# 检查 MySQL 端口是否可访问
telnet localhost 3306

# 检查 .env 中的数据库配置
# 如果使用 host.docker.internal 不工作，尝试使用服务器内网 IP
# 例如：172.17.0.1（Docker 默认网桥 IP）或服务器实际 IP

# 临时使用网络模式（不推荐长期使用）
# 在 docker-compose.yml 中注释掉 networks，添加：
# network_mode: "host"
```

### 2. 证书问题

**症状**: HTTPS 无法访问，Nginx 报错

**解决方案**:

```bash
# 检查证书文件是否存在且格式正确
openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -text -noout
openssl rsa -in nginx/ssl/cailanzikzh.xin.key -check

# 检查证书和密钥是否匹配
openssl x509 -noout -modulus -in nginx/ssl/cailanzikzh.xin.pem | openssl md5
openssl rsa -noout -modulus -in nginx/ssl/cailanzikzh.xin.key | openssl md5
```

### 3. 端口被占用

**症状**: `bind: address already in use`

**解决方案**:

```bash
# 检查端口占用
sudo lsof -i :80
sudo lsof -i :443
sudo lsof -i :8081
sudo lsof -i :6379

# 停止占用服务
sudo systemctl stop nginx  # 如果宿主机有 Nginx
sudo systemctl stop redis  # 如果宿主机有 Redis

# 或者修改 docker-compose.yml 使用其他端口
```

### 4. 数据持久化

Redis 数据默认挂载到 Docker Volume：

```bash
# 查看数据卷
docker volume ls | grep redis

# 如果需要备份 Redis 数据
docker-compose exec redis redis-cli SAVE
docker cp asianpets-redis:/data/dump.rdb /backup/redis/
```

## 📊 监控与维护

### 查看日志

```bash
# 所有服务日志
docker-compose logs -f

# 特定服务
docker-compose logs -f app
docker-compose logs -f nginx
docker-compose logs -f redis
```

### 重启服务

```bash
# 重启所有
docker-compose restart

# 重启单个服务
docker-compose restart app
```

### 更新部署

```bash
# 拉取最新代码
git pull

# 重新构建并部署
./deploy-docker.sh

# 或手动
docker-compose down
docker-compose up -d --build
```

### 备份数据

```bash
# 备份 Redis
docker-compose exec redis redis-cli SAVE
docker cp asianpets-redis:/data/dump.rdb ./backup/$(date +%Y%m%d)_redis.rdb

# 备份 MySQL（在宿主机执行）
mysqldump -u root -p asian_pets_system > ./backup/$(date +%Y%m%d)_db.sql
```

## 🔒 安全建议

1. **修改 JWT 密钥**: 生产环境务必修改 `.env` 中的 `JWT_SECRET`
2. **数据库密码**: 使用强密码，定期更换
3. **防火墙配置**: 只开放必要端口（80、443）
4. **SSL 证书**: 确保证书有效，及时续期
5. **定期更新**: 定期更新 Docker 镜像和基础镜像

## 📝 备注

- Nginx 容器会挂载项目中的 SSL 证书和配置文件
- Redis 数据通过 Docker Volume 持久化
- 应用日志挂载到 `./logs` 目录
- 上传文件挂载到 `./uploads` 目录
