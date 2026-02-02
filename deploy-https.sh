#!/bin/bash

# HTTPS安全部署脚本

set -e

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "❌ 请使用root权限运行此脚本"
    echo "使用方法: sudo ./deploy-https.sh your-domain.com"
    exit 1
fi

echo "🔐 开始HTTPS安全部署..."

# 检查域名参数
if [ -z "$1" ]; then
    echo "❌ 请提供域名参数"
    echo "使用方法: ./deploy-https.sh your-domain.com"
    echo "示例: ./deploy-https.sh cailanzikzh.xin"
    exit 1
fi

# 清理域名参数，移除http://或https://前缀
DOMAIN=$(echo "$1" | sed 's|^https\?://||' | sed 's|/$||')
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
cp -r ./src ./nginx ./Dockerfile ./docker-compose.prod.yml ./.env /opt/AsianPetsActivity/

# 确保配置文件存在
if [ ! -f "/opt/AsianPetsActivity/docker-compose.prod.yml" ]; then
    echo "❌ 项目文件复制失败"
    exit 1
fi

# 修改Nginx配置中的域名
echo "🔧 配置Nginx..."
sed -i "s/your-domain.com/$DOMAIN/g" /opt/AsianPetsActivity/nginx/conf/nginx-https.conf
sed -i "s/www.your-domain.com/www.$DOMAIN/g" /opt/AsianPetsActivity/nginx/conf/nginx-https.conf

# 获取SSL证书
echo "📜 获取SSL证书..."
if sudo certbot --nginx -d $DOMAIN -d www.$DOMAIN --non-interactive --agree-tos --email admin@$DOMAIN; then
    echo "✅ SSL证书获取成功"
else
    echo "❌ SSL证书获取失败，尝试使用备用方案..."
    # 如果Let's Encrypt失败，继续使用HTTP部署
    echo "⚠️  将使用HTTP部署替代HTTPS部署"
    USE_HTTP=true
fi

# 替换Nginx配置文件
if [ "$USE_HTTP" != "true" ]; then
    echo "📋 应用HTTPS配置..."
    sudo cp /opt/AsianPetsActivity/nginx/conf/nginx-https.conf /etc/nginx/nginx.conf
else
    echo "📋 应用HTTP配置..."
    sudo cp /opt/AsianPetsActivity/nginx/conf/nginx.conf /etc/nginx/nginx.conf
fi

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
if [ "$USE_HTTP" != "true" ]; then
    PROTOCOL="https"
    HEALTH_URL="https://$DOMAIN/health"
    APP_URL="https://$DOMAIN/swagger-ui/index.html"
else
    PROTOCOL="http"
    HEALTH_URL="http://$DOMAIN/health"
    APP_URL="http://$DOMAIN/swagger-ui/index.html"
    echo "⚠️  注意：由于SSL证书问题，正在使用HTTP部署"
fi

if curl -f "$HEALTH_URL" > /dev/null 2>&1; then
    echo "🎉 ${PROTOCOL^^}部署成功！"
    echo "应用查看地址: $APP_URL"
    echo "健康检查: $HEALTH_URL"
else
    echo "⚠️ 部署可能存在问题，请检查日志"
    cd /opt/AsianPetsActivity
    docker-compose -f docker-compose.prod.yml logs app --tail=20
fi

# 设置自动续期
echo "⏰ 设置SSL证书自动续期..."
sudo crontab -l | { cat; echo "0 12 * * * /usr/bin/certbot renew --quiet"; } | sudo crontab -

echo "🔐 HTTPS部署完成！"