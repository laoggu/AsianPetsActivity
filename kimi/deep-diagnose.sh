    #!/bin/bash
    # 深度诊断 - 抓包分析流量是否到达服务器

    echo "=== 深度诊断 ($(date)) ==="
    echo ""

    echo "[1] 检查公网接口..."
    ip addr show | grep -E "inet |eth|ens"
    echo ""

    echo "[2] 查看路由表..."
    ip route show
    echo ""

    echo "[3] 检查腾讯云CLI/元数据 (如果是腾讯云)..."
    # 尝试获取实例元数据
    PUBLIC_IP=$(curl -s --connect-timeout 2 http://metadata.tencentyun.com/latest/meta-data/public-ipv4 2>/dev/null || echo "无法获取")
    echo "元数据公网IP: $PUBLIC_IP"
    echo ""

    echo "[4] 实时监控443端口流量 (持续10秒)..."
    echo "请现在从外部执行: curl -I -k https://cailanzikzh.xin"
    echo "开始抓包..."
    timeout 10 tcpdump -i any port 443 -nn -c 20 2>/dev/null || echo "tcpdump执行失败或无权限"
    echo ""

    echo "[5] 检查所有接口上的443流量统计..."
    for iface in $(ls /sys/class/net/ 2>/dev/null); do
        echo "--- 接口: $iface ---"
        tcpdump -i $iface port 443 -nn -c 5 2>/dev/null &
        PID=$!
        sleep 3
        kill $PID 2>/dev/null
        wait $PID 2>/dev/null
        echo ""
    done

    echo "[6] 检查腾讯云云防火墙/安全组 (使用tccli)..."
    if command -v tccli &> /dev/null; then
        echo "腾讯云CLI已安装"
        tccli cvm DescribeSecurityGroups --limit 1 2>/dev/null | head -20 || echo "tccli需要配置凭证"
    else
        echo "腾讯云CLI未安装，尝试其他方式..."
    fi
    echo ""

    echo "[7] 检查系统级防火墙 (ufw/firewalld)..."
    if command -v ufw &> /dev/null; then
        echo "--- UFW状态 ---"
        ufw status verbose 2>/dev/null || echo "ufw未启用"
    fi
    if command -v firewall-cmd &> /dev/null; then
        echo "--- Firewalld状态 ---"
        firewall-cmd --state 2>/dev/null || echo "firewalld未运行"
        firewall-cmd --list-all 2>/dev/null | head -20
    fi
    echo ""

    echo "[8] 检查mangle表是否有异常规则..."
    echo "--- iptables mangle表 ---"
    iptables -t mangle -L -n -v --line-numbers 2>/dev/null | head -30
    echo ""

    echo "[9] 检查raw表..."
    echo "--- iptables raw表 ---"
    iptables -t raw -L -n -v --line-numbers 2>/dev/null | head -20
    echo ""

    echo "[10] 检查conntrack中443连接..."
    conntrack -L -p tcp --dport 443 2>/dev/null | head -10 || echo "无443连接跟踪"
    echo ""

    echo "=== 诊断完成 ==="
    echo ""
    echo "请分析:"
    echo "1. 如果[4]或[5]能看到外部IP的SYN包，说明流量到达服务器，问题在防火墙规则"
    echo "2. 如果[4][5]看不到任何包，说明流量被云安全组或网络层拦截"
    echo "3. 检查[8][9]是否有DROP/REJECT规则在mangle/raw表"
