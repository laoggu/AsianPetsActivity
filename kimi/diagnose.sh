#!/bin/bash
# 诊断脚本 - 检查HTTPS连接被重置问题

echo "=== HTTPS 连接问题诊断 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || cd /opt/AsianPetsSystem 2>/dev/null

echo "[1] Nginx 容器状态"
docker-compose ps nginx 2>/dev/null || docker compose ps nginx
echo ""

echo "[2] 证书检查"
openssl x509 -in nginx/ssl/cailanzikzh.xin.pem -noout -dates 2>/dev/null && echo "证书有效"
echo ""

echo "[3] 本地 HTTPS 测试 (服务器本地)"
LOCAL_HTTPS=$(curl -s -o /dev/null -w "%{http_code}" -k https://localhost:443)
echo "本地HTTPS状态码: $LOCAL_HTTPS"
if [ "$LOCAL_HTTPS" = "302" ] || [ "$LOCAL_HTTPS" = "200" ] || [ "$LOCAL_HTTPS" = "301" ]; then
    echo "✅ 服务器本地HTTPS正常"
else
    echo "❌ 服务器本地HTTPS异常"
fi
echo ""

echo "[4] nftables 规则检查 (重点: DROP/REJECT规则)"
echo "--- filter表规则 ---"
nft list table ip filter 2>/dev/null || echo "无ip filter表"
echo ""
echo "--- 所有DROP/REJECT规则 ---"
nft list ruleset 2>/dev/null | grep -E "drop|reject|DROP|REJECT" | head -20
echo ""

echo "[5] iptables 规则检查"
echo "--- filter表 INPUT链 ---"
iptables -L INPUT -n -v --line-numbers 2>/dev/null | head -20
echo ""
echo "--- 443端口相关规则 ---"
iptables -L -n -v 2>/dev/null | grep -E "443|dpt:https" | head -10
echo ""

echo "[6] 连接跟踪状态"
echo "当前连接数 / 最大连接数:"
sysctl net.netfilter.nf_conntrack_count 2>/dev/null
sysctl net.netfilter.nf_conntrack_max 2>/dev/null
echo ""
echo "443端口连接状态:"
conntrack -L 2>/dev/null | grep 443 | head -5 || echo "无443连接或无conntrack命令"
echo ""

echo "[7] 端口监听状态"
echo "--- 所有443端口监听 ---"
ss -tlnp | grep 443
echo ""
echo "--- Docker端口映射 ---"
docker ps --format "table {{.Names}}\t{{.Ports}}" 2>/dev/null | grep -E "nginx|443" || docker ps | grep -E "nginx|443"
echo ""

echo "[8] NAT/DNAT规则"
echo "--- PREROUTING链 ---"
iptables -t nat -L PREROUTING -n -v --line-numbers 2>/dev/null | head -15
echo ""
echo "--- 443相关NAT规则 ---"
iptables -t nat -L -n -v 2>/dev/null | grep 443
echo ""

echo "[9] 系统日志 (最近防火墙相关)"
echo "--- 内核日志 ---"
dmesg 2>/dev/null | grep -iE "firewall|drop|reject|443" | tail -10 || echo "无相关日志"
echo ""
echo "--- 系统日志 ---"
journalctl -k 2>/dev/null | grep -iE "drop|reject|443" | tail -10 || echo "无相关日志"
echo ""

echo "=== 诊断完成 ==="
echo ""
echo "关键检查点:"
echo "1. 如果[4]或[5]有DROP/REJECT规则涉及443端口，那就是问题所在"
echo "2. 如果[6]连接跟踪满了，也会导致连接问题"
echo "3. 如果[8]DNAT规则不正确，流量无法转发到Docker"
