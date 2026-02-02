#!/bin/bash

# 一键部署脚本 - 适用于cailanzikzh.xin域名
# 服务器IP: 101.43.57.35

# 一键部署脚本 - 适用于cailanzikzh.xin域名
# 服务器IP: 101.43.57.35

set -e

echo "🚀 开始一键部署亚洲宠物协会系统..."
echo "域名: cailanzikzh.xin"
echo "服务器IP: 101.43.57.35"

# 检查是否在正确目录
if [ ! -f "docker-compose.prod.yml" ]; then
    echo "❌ 请在项目根目录执行此脚本"
    exit 1
fi

# 检查必要文件
echo "🔍 检查必要文件..."
REQUIRED_FILES=("Dockerfile" "docker-compose.prod.yml" ".env" "deploy.sh" "jwt-config-tool.sh")
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        echo "❌ 缺少必要文件: $file"
        exit 1
    fi
done

# 生成JWT密钥
echo "🔐 生成JWT密钥..."
./jwt-config-tool.sh << EOF
4
EOF

# 构建和部署
echo "🔨 开始构建和部署..."
./deploy.sh

# 验证部署
echo "🧪 验证部署结果..."

echo "本地健康检查:"
curl -f http://localhost:8081/actuator/health && echo " ✅ 应用健康检查通过" || echo " ❌ 应用健康检查失败"
curl -f http://localhost/health && echo " ✅ Nginx健康检查通过" || echo " ❌ Nginx健康检查失败"

echo ""
echo "公网访问测试:"
curl -I http://101.43.57.35:8081/actuator/health && echo " ✅ 公网应用访问正常" || echo " ❌ 公网应用访问失败"
curl -I http://101.43.57.35/swagger-ui/index.html && echo " ✅ Swagger UI访问正常" || echo " ❌ Swagger UI访问失败"

# 显示部署信息
echo ""
echo "🎉 部署完成！"
echo "========================="
echo "应用查看地址: http://101.43.57.35/swagger-ui/index.html"
echo "健康检查地址: http://101.43.57.35/health"
echo "API文档地址: http://101.43.57.35/swagger-ui/index.html"
echo ""
echo "如需配置HTTPS，请运行: ./deploy-https.sh cailanzikzh.xin"
echo "========================="

# 显示服务状态
echo "📋 当前服务状态:"
docker-compose -f docker-compose.prod.yml ps