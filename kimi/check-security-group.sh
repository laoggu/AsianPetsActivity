#!/bin/bash
# 检查腾讯云安全组配置

echo "=== 检查安全组和网络配置 ($(date)) ==="
echo ""

echo "[1] 获取实例信息..."
INSTANCE_ID=$(curl -s http://metadata.tencentyun.com/latest/meta-data/instance-id 2>/dev/null)
echo "实例ID: ${INSTANCE_ID:-无法获取}"
echo "公网IP: 101.43.57.35"
echo "内网IP: 10.0.0.10"
echo ""

echo "[2] 检查sshd日志中的外部连接..."
echo "最近的外部IP连接sshd (证明外部能到达服务器):"
sudo grep "Accepted" /var/log/auth.log 2>/dev/null | tail -5 || sudo grep "Accepted" /var/log/secure 2>/dev/null | tail -5 || echo "无法读取日志"
echo ""

echo "[3] 检查tcpdump能否抓到外部443..."
echo "请在另一个终端执行: curl -I -k https://cailanzikzh.xin"
echo "开始抓包5秒..."
timeout 5 tcpdump -i eth0 port 443 -nn -c 10 2>/dev/null || echo "tcpdump无权限或超时"
echo ""

echo "[4] 检查iptables是否记录DROP..."
echo "是否有日志规则:"
iptables -L -n -v | grep LOG | head -5
echo ""

echo "[5] 检查系统消息日志..."
echo "最近的内核消息:"
sudo dmesg | tail -20 | grep -iE "drop|reject|443|firewall" || echo "无相关消息"
echo ""

echo "[6] 检查腾讯云CLI (如已安装)..."
if command -v tccli &> /dev/null; then
    echo "腾讯云CLI已安装，尝试获取安全组..."
    tccli vpc DescribeSecurityGroupPolicies --filters "Name=security-group-id,Values=$(curl -s http://metadata.tencentyun.com/latest/meta-data/security-groups 2>/dev/null)" 2>/dev/null || echo "无法获取安全组信息"
else
    echo "腾讯云CLI未安装"
fi
echo ""

echo "[7] 手动测试443端口连通性..."
echo "使用nc测试..."
timeout 2 nc -zv 101.43.57.35 443 2>&1 || echo "nc测试失败"
echo ""

echo "=== 检查完成 ==="
echo ""
echo "建议:"
echo "1. 登录腾讯云控制台，检查安全组规则是否真正生效"
echo "2. 检查是否有网络ACL限制"
echo "3. 检查是否有其他云防火墙产品"
