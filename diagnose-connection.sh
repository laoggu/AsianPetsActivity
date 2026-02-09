#!/bin/bash

echo "=== 连接问题诊断脚本 ==="
echo ""

cd /root/AsianPetsSystem 2>/dev/null || cd ~/AsianPetsSystem 2>/dev/null || cd /opt/AsianPetsSystem 2>/dev/null || cd $(dirname $0)

echo "[1] 检查 Docker 容器状态"
echo "------------------------"
docker-compose ps
echo ""

echo "[2] 检查 Docker 网络"
echo "------------------------"
docker network ls | grep asianpets
echo ""
docker network inspect asianpets-network 2>/dev/null || docker network inspect asianpets AsianPetsSystem_asianpets-network 2>/dev/null
echo ""

echo "[3] 检查 app 容器是否可以访问"
echo "------------------------"
docker exec asianpets-nginx nslookup app 2>/dev/null || echo "无法解析 'app' 主机名"
docker exec asianpets-nginx ping -c 1 app 2>/dev/null || echo "无法 ping 通 'app'"
echo ""

echo "[4] 检查 nginx 日志（最近 10 条错误）"
echo "------------------------"
docker exec asianpets-nginx tail -10 /var/log/nginx/error.log 2>/dev/null || tail -10 nginx/logs/error.log
echo ""

echo "[5] 检查 SSL 证书"
echo "------------------------"
if [ -f nginx/ssl/cailanzikzh.xin.pem ]; then
    echo "证书文件存在"
    openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -subject -dates 2>/dev/null || echo "无法读取证书信息"
else
    echo "❌ 证书文件不存在!"
fi
echo ""

echo "[6] 测试从 nginx 访问后端"
echo "------------------------"
docker exec asianpets-nginx wget -qO- http://app:8081/actuator/health 2>/dev/null || echo "无法从 nginx 访问 app:8081"
echo ""

echo "[7] 检查端口监听"
echo "------------------------"
netstat -tlnp 2>/dev/null | grep -E ':(80|443)' || ss -tlnp 2>/dev/null | grep -E ':(80|443)'
echo ""

echo "[8] DNS 解析检查"
echo "------------------------"
echo "域名 cailanzikzh.xin 解析到:"
nslookup cailanzikzh.xin 2>/dev/null | grep -A1 "Name:" || echo "无法解析域名"
echo ""
echo "本机 IP:"
ip addr show 2>/dev/null | grep "inet " || ifconfig 2>/dev/null | grep "inet "
echo ""

echo "=== 诊断完成 ==="
echo ""
echo "常见问题解决方案："
echo ""
echo "1. 如果 'nslookup app' 失败，说明 Docker DNS 有问题："
echo "   解决方案: docker-compose restart"
echo ""
echo "2. 如果证书过期："
echo "   检查: openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -dates"
echo "   需要重新申请证书"
echo ""
echo "3. 如果后端无法访问："
echo "   检查: docker-compose logs app"
echo ""
echo "4. 如果域名解析错误："
echo "   请检查域名 DNS A 记录是否指向 101.43.57.35"
