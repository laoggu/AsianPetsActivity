#!/bin/bash

echo "🔧 快速修复JWT认证问题..."

# 备份当前配置
echo "📋 备份当前配置..."
cp src/main/java/org/example/asianpetssystem/security/JwtAuthenticationFilter.java src/main/java/org/example/asianpetssystem/security/JwtAuthenticationFilter.java.backup

# 重新构建镜像
echo "🔨 重新构建Docker镜像..."
docker-compose -f docker-compose.prod.yml build --no-cache

# 停止现有服务
echo "⏹️ 停止现有服务..."
docker-compose -f docker-compose.prod.yml down

# 启动新服务
echo "▶️ 启动修复后的服务..."
docker-compose -f docker-compose.prod.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动(60秒)..."
sleep 60

# 验证修复效果
echo "🧪 验证修复效果..."

echo "检查Swagger UI访问:"
if curl -f http://localhost:8081/swagger-ui/index.html > /dev/null 2>&1; then
    echo "✅ Swagger UI访问正常"
else
    echo "❌ Swagger UI访问仍有问题"
fi

echo "检查健康检查端点:"
if curl -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "✅ 健康检查端点正常"
else
    echo "❌ 健康检查仍有问题"
fi

echo "检查通用接口:"
if curl -f http://localhost:8081/api/common/system-config > /dev/null 2>&1; then
    echo "✅ 通用接口访问正常"
else
    echo "❌ 通用接口访问仍有问题"
fi

# 显示服务状态
echo "📋 当前服务状态:"
docker-compose -f docker-compose.prod.yml ps

# 显示最近日志
echo "📝 最近的应用日志:"
docker-compose -f docker-compose.prod.yml logs app --tail=20

echo "✅ JWT认证修复完成！"
echo "如需查看详细日志，请运行: docker-compose -f docker-compose.prod.yml logs -f app"