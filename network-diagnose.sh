#!/bin/bash

echo "=== 网络连接诊断 ==="
echo ""

# 1. 检查服务器 IP
echo "[1] 服务器 IP 地址"
ip addr show | grep "inet " | head -5
echo ""

# 2. 检查路由
echo "[2] 路由表"
ip route | head -10
echo ""

# 3. 检查 443 端口监听详情
echo "[3] 443 端口监听详情"
ss -tlnp | grep 443
echo ""

# 4. 测试从外部连接（请在你的本地电脑执行）
echo "[4] 请在你的本地电脑（Windows CMD）执行以下命令："
echo "    tracert 101.43.57.35"
echo "    telnet 101.43.57.35 443"
echo ""

# 5. 检查是否有其他防火墙
echo "[5] 检查其他防火墙"
iptables -L INPUT -n --line-numbers | grep -E "443|REJECT|DROP"
echo ""

# 6. 测试 TCP 连接
echo "[6] 本地 TCP 测试"
timeout 3 bash -c 'echo > /dev/tcp/localhost/443' && echo "本地 443: 正常" || echo "本地 443: 失败"
timeout 3 bash -c 'echo > /dev/tcp/127.0.0.1/443' && echo "127.0.0.1 443: 正常" || echo "127.0.0.1 443: 失败"
echo ""

# 7. 检查网络接口
echo "[7] 网络接口"
ip link show | grep -E "UP|DOWN"
echo ""

# 8. 测试使用不同方式监听
echo "[8] 建议测试"
echo "请在你的本地电脑执行:"
echo "  curl -v https://101.43.57.35 --insecure"
echo "  curl -v https://cailanzikzh.xin --insecure"
