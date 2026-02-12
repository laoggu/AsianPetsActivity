#!/bin/bash
# 解决网络环境对443端口的封锁

echo "=== 解决端口封锁问题 ($(date)) ==="
echo ""

cd /opt/AsianPetsActivity 2>/dev/null || cd ~/AsianPetsActivity 2>/dev/null || exit 1

echo "当前Nginx端口映射:"
docker port asianpets-nginx | grep 443
echo ""

echo "[方案1] 测试8443端口是否可用..."
echo "请从您的电脑执行: curl -I -k https://cailanzikzh.xin:8443"
echo ""

echo "[方案2] 添加更多备用端口(如4443)..."
read -p "是否添加4443端口映射? (y/n): " answer
if [ "$answer" = "y" ]; then
    echo "修改docker-compose.yml添加4443端口..."
    # 备份
    cp docker-compose.yml docker-compose.yml.bak
    # 添加4443端口映射
    sed -i 's/"8443:443"/"8443:443"\n      - "4443:443"/' docker-compose.yml
    echo "重新启动Nginx..."
    docker-compose up -d nginx
    echo "完成! 现在可以尝试 https://cailanzikzh.xin:4443"
fi
echo ""

echo "[方案3] 使用CDN隐藏真实IP..."
echo "建议: 使用腾讯云CDN或Cloudflare，将域名解析到CDN，避免直接暴露443端口"
echo ""

echo "=== 完成 ==="
