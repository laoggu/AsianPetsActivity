#!/bin/bash
# 修复脚本 - 重启 Nginx 并检查状态

echo "=== 修复 Nginx ==="

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 重启 Nginx..."
docker-compose restart nginx
sleep 3

echo ""
echo "[2] 检查状态..."
docker-compose ps nginx

echo ""
echo "[3] 测试本地 HTTPS..."
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" -k https://localhost:443

echo ""
echo "[4] 查看最近日志..."
docker-compose logs --tail=10 nginx

echo ""
echo "=== 完成 ==="
