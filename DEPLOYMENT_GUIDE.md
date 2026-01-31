# 亚洲宠物协会系统部署指南

## 📋 部署前准备

### 1. 服务器环境要求
- Ubuntu 20.04 LTS 或 CentOS 8+
- Docker 20.10+
- Docker Compose 1.29+
- MySQL 8.0+（服务器本地安装）

### 2. 本地数据库准备
确保服务器上的MySQL已创建数据库：
```sql
CREATE DATABASE asian_pets_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 防火墙配置
```bash
# Ubuntu
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 3306/tcp  # 如果需要远程访问数据库
sudo ufw reload

# CentOS
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --permanent --add-port=3306/tcp
sudo firewall-cmd --reload
```

## 🚀 部署步骤

### 方法一：使用部署脚本（推荐）

1. **上传文件到服务器**
```bash
scp -r AsianPetsSystem user@your-server-ip:/home/user/
```

2. **在服务器上执行部署**
```bash
cd /home/user/AsianPetsSystem
chmod +x deploy.sh
./deploy.sh
```

### 方法二：手动部署

1. **构建并启动服务**
```bash
# 构建镜像
docker-compose -f docker-compose.prod.yml build

# 启动服务
docker-compose -f docker-compose.prod.yml up -d
```

2. **查看服务状态**
```bash
docker-compose -f docker-compose.prod.yml ps
docker-compose -f docker-compose.prod.yml logs -f
```

## 🔧 配置说明

### 环境变量配置 (.env文件)
```bash
# JWT密钥（生产环境务必修改）
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production

# 数据库连接（host.docker.internal指向宿主机）
SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/asian_pets_system
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your-db-password
```

### 端口映射
- **80**: Nginx HTTP服务
- **443**: Nginx HTTPS服务（可选）
- **8081**: 应用直接访问端口（调试用）

## 📊 监控和维护

### 查看日志
```bash
# 查看所有服务日志
docker-compose -f docker-compose.prod.yml logs -f

# 查看特定服务日志
docker-compose -f docker-compose.prod.yml logs -f app
docker-compose -f docker-compose.prod.yml logs -f nginx
```

### 服务管理
```bash
# 停止服务
docker-compose -f docker-compose.prod.yml down

# 重启服务
docker-compose -f docker-compose.prod.yml restart

# 更新部署
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml build
docker-compose -f docker-compose.prod.yml up -d
```

### 健康检查
```bash
# 应用健康检查
curl http://localhost:8081/actuator/health

# Nginx健康检查
curl http://localhost/health
```

## 🔒 安全建议

1. **修改默认密码**
   - 更新.env文件中的数据库密码
   - 修改JWT_SECRET为强随机字符串

2. **配置HTTPS**（推荐）
   - 获取SSL证书
   - 修改nginx.conf启用443端口
   - 配置SSL证书路径

3. **数据库安全**
   - 不要使用root用户连接
   - 创建专用数据库用户
   - 限制数据库访问权限

## 🚨 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否运行
   - 验证数据库用户权限
   - 确认防火墙开放3306端口

2. **端口占用**
   ```bash
   # 查看端口占用
   netstat -tlnp | grep :80
   netstat -tlnp | grep :8081
   
   # 杀掉占用进程
   kill -9 <PID>
   ```

3. **容器启动失败**
   ```bash
   # 查看详细错误
   docker-compose -f docker-compose.prod.yml logs app
   
   # 重新构建镜像
   docker-compose -f docker-compose.prod.yml build --no-cache
   ```

## 📈 性能优化

1. **连接池调优**（已在.env中配置）
2. **Nginx缓存配置**（已在nginx.conf中配置）
3. **JVM内存优化**（可在Dockerfile中添加）

## 🔄 备份策略

建议定期备份：
1. 数据库数据
2. 应用配置文件
3. 用户上传的文件

```bash
# 数据库备份示例
mysqldump -u root -p asian_pets_system > backup_$(date +%Y%m%d).sql
```