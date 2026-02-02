#!/bin/bash

# HTTPS安全部署脚本

set -e

echo "🔐 开始HTTPS安全部署..."

# 检查域名参数
if [ -z "$1" ]; then
    echo "❌ 请提供域名参数"
    echo "使用方法: ./deploy-https.sh your-domain.com"
    exit 1
fi

DOMAIN=$1
echo "🎯 目标域名: $DOMAIN"

# 安装必要的软件
echo "📦 安装必要软件..."
sudo apt update
sudo apt install -y nginx certbot python3-certbot-nginx

# 创建项目目录
echo "📁 创建项目目录..."
sudo mkdir -p /opt/AsianPetsActivity
sudo chown -R $USER:$USER /opt/AsianPetsActivity

# 复制项目文件
echo "📋 复制项目文件..."
cp -r ../src ../nginx ../Dockerfile ../docker-compose.prod.yml ../.env /opt/AsianPetsActivity/

# 修改Nginx配置中的域名
echo "🔧 配置Nginx..."
sed -i "s/your-domain.com/$DOMAIN/g" /opt/AsianPetsActivity/nginx/conf/nginx-https.conf

# 获取SSL证书
echo "📜 获取SSL证书..."
sudo certbot --nginx -d $DOMAIN -d www.$DOMAIN --non-interactive --agree-tos --email admin@$DOMAIN

# 替换Nginx配置文件
sudo cp /opt/AsianPetsActivity/nginx/conf/nginx-https.conf /etc/nginx/nginx.conf

# 测试Nginx配置
echo "🧪 测试Nginx配置..."
sudo nginx -t

# 重启Nginx
echo "🔄 重启Nginx..."
sudo systemctl restart nginx

# 构建并启动Docker服务
echo "🐳 启动应用服务..."
cd /opt/AsianPetsActivity
docker-compose -f docker-compose.prod.yml up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 60

# 验证部署
echo "✅ 验证部署..."
if curl -f https://$DOMAIN/health > /dev/null 2>&1; then
    echo "🎉 HTTPS部署成功！"
    echo "应用查看地址: https://cailanzikzh.xin/swagger-ui/index.html"
    echo "健康检查: https://cailanzikzh.xin/health"
else
    echo "⚠️ 部署可能存在问题，请检查日志"
    docker-compose logs app
fi

# 设置自动续期
echo "⏰ 设置SSL证书自动续期..."
sudo crontab -l | { cat; echo "0 12 * * * /usr/bin/certbot renew --quiet"; } | sudo crontab -

echo "🔐 HTTPS部署完成！"