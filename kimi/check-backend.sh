#!/bin/bash
# 检查后端应用和Nginx状态

echo "=== 检查后端应用和Nginx ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 检查所有容器状态..."
docker-compose ps
echo ""

echo "[2] 检查后端应用健康状态..."
curl -s http://localhost:8081/actuator/health 2>&1 || echo "后端健康检查失败"
echo ""

echo "[3] 测试直接访问后端..."
curl -s -o /dev/null -w "HTTP状态码: %{http_code}\n" http://localhost:8081/ 2>&1
echo ""

echo "[4] 检查Nginx错误日志 (最近50行)..."
docker-compose logs --tail=50 nginx 2>&1
echo ""

echo "[5] 检查后端应用日志 (最近20行)..."
docker-compose logs --tail=20 app 2>&1
echo ""

echo "[6] 检查Nginx连接后端是否成功..."
docker exec asianpets-nginx wget -q -O- http://app:8081/actuator/health 2>&1 || echo "Nginx无法连接后端"
echo ""

echo "[7] 检查Docker网络..."
docker network inspect asianpets-network 2>&1 | grep -E '"Name"|"IPv4"'
echo ""

echo "[8] 检查Nginx配置中的upstream..."
docker exec asianpets-nginx cat /etc/nginx/nginx.conf | grep -A5 "upstream"
echo ""

echo "=== 检查完成 ==="
