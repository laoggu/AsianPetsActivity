#!/bin/bash

echo "=== NAT/公网 IP 检查 ==="
echo ""

# 1. 获取公网 IP
echo "[1] 获取公网 IP"
curl -s http://ip.sb || curl -s http://ifconfig.me || echo "无法获取公网 IP"
echo ""

# 2. 检查 IP 转发
echo "[2] IP 转发设置"
sysctl net.ipv4.ip_forward
echo ""

# 3. 检查 iptables NAT 规则
echo "[3] iptables NAT 规则"
iptables -t nat -L -n | grep -E "DNAT|SNAT|REDIRECT" | head -20
echo ""

# 4. 检查是否有其他防火墙工具
echo "[4] 其他防火墙"
which firewalld && systemctl status firewalld --no-pager || echo "firewalld 未安装"
which nft && nft list ruleset 2>/dev/null | head -20 || echo "nftables 未使用"
echo ""

# 5. 测试通过公网 IP 访问
echo "[5] 测试本地通过公网 IP 访问"
timeout 3 bash -c "echo > /dev/tcp/101.43.57.35/443" 2>/dev/null && echo "公网 443 可连接" || echo "公网 443 不可连接"
echo ""

echo "=== 建议 ==="
echo "如果公网 IP 无法连接但本地可以，问题在："
echo "1. 云厂商的 NAT 网关配置"
echo "2. 共享带宽/网络配置"
echo "3. 负载均衡器配置（如果有）"
echo ""
echo "请检查腾讯云控制台："
echo "- NAT 网关"
echo "- 弹性公网 IP (EIP) 绑定"
echo "- 负载均衡 (CLB) 配置"
