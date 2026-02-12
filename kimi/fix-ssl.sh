#!/bin/bash
# SSL/TLS问题诊断和修复脚本

echo "=== SSL/TLS 问题诊断 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 检查Nginx SSL配置..."
echo ""
grep -E "ssl_|listen.*443|tls|certificate" nginx/conf/nginx.conf | head -30
echo ""

echo "[2] 测试证书和私钥匹配..."
echo "证书MD5:"
openssl x509 -noout -modulus -in nginx/ssl/cailanzikzh.xin.pem 2>/dev/null | openssl md5
echo "私钥MD5:"
openssl rsa -noout -modulus -in nginx/ssl/cailanzikzh.xin.key 2>/dev/null | openssl md5
echo ""

echo "[3] 检查证书链完整性..."
echo "证书链中的证书数量:"
openssl crl2pkcs7 -nocrl -certfile nginx/ssl/cailanzikzh.xin.pem 2>/dev/null | openssl pkcs7 -print_certs -noout 2>/dev/null | grep -c "subject=" || echo "1"
echo ""
echo "证书详情:"
openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -subject -issuer 2>/dev/null
echo ""

echo "[4] 使用OpenSSL测试TLS握手 (详细)..."
echo "--- 测试TLS 1.2 ---"
openssl s_client -connect localhost:443 -tls1_2 -servername cailanzikzh.xin 2>&1 < /dev/null | grep -E "Protocol|Cipher|Verify|error|failure"
echo ""
echo "--- 测试TLS 1.3 ---"
openssl s_client -connect localhost:443 -tls1_3 -servername cailanzikzh.xin 2>&1 < /dev/null | grep -E "Protocol|Cipher|Verify|error|failure"
echo ""

echo "[5] 检查Nginx错误日志..."
echo "最近20行错误日志:"
docker-compose logs --tail=20 nginx 2>&1 | grep -iE "error|ssl|tls|handshake|alert"
echo ""

echo "[6] 检查证书有效期..."
openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -dates 2>/dev/null
echo ""

echo "[7] 验证完整证书链..."
echo "提取中间证书并验证链..."
openssl x509 -in nginx/ssl/cailanzikzh.xin.pem 2>/dev/null > /tmp/server_cert.pem
cat /tmp/server_cert.pem | openssl crl2pkcs7 -nocrl -certfile /dev/stdin 2>/dev/null | openssl pkcs7 -print_certs -noout 2>/dev/null
echo ""

echo "[8] 测试SNI支持..."
echo "不带SNI的测试:"
openssl s_client -connect localhost:443 2>&1 < /dev/null | grep -E "Protocol|Cipher|Verify" | head -3
echo "带SNI的测试:"
openssl s_client -connect localhost:443 -servername cailanzikzh.xin 2>&1 < /dev/null | grep -E "Protocol|Cipher|Verify" | head -3
echo ""

echo "=== 诊断完成 ==="
echo ""
echo "常见问题:"
echo "1. 证书链不完整 - 需要将中间证书追加到证书文件"
echo "2. SNI配置问题 - Nginx可能无法正确识别server_name"
echo "3. TLS版本不兼容 - 客户端/服务器TLS版本不匹配"
echo "4. 证书格式问题 - 证书或私钥格式不正确"
