#!/bin/bash

echo "🔧 修复Swagger UI静态资源404问题..."

# 1. 备份当前Nginx配置
cp nginx/conf/nginx.conf nginx/conf/nginx.conf.backup.static 2>/dev/null || echo "⚠️  备份文件已存在"

# 2. 重新构建并重启服务
echo "🔄 重新构建并重启服务..."
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d --build

# 3. 等待服务启动
echo "⏳ 等待服务启动(60秒)..."
sleep 60

# 4. 验证修复结果
echo "🧪 验证修复结果..."

echo "检查Swagger UI页面:"
curl -f http://localhost:8081/swagger-ui/index.html >/dev/null 2>&1 && echo "✅ Swagger UI页面访问正常" || echo "❌ Swagger UI页面访问失败"

echo "检查Nginx代理:"
curl -f http://localhost/health >/dev/null 2>&1 && echo "✅ Nginx健康检查正常" || echo "❌ Nginx健康检查失败"

# 5. 显示服务状态
echo ""
echo "📋 当前服务状态:"
docker-compose -f docker-compose.prod.yml ps

echo ""
echo "📝 最近日志:"
docker-compose -f docker-compose.prod.yml logs app --tail=10

echo ""
echo "✅ Swagger静态资源修复完成!"
echo "请通过 http://101.43.57.35/swagger-ui/index.html 访问API文档"