#!/bin/bash

# 亚洲宠物协会系统部署脚本

set -e

echo "🚀 开始部署亚洲宠物协会系统..."

# 检查必要文件
echo "🔍 检查必要文件..."
if [ ! -f "Dockerfile" ]; then
    echo "❌ Dockerfile 不存在"
    exit 1
fi

if [ ! -f "docker-compose.prod.yml" ]; then
    echo "❌ docker-compose.prod.yml 不存在"
    exit 1
fi

if [ ! -f ".env" ]; then
    echo "❌ .env 文件不存在"
    exit 1
fi

# 构建镜像
echo "🔨 构建Docker镜像..."
docker-compose -f docker-compose.prod.yml build

# 停止现有容器
echo "⏹️ 停止现有容器..."
docker-compose -f docker-compose.prod.yml down

# 启动服务
echo "▶️ 启动服务..."
docker-compose -f docker-compose.prod.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "📋 检查服务状态..."
docker-compose -f docker-compose.prod.yml ps

# 检查应用健康状态
echo "🏥 检查应用健康状态..."
for i in {1..10}; do
    if curl -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
        echo "✅ 应用服务健康检查通过"
        break
    fi
    echo "⏳ 等待应用启动... ($i/10)"
    sleep 10
done

# 检查Nginx
echo "🌐 检查Nginx服务..."
if curl -f http://localhost/health > /dev/null 2>&1; then
    echo "✅ Nginx服务运行正常"
else
    echo "⚠️ Nginx服务可能存在问题"
fi

echo "🎉 部署完成！"
echo "应用查看地址: http://你的服务器IP/swagger-ui/index.html"
echo "健康检查: http://你的服务器IP/health"