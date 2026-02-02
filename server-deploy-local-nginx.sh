#!/bin/bash

# 服务器本地Nginx部署脚本
# 用于腾讯云服务器部署

set -e

echo "🚀 开始服务器本地Nginx部署..."

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "❌ 请使用root权限运行此脚本"
    echo "使用方法: sudo ./server-deploy-local-nginx.sh"
    exit 1
fi

# 检查必要文件
echo "🔍 检查必要文件..."
REQUIRED_FILES=(
    "docker-compose-local-nginx.yml"
    ".env"
    "nginx/conf/nginx-local.conf"
    "Dockerfile"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        echo "❌ 缺少必要文件: $file"
        exit 1
    fi
done

# 安装必要软件
echo "📦 安装必要软件..."
apt update
apt install -y nginx docker.io docker-compose curl

# 创建项目目录
echo "📁 创建项目目录..."
mkdir -p /opt/AsianPetsActivity
mkdir -p /var/log/asianpets

# 复制项目文件到部署目录
echo "📋 复制项目文件..."
cp -r ./src ./Dockerfile ./docker-compose-local-nginx.yml ./.env /opt/AsianPetsActivity/
cp -r ./nginx /opt/AsianPetsActivity/

# 配置本地Nginx
echo "🔧 配置服务器本地Nginx..."
cp /opt/AsianPetsActivity/nginx/conf/nginx-local.conf /etc/nginx/sites-available/asianpets
ln -sf /etc/nginx/sites-available/asianpets /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default

# 测试Nginx配置
echo "🧪 测试Nginx配置..."
if nginx -t; then
    echo "✅ Nginx配置测试通过"
else
    echo "❌ Nginx配置有错误"
    exit 1
fi

# 启动Nginx服务
echo "🔄 启动Nginx服务..."
systemctl enable nginx
systemctl restart nginx

# 进入部署目录
cd /opt/AsianPetsActivity

# 停止现有服务
echo "⏹️ 停止现有Docker服务..."
docker-compose -f docker-compose-local-nginx.yml down 2>/dev/null || echo "没有运行中的服务"

# 构建并启动Docker服务
echo "🐳 构建并启动应用服务..."
docker-compose -f docker-compose-local-nginx.yml up -d --build

# 等待服务启动
echo "⏳ 等待服务启动(60秒)..."
sleep 60

# 验证部署
echo "✅ 验证部署结果..."

# 检查容器状态
echo "📋 Docker容器状态:"
docker-compose -f docker-compose-local-nginx.yml ps

# 测试本地服务连通性
echo "🏥 本地服务测试:"
if curl -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "✅ 应用健康检查通过"
else
    echo "❌ 应用健康检查失败"
    echo "应用查看日志:"
    docker-compose -f docker-compose-local-nginx.yml logs app --tail=20
fi

if curl -f http://localhost/health > /dev/null 2>&1; then
    echo "✅ Nginx健康检查通过"
else
    echo "❌ Nginx健康检查失败"
    echo "Nginx错误日志:"
    tail -20 /var/log/nginx/error.log
fi

# 显示服务信息
echo ""
echo "📋 当前服务状态:"
echo "Nginx状态: $(systemctl is-active nginx)"
echo "Docker容器:"
docker-compose -f docker-compose-local-nginx.yml ps

echo ""
echo "🎉 服务器本地Nginx部署完成！"
echo "========================="
echo "访问地址:"
echo "Swagger API文档: http://cailanzikzh.xin/swagger-ui/index.html"
echo "健康检查: http://cailanzikzh.xin/health"
echo "本地测试: http://localhost/swagger-ui/index.html"
echo ""
echo "管理命令:"
echo "查看应用日志: docker-compose -f docker-compose-local-nginx.yml logs -f app"
echo "重启服务: docker-compose -f docker-compose-local-nginx.yml restart"
echo "停止服务: docker-compose -f docker-compose-local-nginx.yml down"
echo "Nginx状态: systemctl status nginx"
echo "========================="