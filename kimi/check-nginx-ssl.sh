#!/bin/bash
# 检查Nginx容器内的SSL配置

echo "=== 检查Nginx容器内部SSL配置 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 检查容器内证书文件是否存在..."
docker exec asianpets-nginx ls -la /opt/ssl/ 2>/dev/null || echo "证书目录不存在或无法访问"
echo ""

echo "[2] 检查容器内证书内容..."
docker exec asianpets-nginx cat /opt/ssl/cailanzikzh.xin.pem 2>/dev/null | head -30 || echo "无法读取证书"
echo ""

echo "[3] 检查容器内私钥..."
docker exec asianpets-nginx cat /opt/ssl/cailanzikzh.xin.key 2>/dev/null | head -10 || echo "无法读取私钥"
echo ""

echo "[4] 检查Nginx配置文件..."
docker exec asianpets-nginx cat /etc/nginx/nginx.conf 2>/dev/null | grep -E "ssl_|listen|server_name" | head -20
echo ""

echo "[5] 测试Nginx配置语法..."
docker exec asianpets-nginx nginx -t 2>&1
echo ""

echo "[6] 检查Nginx错误日志 (实时)..."
echo "最近30行错误日志:"
docker-compose logs --tail=30 nginx 2>&1
echo ""

echo "[7] 检查证书链是否完整 (容器内测试)..."
docker exec asianpets-nginx sh -c "openssl crl2pkcs7 -nocrl -certfile /opt/ssl/cailanzikzh.xin.pem | openssl pkcs7 -print_certs -noout" 2>/dev/null || echo "测试失败"
echo ""

echo "[8] 检查中间证书..."
echo "证书文件中的证书数量:"
docker exec asianpets-nginx sh -c "grep -c 'BEGIN CERTIFICATE' /opt/ssl/cailanzikzh.xin.pem" 2>/dev/null || echo "无法读取"
echo ""

echo "[9] 在容器内测试HTTPS..."
docker exec asianpets-nginx wget -q -O- --no-check-certificate https://localhost/ 2>&1 | head -5 || echo "wget测试失败"
echo ""

echo "=== 诊断完成 ==="
