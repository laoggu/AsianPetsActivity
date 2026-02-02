# 🚀 亚洲宠物协会系统服务器部署指南

## 📋 服务器信息
- **域名**: cailanzikzh.xin
- **服务器IP**: 101.43.57.35
- **部署路径**: /opt/AsianPetsActivity

## 🔧 服务器准备工作

### 1. 连接服务器
```bash
ssh ubuntu@101.43.57.35
```

### 2. 系统更新和基础软件安装
```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y curl wget git unzip openssl
```

### 3. 安装Docker和Docker Compose
```bash
# 安装Docker
sudo apt install -y docker.io

# 启动并启用Docker服务
sudo systemctl start docker
sudo systemctl enable docker

# 将当前用户添加到docker组
sudo usermod -aG docker $USER

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

### 4. 配置数据库
```bash
# 安装MySQL
sudo apt install -y mysql-server

# 启动MySQL服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 登录MySQL并创建数据库和用户
sudo mysql -u root -p

# 在MySQL中执行以下命令：
CREATE DATABASE asian_pets_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'AsainPetsAdmin'@'localhost' IDENTIFIED BY 'Yk731207';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER ON asian_pets_system.* TO 'AsainPetsAdmin'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 5. 导入初始数据（如果需要）
```bash
# 将数据库备份文件上传到服务器
# scp db/Dump20260130.sql ubuntu@101.43.57.35:/tmp/

# 导入数据
mysql -u AsainPetsAdmin -p asian_pets_system < /tmp/Dump20260130.sql
```

## 📦 项目部署

### 1. 上传项目文件
```bash
# 在本地Windows机器上打包项目
cd C:\Users\14199\AsianPetsSystem
Compress-Archive -Path .\* -DestinationPath AsianPetsSystem.zip

# 上传到服务器
scp AsianPetsSystem.zip ubuntu@101.43.57.35:/home/ubuntu/
```

### 2. 在服务器上解压和配置
```bash
# 解压项目文件
cd /home/ubuntu
unzip AsianPetsSystem.zip -d AsianPetsActivity
cd AsianPetsActivity

# 设置执行权限
chmod +x deploy.sh deploy-https.sh jwt-config-tool.sh

# 生成安全的JWT密钥
./jwt-config-tool.sh
# 选择选项4执行完整流程
```

### 3. 配置环境变量
```bash
# 编辑.env文件，确保配置正确
nano .env

# 确认以下关键配置：
# JWT_SECRET=（使用jwt-config-tool.sh生成的安全密钥）
# SPRING_DATASOURCE_PASSWORD=Yk731207
```

## 🔐 HTTPS部署（推荐）

### 1. 执行HTTPS部署
```bash
# 运行HTTPS部署脚本
./deploy-https.sh cailanzikzh.xin
```

### 2. 手动配置HTTPS（如果自动部署失败）
```bash
# 安装Certbot
sudo apt install -y certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d cailanzikzh.xin -d www.cailanzikzh.xin --non-interactive --agree-tos --email admin@cailanzikzh.xin

# 使用HTTPS配置文件
sudo cp nginx/conf/nginx-https.conf /etc/nginx/nginx.conf
sudo nginx -t
sudo systemctl restart nginx
```

## 🚀 HTTP部署（备选方案）

如果HTTPS部署有问题，可以先使用HTTP部署：

```bash
# 执行HTTP部署
./deploy.sh
```

## 🧪 部署验证

### 1. 检查服务状态
```bash
# 查看容器运行状态
docker-compose -f docker-compose.prod.yml ps

# 查看应用日志
docker-compose -f docker-compose.prod.yml logs app

# 查看Nginx日志
tail -f nginx/logs/access.log
```

### 2. 健康检查
```bash
# 本地健康检查
curl -f http://localhost:8081/actuator/health
curl -f http://localhost/health

# 公网访问测试
curl -I http://101.43.57.35:8081/actuator/health
curl -I http://101.43.57.35/swagger-ui/index.html

# HTTPS访问测试（如果配置了HTTPS）
curl -I https://cailanzikzh.xin/health
curl -I https://cailanzikzh.xin/swagger-ui/index.html
```

## 🔧 常见问题处理

### 1. 数据库连接问题
```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 测试数据库连接
mysql -u AsainPetsAdmin -p -e "SHOW DATABASES;"
```

### 2. Docker容器问题
```bash
# 查看容器日志
docker-compose logs app

# 重新构建镜像
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.prod.yml up -d
```

### 3. Nginx配置问题
```bash
# 测试Nginx配置
sudo nginx -t

# 重新加载Nginx配置
sudo systemctl reload nginx

# 查看Nginx错误日志
sudo tail -f /var/log/nginx/error.log
```

## 🔒 安全配置

### 1. 防火墙配置
```bash
# 配置UFW防火墙
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw enable
```

### 2. SSL证书自动续期
```bash
# 设置自动续期（已在deploy-https.sh中配置）
sudo crontab -l
# 应该看到：0 12 * * * /usr/bin/certbot renew --quiet
```

## 📊 监控和维护

### 1. 查看系统资源
```bash
# 查看磁盘使用情况
df -h

# 查看内存使用情况
free -h

# 查看CPU使用情况
top
```

### 2. 日志管理
```bash
# 查看应用日志
docker-compose logs app --tail=100 -f

# 查看Nginx访问日志
tail -f nginx/logs/access.log

# 查看系统日志
journalctl -u docker -f
```

## 🔄 更新部署

当需要更新应用时：

```bash
# 拉取最新代码（如果是Git仓库）
git pull

# 重新部署
./deploy.sh
# 或者HTTPS部署
./deploy-https.sh cailanzikzh.xin
```

## 🎯 最终访问地址

部署成功后，可以通过以下地址访问：

- **Swagger API文档**: https://cailanzikzh.xin/swagger-ui/index.html
- **健康检查**: https://cailanzikzh.xin/health
- **API接口**: https://cailanzikzh.xin/api/

如果HTTPS配置不成功，也可以通过HTTP访问：
- **Swagger API文档**: http://101.43.57.35/swagger-ui/index.html
- **健康检查**: http://101.43.57.35/health