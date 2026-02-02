#!/bin/bash

# HTTP部署脚本 - 简化版

set -e

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "❌ 请使用root权限运行此脚本"
    echo "使用方法: sudo ./deploy-http.sh"
    exit 1
fi

echo "🚀 开始HTTP部署..."

# 检查必要文件
echo "🔍 检查必要文件..."
if [ ! -f "docker-compose.prod.yml" ]; then
    echo "❌ docker-compose.prod.yml 不存在"
    exit 1
fi

if [ ! -f ".env" ]; then
    echo "❌ .env 文件不存在"
    exit 1
fi

if [ ! -f "nginx/conf/nginx.conf" ]; then
    echo "❌ Nginx配置文件不存在"
    exit 1
fi

# 创建项目目录（如果不存在）
echo "📁 准备项目目录..."
mkdir -p /opt/AsianPetsActivity

# 复制项目文件到部署目录
echo "📋 复制项目文件..."
cp -r ./src ./nginx ./Dockerfile ./docker-compose.prod.yml ./.env /opt/AsianPetsActivity/

# 复制Nginx配置
echo "🔧 配置Nginx..."
sudo cp /opt/AsianPetsActivity/nginx/conf/nginx.conf /etc/nginx/nginx.conf

# 测试Nginx配置
echo "🧪 测试Nginx配置..."
if sudo nginx -t; then
    echo "✅ Nginx配置测试通过"
else
    echo "❌ Nginx配置有错误"
    exit 1
fi

# 重启Nginx
echo "🔄 重启Nginx..."
sudo systemctl restart nginx

# 进入部署目录
cd /opt/AsianPetsActivity

# 停止现有服务
echo "⏹️ 停止现有服务..."
docker-compose -f docker-compose.prod.yml down

# 构建并启动Docker服务
echo "🐳 启动应用服务..."
docker-compose -f docker-compose.prod.yml up -d --build

# 等待服务启动
echo "⏳ 等待服务启动(90秒)..."
sleep 90

# 验证部署
echo "✅ 验证部署..."

# 检查容器状态
echo "📋 容器状态:"
docker-compose -f docker-compose.prod.yml ps

# 测试健康检查
echo "🏥 健康检查测试:"
if curl -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "✅ 应用健康检查通过"
else
    echo "❌ 应用健康检查失败"
fi

if curl -f http://localhost/health > /dev/null 2>&1; then
    echo "✅ Nginx健康检查通过"
else
    echo "❌ Nginx健康检查失败"
fi

# 显示日志
echo ""
echo "📝 最近日志:"
docker-compose -f docker-compose.prod.yml logs app --tail=10

echo ""
echo "🎉 HTTP部署完成！"
echo "访问地址:"
echo "Swagger API文档: http://cailanzikzh.xin/swagger-ui/index.html"
echo "健康检查: http://cailanzikzh.xin/health"
echo ""
echo "如需查看更多日志: docker-compose -f docker-compose.prod.yml logs -f app"