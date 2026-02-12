#!/bin/bash
# SSL对比测试 - 找出本地和外部访问的差异

echo "=== SSL对比测试 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 使用不同方式测试HTTPS..."
echo ""

echo "--- 方式1: 本地回环 ---"
curl -I -k https://localhost:443 2>&1 | head -5
echo ""

echo "--- 方式2: 内网IP ---"
curl -I -k https://10.0.0.10:443 2>&1 | head -5
echo ""

echo "--- 方式3: Docker容器IP ---"
curl -I -k https://172.18.0.4:443 2>&1 | head -5
echo ""

echo "[2] 测试不同TLS版本..."
echo "--- TLS 1.2 ---"
curl -I -k --tlsv1.2 https://localhost:443 2>&1 | head -3
echo ""
echo "--- TLS 1.3 ---"
curl -I -k --tlsv1.3 https://localhost:443 2>&1 | head -3
echo ""

echo "[3] 检查Nginx worker进程..."
docker exec asianpets-nginx ps aux | grep nginx
echo ""

echo "[4] 检查Nginx监听的端口..."
docker exec asianpets-nginx netstat -tlnp 2>/dev/null || docker exec asianpets-nginx ss -tlnp
echo ""

echo "[5] 检查证书文件在容器内的权限..."
docker exec asianpets-nginx ls -la /opt/ssl/
echo ""

echo "[6] 检查Nginx用户..."
docker exec asianpets-nginx grep -E "user|worker" /etc/nginx/nginx.conf | head -5
echo ""

echo "[7] 测试不验证证书的连接..."
echo "--- 使用OpenSSL (localhost) ---"
echo | openssl s_client -connect localhost:443 -servername cailanzikzh.xin 2>&1 | grep -E "Protocol|Cipher|Verify|error"
echo ""

echo "[8] 检查主机防火墙是否针对外部IP..."
echo "--- INPUT链443计数 ---"
iptables -L INPUT -n -v | grep 443
echo ""
echo "--- 检查最近是否有443相关DROP ---"
iptables -L -n -v | grep -i drop | head -10
echo ""

echo "[9] 检查nftables INPUT链计数变化..."
nft list chain ip filter INPUT 2>/dev/null | grep "tcp dport 443"
echo ""

echo "=== 测试完成 ==="
echo ""
echo "分析:"
echo "- 如果本地和Docker IP测试都正常，但外部不行，说明问题在防火墙/安全组层面"
echo "- 如果证书权限有问题，Nginx会报错"
echo "- 如果TLS版本有问题，特定版本测试会失败"
