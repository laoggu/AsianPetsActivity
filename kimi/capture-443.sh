#!/bin/bash
# 抓包监控443端口 - 用于确认流量是否到达服务器

echo "=== 443端口流量抓包 ($(date)) ==="
echo ""

# 获取主网卡
MAIN_IFACE=$(ip route | grep default | awk '{print $5}' | head -1)
echo "主网卡: $MAIN_IFACE"
echo ""

echo "请在另一个终端或本地电脑执行:"
echo "  curl -I -k https://cailanzikzh.xin"
echo ""
echo "开始抓包 (按Ctrl+C停止)..."
echo ""

# 抓取443端口，显示详细信息
tcpdump -i $MAIN_IFACE port 443 -nn -v
