#!/bin/bash

# 阿里云SSL证书部署脚本

set -e

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "❌ 请使用root权限运行此脚本"
    echo "使用方法: sudo ./deploy-alicloud-ssl.sh"
    exit 1
fi

echo "🔐 开始阿里云SSL证书部署..."

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

# 检查SSL证书文件
echo "🔍 检查SSL证书文件..."
if ! ls /opt/ssl/*.pem 1> /dev/null 2>&1 || ! ls /opt/ssl/*.key 1> /dev/null 2>&1; then
    echo "❌ 请先将阿里云SSL证书文件上传到 /opt/ssl/ 目录"
    echo "需要的文件："
    echo "  - 证书文件 (*.pem)"
    echo "  - 私钥文件 (*.key)"
    echo ""
    echo "当前 /opt/ssl/ 目录内容："
    ls -la /opt/ssl/
    exit 1
fi

# 获取证书文件名
CERT_PEM=$(ls /opt/ssl/*.pem | head -n 1)
CERT_KEY=$(ls /opt/ssl/*.key | head -n 1)
CERT_NAME=$(basename "$CERT_PEM" .pem)

echo "📋 检测到证书文件："
echo "  证书文件: $CERT_PEM"
echo "  私钥文件: $CERT_KEY"
echo "  证书名称: $CERT_NAME"

# 创建项目目录
echo "📁 创建项目目录..."
mkdir -p /opt/AsianPetsActivity
mkdir -p /opt/ssl

# 复制项目文件
echo "📋 复制项目文件..."
cp -r ./src ./nginx ./Dockerfile ./docker-compose.prod.yml ./.env /opt/AsianPetsActivity/

# 使用阿里云证书配置
echo "🔧 配置Nginx使用阿里云证书..."
cp /opt/AsianPetsActivity/nginx/conf/nginx-alicloud-https.conf /etc/nginx/nginx.conf

# 替换证书路径
sed -i "s|/opt/ssl/证书名称.pem|$CERT_PEM|g" /etc/nginx/nginx.conf
sed -i "s|/opt/ssl/证书名称.key|$CERT_KEY|g" /etc/nginx/nginx.conf

# 测试Nginx配置
echo "🧪 测试Nginx配置..."
if nginx -t; then
    echo "✅ Nginx配置测试通过"
else
    echo "❌ Nginx配置有错误"
    exit 1
fi

# 重启Nginx
echo "🔄 重启Nginx..."
systemctl restart nginx

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

# 测试HTTPS连接
echo "🔐 HTTPS连接测试:"
DOMAIN="cailanzikzh.xin"
if curl -fk https://$DOMAIN/health > /dev/null 2>&1; then
    echo "✅ HTTPS健康检查通过"
else
    echo "⚠️  HTTPS健康检查可能需要更多时间或证书配置检查"
fi

# 测试HTTP重定向
echo "🔄 HTTP重定向测试:"
if curl -f http://$DOMAIN/health 2>&1 | grep -q "301"; then
    echo "✅ HTTP到HTTPS重定向配置正确"
else
    echo "⚠️  HTTP重定向可能需要检查"
fi

# 显示日志
echo ""
echo "📝 最近日志:"
docker-compose -f docker-compose.prod.yml logs app --tail=10

echo ""
echo "🎉 阿里云SSL证书部署完成！"
echo "访问地址:"
echo "Swagger API文档: https://cailanzikzh.xin/swagger-ui/index.html"
echo "健康检查: https://cailanzikzh.xin/health"
echo ""
echo "💡 提示："
echo "  - 首次访问可能会有证书警告，请稍等几分钟让证书生效"
echo "  - 如需查看更多日志: docker-compose -f docker-compose.prod.yml logs -f app"
echo "  - 证书有效期一年，到期前需要更新"