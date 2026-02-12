#!/bin/bash
# 修复脚本 - 解决nftables/iptables阻止443入站问题

echo "=== 防火墙修复脚本 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || cd /opt/AsianPetsSystem 2>/dev/null

# 检查是否有明确的443 DROP规则
echo "[1] 检查现有防火墙规则..."
echo ""

echo "--- nftables规则 ---"
nft list ruleset 2>/dev/null | grep -E "443|https" | head -10 || echo "无nftables 443相关规则"
echo ""

echo "--- iptables INPUT链 ---"
iptables -L INPUT -n --line-numbers 2>/dev/null | head -20
echo ""

echo "[2] 添加允许443入站规则..."
echo ""

# 方法1: 使用iptables添加允许规则
echo "方式A: 通过iptables添加允许规则"

# 检查是否已有ACCEPT规则
EXIST_ACCEPT=$(iptables -C INPUT -p tcp --dport 443 -j ACCEPT 2>&1)
if [ $? -ne 0 ]; then
    echo "添加iptables规则: 允许443入站..."
    iptables -I INPUT 1 -p tcp --dport 443 -j ACCEPT 2>/dev/null && echo "✅ 已添加443允许规则(iptables)"
else
    echo "443允许规则已存在(iptables)"
fi

echo ""
echo "方式B: 通过nftables添加允许规则"

# 检查nftables是否有filter表
nft list table ip filter 2>/dev/null
if [ $? -eq 0 ]; then
    echo "nftables filter表存在"
    # 添加允许443的规则到input链
    nft add rule ip filter input tcp dport 443 accept 2>/dev/null && echo "✅ 已添加443允许规则(nftables)"
else
    echo "nftables无ip filter表，跳过"
fi

echo ""
echo "[3] 保存防火墙规则..."

# 尝试保存规则
if command -v netfilter-persistent &> /dev/null; then
    netfilter-persistent save 2>/dev/null && echo "✅ 已保存规则(netfilter-persistent)"
elif [ -f /etc/iptables/rules.v4 ]; then
    iptables-save > /etc/iptables/rules.v4 2>/dev/null && echo "✅ 已保存规则(/etc/iptables/rules.v4)"
elif [ -f /etc/sysconfig/iptables ]; then
    iptables-save > /etc/sysconfig/iptables 2>/dev/null && echo "✅ 已保存规则(/etc/sysconfig/iptables)"
else
    echo "⚠️ 无法自动保存规则，重启后可能失效"
fi

echo ""
echo "[4] 验证规则已生效..."
echo ""

echo "--- 当前INPUT链 ---"
iptables -L INPUT -n --line-numbers 2>/dev/null | head -15
echo ""

echo "[5] 测试本地HTTPS..."
curl -s -o /dev/null -w "本地HTTPS状态码: %{http_code}\n" -k https://localhost:443
echo ""

echo "=== 修复完成 ==="
echo ""
echo "请在本地电脑测试外部访问:"
echo "  curl -I -k https://cailanzikzh.xin"
echo ""
echo "如果仍有问题，请运行: bash kimi/diagnose.sh"
