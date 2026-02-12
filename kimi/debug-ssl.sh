    #!/bin/bash
    # 详细SSL调试脚本

    echo "=== SSL 详细调试 ($(date)) ==="
    echo ""

    cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

    echo "[1] 检查主机Nginx进程是否占用443..."
    ss -tlnp | grep 443
    echo ""

    echo "[2] 检查Docker代理进程..."
    ps aux | grep docker-proxy | grep 443
    echo ""

    echo "[3] 检查证书文件权限..."
    ls -la nginx/ssl/
    file nginx/ssl/cailanzikzh.xin.pem
    file nginx/ssl/cailanzikzh.xin.key
    echo ""

    echo "[4] 检查证书格式..."
    echo "证书PEM格式检查:"
    grep -c "BEGIN CERTIFICATE" nginx/ssl/cailanzikzh.xin.pem
    grep -c "END CERTIFICATE" nginx/ssl/cailanzikzh.xin.pem
    echo ""
    echo "私钥格式检查:"
    grep -c "BEGIN PRIVATE KEY\|BEGIN RSA PRIVATE KEY" nginx/ssl/cailanzikzh.xin.key
    grep -c "END PRIVATE KEY\|END RSA PRIVATE KEY" nginx/ssl/cailanzikzh.xin.key
    echo ""

    echo "[5] 在容器外测试到容器的直接连接..."
    # 直接测试Docker容器IP
    curl -k -I https://172.18.0.4:443 2>&1 | head -10
    echo ""

    echo "[6] 使用OpenSSL详细调试..."
    echo "详细TLS握手调试 (可能会显示错误原因):"
    openssl s_client -connect localhost:443 -msg -debug 2>&1 | tail -50
    echo ""

    echo "[7] 检查Nginx错误日志..."
    docker-compose logs nginx 2>&1 | grep -iE "error|emerg|alert|ssl|tls" | tail -30
    echo ""

    echo "[8] 检查系统日志中Nginx相关..."
    sudo dmesg 2>/dev/null | grep -iE "nginx|ssl|443" | tail -10 || echo "无相关日志"
    echo ""

    echo "=== 调试完成 ==="
