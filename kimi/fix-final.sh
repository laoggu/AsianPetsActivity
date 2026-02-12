#!/bin/bash
# 最终修复脚本 - 解决TLS decode_error问题

echo "=== 最终修复 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 检查Nginx容器内证书..."
docker exec asianpets-nginx ls -la /opt/ssl/ 2>&1
echo ""

echo "[2] 检查容器内证书内容..."
docker exec asianpets-nginx cat /opt/ssl/cailanzikzh.xin.pem 2>&1 | head -20
echo ""

echo "[3] 检查Nginx配置..."
docker exec asianpets-nginx cat /etc/nginx/nginx.conf 2>&1 | grep -E "ssl_|listen" | head -10
echo ""

echo "[4] 测试Nginx配置语法..."
docker exec asianpets-nginx nginx -t 2>&1
echo ""

echo "[5] 检查证书在容器内是否可读..."
docker exec asianpets-nginx sh -c "openssl x509 -in /opt/ssl/cailanzikzh.xin.pem -noout -subject 2>&1"
echo ""

echo "[6] 检查私钥在容器内是否可读..."
docker exec asianpets-nginx sh -c "openssl rsa -in /opt/ssl/cailanzikzh.xin.key -check -noout 2>&1"
echo ""

echo "[7] 检查Nginx错误日志..."
docker-compose logs --tail=50 nginx 2>&1 | grep -iE "error|emerg|ssl|tls|alert"
echo ""

echo "[8] 检查证书链顺序..."
echo "证书链中的证书 (应该服务器证书在前，中间证书在后):"
docker exec asianpets-nginx sh -c "openssl crl2pkcs7 -nocrl -certfile /opt/ssl/cailanzikzh.xin.pem | openssl pkcs7 -print_certs -noout 2>&1 | grep -E 'subject|issuer'"
echo ""

echo "[9] 尝试修复 - 重新加载Nginx..."
docker exec asianpets-nginx nginx -s reload 2>&1 || echo "reload失败，尝试重启"
docker-compose restart nginx
echo ""

echo "[10] 等待Nginx启动..."
sleep 3
echo ""

echo "[11] 最终测试..."
echo "主机本地测试:"
curl -I -k https://localhost:443 2>&1 | head -5
echo ""
echo "直接连接容器:"
curl -I -k https://172.18.0.4:443 2>&1 | head -5
echo ""

echo "=== 修复完成 ==="
echo ""
echo "请在本地电脑测试:"
echo "  curl -I -k https://cailanzikzh.xin"
