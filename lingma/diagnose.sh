#!/bin/bash
# Lingma诊断脚本 - 检查域名访问"链接已重置"问题

echo "==========================================="
echo "  域名访问问题诊断脚本 (Lingma)"
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

# 1. 检查Docker服务状态
echo "=== [1] Docker容器状态检查 ==="
if command -v docker-compose >/dev/null 2>&1; then
    echo "🐳 Docker Compose 状态:"
    docker-compose ps
elif command -v docker >/dev/null 2>&1; then
    echo "🐳 Docker Compose (新版本) 状态:"
    docker compose ps
else
    echo "❌ Docker未安装或不可用"
fi
echo ""

# 2. 检查Nginx配置
echo "=== [2] Nginx配置检查 ==="
if [ -f "nginx/conf/nginx.conf" ]; then
    echo "📄 Nginx配置文件存在"
    echo "🔍 检查server_name配置:"
    grep -n "server_name" nginx/conf/nginx.conf
    echo ""
    echo "🔍 检查SSL证书配置:"
    grep -n "ssl_certificate" nginx/conf/nginx.conf
    echo ""
    echo "🔍 检查监听端口:"
    grep -n "listen" nginx/conf/nginx.conf | grep -E "80|443"
else
    echo "❌ Nginx配置文件不存在"
fi
echo ""

# 3. 检查SSL证书
echo "=== [3] SSL证书状态检查 ==="
if [ -f "nginx/ssl/cailanzikzh.xin.pem" ] && [ -f "nginx/ssl/cailanzikzh.xin.key" ]; then
    echo "✅ SSL证书文件存在"
    echo "📅 证书有效期:"
    openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -dates 2>/dev/null || echo "无法读取证书信息"
    echo ""
    echo "🔍 证书主题:"
    openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -subject 2>/dev/null || echo "无法读取证书主题"
else
    echo "❌ SSL证书文件缺失"
fi
echo ""

# 4. 服务器本地测试
echo "=== [4] 服务器本地HTTPS测试 ==="
LOCAL_RESULT=$(curl -s -o /dev/null -w "%{http_code}" -k https://localhost:443 2>/dev/null)
if [ $? -eq 0 ]; then
    echo "✅ 本地HTTPS测试成功，状态码: $LOCAL_RESULT"
else
    echo "❌ 本地HTTPS测试失败"
fi
echo ""

# 5. 本机IP测试
echo "=== [5] 本机IP HTTPS测试 ==="
SERVER_IP=$(hostname -I | awk '{print $1}')
if [ ! -z "$SERVER_IP" ]; then
    IP_RESULT=$(curl -s -o /dev/null -w "%{http_code}" -k https://$SERVER_IP:443 2>/dev/null)
    if [ $? -eq 0 ]; then
        echo "✅ 本机IP HTTPS测试成功 ($SERVER_IP:443)，状态码: $IP_RESULT"
    else
        echo "❌ 本机IP HTTPS测试失败 ($SERVER_IP:443)"
    fi
else
    echo "❌ 无法获取服务器IP"
fi
echo ""

# 6. 端口监听检查
echo "=== [6] 端口监听状态 ==="
echo "🔍 443端口监听情况:"
ss -tlnp | grep :443 || netstat -tlnp | grep :443 || echo "443端口未监听"
echo ""
echo "🔍 80端口监听情况:"
ss -tlnp | grep :80 || netstat -tlnp | grep :80 || echo "80端口未监听"
echo ""

# 7. Docker端口映射检查
echo "=== [7] Docker端口映射检查 ==="
if command -v docker >/dev/null 2>&1; then
    echo "🔍 Nginx容器端口映射:"
    docker ps --format "table {{.Names}}\t{{.Ports}}" | grep nginx || echo "未找到nginx容器"
    echo ""
    echo "🔍 所有容器端口映射:"
    docker ps --format "table {{.Names}}\t{{.Ports}}"
else
    echo "❌ Docker命令不可用"
fi
echo ""

# 8. 防火墙检查
echo "=== [8] 防火墙状态检查 ==="
echo "🔍 UFW状态:"
ufw status 2>/dev/null || echo "UFW未安装或未启用"
echo ""
echo "🔍 iptables INPUT链:"
iptables -L INPUT -n -v --line-numbers 2>/dev/null | head -10 || echo "无法读取iptables规则"
echo ""
echo "🔍 443端口相关规则:"
iptables -L -n -v 2>/dev/null | grep -E "443|dpt:https" || echo "无443端口相关规则"
echo ""

# 9. 网络连接测试
echo "=== [9] 网络连通性测试 ==="
echo "🔍 DNS解析测试:"
nslookup cailanzikzh.xin 2>/dev/null || dig cailanzikzh.xin 2>/dev/null || echo "DNS查询失败"
echo ""
echo "🔍 外部连通性测试:"
ping -c 3 8.8.8.8 2>/dev/null | head -5 || echo "无法ping外部网络"
echo ""

# 10. Nginx日志检查（最近10行）
echo "=== [10] Nginx错误日志检查 ==="
if [ -f "nginx/logs/error.log" ]; then
    echo "🔍 最近的Nginx错误日志:"
    tail -10 nginx/logs/error.log 2>/dev/null || echo "无法读取错误日志"
else
    echo "❌ Nginx错误日志文件不存在"
fi
echo ""

# 11. 应用健康检查
echo "=== [11] 应用健康状态检查 ==="
APP_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/actuator/health 2>/dev/null)
if [ $? -eq 0 ] && [ "$APP_HEALTH" = "200" ]; then
    echo "✅ 后端应用健康检查通过，状态码: $APP_HEALTH"
else
    echo "❌ 后端应用健康检查失败，状态码: $APP_HEALTH"
fi
echo ""

echo "==========================================="
echo "  诊断完成"
echo "==========================================="
echo ""
echo "📋 关键检查点总结:"
echo "1. 如果Docker容器未运行，请执行: docker-compose up -d"
echo "2. 如果SSL证书过期，请重新申请并替换"
echo "3. 如果443端口未监听，请检查Nginx配置和容器状态"
echo "4. 如果防火墙阻止443端口，请添加相应规则"
echo ""
echo "💡 建议操作:"
echo "- 如需立即恢复访问，可尝试备用端口: https://cailanzikzh.xin:8443"
echo "- 如问题持续存在，建议部署CDN服务"