#!/bin/bash

echo "🔧 开始修复部署问题..."

# 1. 备份原始配置
cp .env .env.backup 2>/dev/null || echo "⚠️  .env备份文件已存在"
cp nginx/conf/nginx.conf nginx/conf/nginx.conf.backup 2>/dev/null || echo "⚠️  nginx.conf备份文件已存在"

# 2. 修复数据库连接配置（如果还未修复）
if grep -q "host.docker.internal" .env; then
    sed -i 's/host.docker.internal/mysql/g' .env
    echo "✅ 已修复.env中的数据库连接配置"
else
    echo "✅ .env配置已经是正确的"
fi

# 3. 检查并修复Nginx配置
NGINX_CONF="nginx/conf/nginx.conf"
if grep -q "app:8081" "$NGINX_CONF"; then
    sed -i 's/app:8081/asianpetsactivity-app-1:8081/g' "$NGINX_CONF"
    echo "✅ 已修复Nginx upstream配置"
else
    echo "✅ Nginx配置已经是正确的"
fi

# 4. 重启服务
echo "🔄 重启Docker服务..."
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d

# 5. 等待服务启动
echo "⏳ 等待服务启动(60秒)..."
sleep 60

# 6. 验证服务状态
echo "📋 服务状态检查:"
docker-compose -f docker-compose.prod.yml ps

echo "🧪 健康检查测试:"
curl -I http://localhost:8081/actuator/health || echo "❌ 健康检查失败"
curl -I http://localhost:8081/swagger-ui/index.html || echo "❌ Swagger UI访问失败"

echo "📝 应用日志最后20行:"
docker-compose -f docker-compose.prod.yml logs app --tail=20

echo "✅ 修复脚本执行完成!"

# 7. 提供公网访问测试命令
echo ""
echo "🌐 公网访问测试命令:"
echo "curl -I http://101.43.57.35:8081/actuator/health"
echo "curl -I http://101.43.57.35/swagger-ui/index.html"