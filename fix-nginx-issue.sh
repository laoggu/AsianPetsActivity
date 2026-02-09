#!/bin/bash

echo "=== 修复 Nginx 连接问题 ==="

# 1. 检查 docker-compose 是否在运行
echo "[1/5] 检查容器状态..."
cd /root/AsianPetsSystem 2>/dev/null || cd ~/AsianPetsSystem 2>/dev/null || cd /opt/AsianPetsSystem 2>/dev/null || cd $(dirname $0)

# 2. 检查 nginx 配置语法
echo "[2/5] 检查 nginx 配置..."
docker run --rm -v $(pwd)/nginx/conf/nginx.conf:/etc/nginx/nginx.conf:ro nginx:alpine nginx -t

if [ $? -ne 0 ]; then
    echo "❌ Nginx 配置语法错误！"
    exit 1
fi

echo "✅ Nginx 配置语法正确"

# 3. 重启 nginx 容器
echo "[3/5] 重启 nginx 容器..."
docker-compose restart nginx

# 4. 检查容器状态
echo "[4/5] 检查容器状态..."
sleep 3
docker-compose ps

# 5. 测试连接
echo "[5/5] 测试连接..."
echo "测试本地连接..."
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" http://localhost:80
curl -s -o /dev/null -w "HTTPS Status: %{http_code}\n" -k https://localhost:443

echo ""
echo "=== 修复完成 ==="
echo ""
echo "请通过以下方式验证："
echo "1. 浏览器访问: http://cailanzikzh.xin"
echo "2. 浏览器访问: https://cailanzikzh.xin"
echo "3. IP 访问: http://101.43.57.35"
echo ""
echo "如果仍有问题，请检查："
echo "- 域名 DNS 解析是否正确指向 101.43.57.35"
echo "- SSL 证书是否过期: openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -dates"
echo "- 防火墙/安全组是否开放 80/443 端口"
echo ""
echo "查看实时日志: docker-compose logs -f nginx"
