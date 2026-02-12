#!/bin/bash
# 修复Docker容器内SSL问题

echo "=== 修复Docker SSL问题 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 检查证书文件权限..."
ls -la nginx/ssl/
echo ""

echo "[2] 检查Docker卷挂载..."
docker inspect asianpets-nginx | grep -A 10 "Mounts"
echo ""

echo "[3] 检查容器内证书是否存在..."
docker exec asianpets-nginx ls -la /opt/ssl/ 2>&1
echo ""

echo "[4] 检查容器内证书内容..."
docker exec asianpets-nginx head -5 /opt/ssl/cailanzikzh.xin.pem 2>&1
echo ""

echo "[5] 检查容器内私钥..."
docker exec asianpets-nginx head -5 /opt/ssl/cailanzikzh.xin.key 2>&1
echo ""

echo "[6] 测试Nginx配置..."
docker exec asianpets-nginx nginx -t 2>&1
echo ""

echo "[7] 检查Nginx错误日志..."
docker-compose logs --tail=50 nginx 2>&1 | tail -20
echo ""

echo "[8] 重启Nginx容器..."
docker-compose restart nginx
echo ""

echo "[9] 等待Nginx启动..."
sleep 3
echo ""

echo "[10] 从主机测试HTTPS..."
curl -I -k https://localhost:443 2>&1
echo ""

echo "[11] 检查Nginx是否监听443..."
ss -tlnp | grep 443
docker exec asianpets-nginx ss -tlnp 2>/dev/null | grep 443 || echo "容器内ss命令不可用"
echo ""

echo "=== 修复完成 ==="
echo ""
echo "如果仍有问题，请检查:"
echo "1. 证书文件是否正确挂载到容器"
echo "2. Nginx配置文件语法"
echo "3. 证书文件格式是否正确"
