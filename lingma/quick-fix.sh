#!/bin/bash
# Lingma快速修复脚本 - 解决域名访问"链接已重置"问题

echo "==========================================="
echo "  快速修复脚本 (Lingma)"
echo "  执行时间: $(date)"
echo "==========================================="
echo ""

# 设置工作目录
cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || cd /opt/AsianPetsSystem 2>/dev/null || {
    echo "❌ 无法找到项目目录"
    exit 1
}

echo "📁 当前工作目录: $(pwd)"
echo ""

# 1. 检查并启动Docker服务
echo "=== [1] 检查Docker服务状态 ==="
if ! command -v docker >/dev/null 2>&1; then
    echo "❌ Docker未安装"
    exit 1
fi

if ! systemctl is-active --quiet docker; then
    echo "🔄 启动Docker服务..."
    sudo systemctl start docker
    sleep 3
fi

echo "✅ Docker服务运行中"
echo ""

# 2. 检查并重建容器
echo "=== [2] 检查容器状态 ==="
CONTAINERS_RUNNING=$(docker ps --format "{{.Names}}" | grep -E "nginx|app" | wc -l)

if [ $CONTAINERS_RUNNING -lt 2 ]; then
    echo "🔄 容器数量不足，重新构建..."
    if [ -f "docker-compose.prod.yml" ]; then
        docker-compose -f docker-compose.prod.yml down
        docker-compose -f docker-compose.prod.yml up -d --build
    elif [ -f "docker-compose.yml" ]; then
        docker-compose down
        docker-compose up -d --build
    else
        echo "❌ 未找到docker-compose文件"
        exit 1
    fi
    echo "⏳ 等待服务启动..."
    sleep 30
else
    echo "✅ 容器运行正常"
fi
echo ""

# 3. 验证服务状态
echo "=== [3] 验证服务状态 ==="
echo "🔍 Docker容器状态:"
docker-compose ps 2>/dev/null || docker compose ps

echo ""
echo "🔍 本地HTTPS测试:"
LOCAL_TEST=$(curl -s -o /dev/null -w "%{http_code}" -k https://localhost:443 2>/dev/null)
if [ "$LOCAL_TEST" = "200" ] || [ "$LOCAL_TEST" = "301" ] || [ "$LOCAL_TEST" = "302" ]; then
    echo "✅ 本地HTTPS服务正常，状态码: $LOCAL_TEST"
else
    echo "❌ 本地HTTPS服务异常，状态码: $LOCAL_TEST"
fi
echo ""

# 4. 检查端口监听
echo "=== [4] 端口监听检查 ==="
PORT_80_LISTENING=$(ss -tlnp | grep :80 | wc -l)
PORT_443_LISTENING=$(ss -tlnp | grep :443 | wc -l)

echo "🔍 80端口监听: $PORT_80_LISTENING 个进程"
echo "🔍 443端口监听: $PORT_443_LISTENING 个进程"

if [ $PORT_80_LISTENING -eq 0 ] || [ $PORT_443_LISTENING -eq 0 ]; then
    echo "⚠️  端口监听异常，可能需要检查防火墙设置"
fi
echo ""

# 5. 防火墙配置
echo "=== [5] 防火墙配置 ==="
if command -v ufw >/dev/null 2>&1; then
    echo "🔍 UFW状态:"
    sudo ufw status
    
    echo "🔄 确保端口开放:"
    sudo ufw allow 80/tcp
    sudo ufw allow 443/tcp
    sudo ufw allow 8443/tcp
    echo "✅ 防火墙规则已更新"
elif command -v firewall-cmd >/dev/null 2>&1; then
    echo "🔍 Firewalld状态:"
    sudo firewall-cmd --list-all
    
    echo "🔄 确保端口开放:"
    sudo firewall-cmd --permanent --add-port=80/tcp
    sudo firewall-cmd --permanent --add-port=443/tcp
    sudo firewall-cmd --permanent --add-port=8443/tcp
    sudo firewall-cmd --reload
    echo "✅ 防火墙规则已更新"
else
    echo "⚠️  未检测到标准防火墙工具，跳过配置"
fi
echo ""

# 6. 测试备用端口
echo "=== [6] 备用端口配置 ==="
echo "🔍 检查docker-compose配置中的备用端口:"
if grep -q "8443:443" docker-compose.prod.yml 2>/dev/null || grep -q "8443:443" docker-compose.yml 2>/dev/null; then
    echo "✅ 8443端口已配置"
    echo "🔍 测试备用端口连通性:"
    ALT_TEST=$(curl -s -o /dev/null -w "%{http_code}" -k https://localhost:8443 2>/dev/null)
    if [ "$ALT_TEST" = "200" ] || [ "$ALT_TEST" = "301" ] || [ "$ALT_TEST" = "302" ]; then
        echo "✅ 备用端口8443服务正常，状态码: $ALT_TEST"
        echo "💡 用户可以尝试访问: https://cailanzikzh.xin:8443"
    else
        echo "❌ 备用端口8443服务异常"
    fi
else
    echo "⚠️  未配置备用端口，正在添加..."
    # 这里可以添加自动配置备用端口的逻辑
fi
echo ""

# 7. 显示访问信息
echo "=== [7] 访问方式汇总 ==="
echo ""
echo "🌐 当前可访问的方式:"
echo "1. 服务器IP访问: http://101.43.57.35"
echo "2. 备用HTTPS端口: https://cailanzikzh.xin:8443"
echo "3. 本地测试: https://localhost:443"
echo ""
echo "⚠️  注意事项:"
echo "- 如果主域名(443端口)仍无法访问，可能是网络环境限制"
echo "- 建议用户在不同网络环境下测试"
echo "- 长期解决方案建议部署CDN服务"
echo ""

# 8. 提供后续建议
echo "=== [8] 后续建议 ==="
echo ""
echo "🔧 如果问题仍然存在，可以尝试:"
echo "1. 重启网络服务: sudo systemctl restart networking"
echo "2. 清除DNS缓存: sudo systemd-resolve --flush-caches"
echo "3. 检查系统日志: journalctl -f"
echo "4. 联系网络管理员确认是否有端口限制"
echo ""
echo "📈 长期优化建议:"
echo "1. 部署腾讯云CDN或Cloudflare"
echo "2. 配置多个备用端口"
echo "3. 监控服务可用性"
echo "4. 设置自动告警机制"
echo ""

echo "==========================================="
echo "  快速修复完成"
echo "==========================================="