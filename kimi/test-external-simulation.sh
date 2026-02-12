#!/bin/bash
# 模拟外部访问测试

echo "=== 模拟外部访问测试 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "[1] 检查公网IP绑定..."
echo "当前服务器公网IP:"
curl -s http://metadata.tencentyun.com/latest/meta-data/public-ipv4 2>/dev/null || echo "101.43.57.35 (已知)"
echo ""

echo "[2] 使用公网IP从服务器内部测试..."
curl -I -k --connect-timeout 5 https://101.43.57.35:443 2>&1
echo ""

echo "[3] 检查Docker端口映射详情..."
docker port asianpets-nginx
echo ""

echo "[4] 检查netstat外部连接..."
ss -tn | grep 443 | head -10
echo ""

echo "[5] 检查是否有TCP连接建立..."
echo "当前443端口连接数:"
ss -tn state established '( dport = :443 or sport = :443 )' | wc -l
echo ""

echo "[6] 检查Nginx访问日志是否有外部请求..."
echo "最近的外部访问记录:"
docker exec asianpets-nginx tail -20 /var/log/nginx/access.log 2>/dev/null | grep -v localhost || echo "无外部访问记录"
echo ""

echo "[7] 检查Nginx错误日志..."
docker exec asianpets-nginx tail -20 /var/log/nginx/error.log 2>/dev/null || echo "无错误日志"
echo ""

echo "[8] 测试MTU问题 - 使用小数据包..."
echo "测试使用小MTU..."
ping -c 3 -M do -s 1400 101.43.57.35 2>/dev/null || echo "ping测试失败"
echo ""

echo "[9] 检查iptables NAT表..."
echo "--- PREROUTING ---"
iptables -t nat -L PREROUTING -n -v | grep 443
echo ""
echo "--- Docker NAT规则 ---"
iptables -t nat -L DOCKER -n -v | grep 443
echo ""

echo "[10] 检查连接跟踪..."
echo "当前443连接跟踪:"
conntrack -L -p tcp --dport 443 2>/dev/null | head -5 || echo "无conntrack命令或无连接"
echo ""

echo "=== 测试完成 ==="
