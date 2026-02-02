#!/bin/bash

# 本地Nginx部署脚本

set -e

echo "🚀 开始本地Nginx部署..."

# 检查必要文件
if [ ! -f "docker-compose-local-nginx.yml" ]; then
    echo "❌ docker-compose-local-nginx.yml 不存在"
    exit 1
fi

if [ ! -f ".env" ]; then
    echo "❌ .env 文件不存在"
    exit 1
fi

if [ ! -f "nginx/conf/nginx-local.conf" ]; then
    echo "❌ nginx-local.conf 文件不存在"
    exit 1
fi

# 安装必要软件
echo "📦 安装必要软件..."
sudo apt update
sudo apt install -y nginx docker.io docker-compose

# 创建项目目录
echo "📁 创建项目目录..."
sudo mkdir -p /opt/AsianPetsActivity

# 复制项目文件
echo "📋 复制项目文件..."
cp -r ./src ./Dockerfile ./docker-compose-local-nginx.yml ./.env /opt/AsianPetsActivity/

# 配置本地Nginx
echo "🔧 配置本地Nginx..."
sudo cp /opt/AsianPetsActivity/nginx/conf/nginx-local.conf /etc/nginx/sites-available/asianpets
sudo ln -sf /etc/nginx/sites-available/asianpets /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# 测试Nginx配置
echo "🧪 测试Nginx配置..."
sudo nginx -t

# 重启Nginx
echo "🔄 重启Nginx..."
sudo systemctl restart nginx

# 进入部署目录
cd /opt/AsianPetsActivity

# 停止现有服务
echo "⏹️ 停止现有服务..."
sudo docker-compose -f docker-compose-local-nginx.yml down

# 构建并启动Docker服务
echo "🐳 启动应用服务..."
sudo docker-compose -f docker-compose-local-nginx.yml up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 60

# 验证部署
echo "✅ 验证部署..."

# 检查容器状态
echo "📋 容器状态:"
sudo docker-compose -f docker-compose-local-nginx.yml ps

# 测试健康检查
echo "🏥 健康检查测试:"
curl -f http://localhost:8081/actuator/health && echo "✅ 应用健康检查通过" || echo "❌ 应用健康检查失败"
curl -f http://localhost/health && echo "✅ Nginx健康检查通过" || echo "❌ Nginx健康检查失败"

echo ""
echo "🎉 本地Nginx部署完成！"
echo "访问地址:"
echo "Swagger API文档: http://cailanzikzh.xin/swagger-ui/index.html"
echo "健康检查: http://cailanzikzh.xin/health"