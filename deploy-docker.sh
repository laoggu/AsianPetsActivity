#!/bin/bash

# Asian Pets System - Docker 部署脚本
# 使用方法: ./deploy-docker.sh

set -e

echo "==================================="
echo "Asian Pets System Docker 部署脚本"
echo "==================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装${NC}"
    echo "请先安装 Docker: https://docs.docker.com/engine/install/"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}错误: Docker Compose 未安装${NC}"
    echo "请先安装 Docker Compose"
    exit 1
fi

# 使用 docker compose 或 docker-compose
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

echo -e "${GREEN}✓ Docker 环境检查通过${NC}"

# 检查必要文件
echo ""
echo "检查必要文件..."

if [ ! -f ".env" ]; then
    echo -e "${RED}错误: .env 文件不存在${NC}"
    echo "请复制 .env.example 为 .env 并配置相关参数"
    exit 1
fi

if [ ! -f "nginx/ssl/cailanzikzh.xin.pem" ] || [ ! -f "nginx/ssl/cailanzikzh.xin.key" ]; then
    echo -e "${YELLOW}警告: SSL 证书文件不存在${NC}"
    echo "请确保证书文件位于 nginx/ssl/ 目录下:"
    echo "  - nginx/ssl/cailanzikzh.xin.pem"
    echo "  - nginx/ssl/cailanzikzh.xin.key"
    read -p "是否继续部署? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo -e "${GREEN}✓ 文件检查通过${NC}"

# 检查 MySQL 连接
echo ""
echo "检查 MySQL 连接..."
if command -v mysql &> /dev/null; then
    # 从 .env 文件读取数据库配置
    DB_HOST=$(grep SPRING_DATASOURCE_URL .env 2>/dev/null | grep -oP 'jdbc:mysql://\K[^:/]+' || echo "localhost")
    DB_PORT=$(grep SPRING_DATASOURCE_URL .env 2>/dev/null | grep -oP 'jdbc:mysql://[^:]+:\K[0-9]+' || echo "3306")
    
    echo "尝试连接 MySQL: $DB_HOST:$DB_PORT"
    if timeout 5 bash -c "</dev/tcp/$DB_HOST/$DB_PORT" 2>/dev/null; then
        echo -e "${GREEN}✓ MySQL 可连接${NC}"
    else
        echo -e "${YELLOW}警告: 无法连接到 MySQL ($DB_HOST:$DB_PORT)${NC}"
        echo "请确保:"
        echo "  1. MySQL 服务已启动"
        echo "  2. 防火墙允许端口访问"
        echo "  3. 数据库配置正确（当前配置为连接宿主机数据库）"
        read -p "是否继续部署? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
else
    echo -e "${YELLOW}未安装 mysql 客户端，跳过连接检查${NC}"
fi

# 停止旧服务
echo ""
echo "停止旧服务..."
$COMPOSE_CMD down --remove-orphans 2>/dev/null || true

# 清理旧镜像（可选）
echo ""
read -p "是否清理旧的 Docker 镜像? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "清理旧镜像..."
    docker images -q asianpetssystem-app 2>/dev/null | xargs -r docker rmi -f 2>/dev/null || true
fi

# 构建并启动服务
echo ""
echo "构建并启动服务..."
$COMPOSE_CMD up -d --build

# 等待服务启动
echo ""
echo "等待服务启动..."
sleep 10

# 检查服务状态
echo ""
echo "检查服务状态..."
$COMPOSE_CMD ps

# 健康检查
echo ""
echo "执行健康检查..."
MAX_RETRY=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRY ]; do
    if curl -s -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ 应用服务健康检查通过${NC}"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "等待应用启动... ($RETRY_COUNT/$MAX_RETRY)"
    sleep 5
done

if [ $RETRY_COUNT -eq $MAX_RETRY ]; then
    echo -e "${RED}✗ 应用服务启动超时${NC}"
    echo "请检查日志: $COMPOSE_CMD logs app"
    exit 1
fi

# 检查 Nginx
echo ""
if curl -s -f http://localhost/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Nginx 服务运行正常${NC}"
else
    echo -e "${YELLOW}警告: Nginx 健康检查未通过${NC}"
    echo "请检查 Nginx 日志: $COMPOSE_CMD logs nginx"
fi

# 检查 Redis
echo ""
if $COMPOSE_CMD exec -T redis redis-cli ping 2>/dev/null | grep -q "PONG"; then
    echo -e "${GREEN}✓ Redis 服务运行正常${NC}"
else
    echo -e "${YELLOW}警告: Redis 连接检查未通过${NC}"
    echo "请检查 Redis 日志: $COMPOSE_CMD logs redis"
fi

echo ""
echo "==================================="
echo -e "${GREEN}部署完成!${NC}"
echo "==================================="
echo ""
echo "服务访问地址:"
echo "  - HTTP:  http://localhost (自动重定向到 HTTPS)"
echo "  - HTTPS: https://localhost"
echo "  - API:   https://localhost/api"
echo ""
echo "常用命令:"
echo "  查看日志:     $COMPOSE_CMD logs -f"
echo "  查看应用日志: $COMPOSE_CMD logs -f app"
echo "  重启服务:     $COMPOSE_CMD restart"
echo "  停止服务:     $COMPOSE_CMD down"
echo "  进入容器:     $COMPOSE_CMD exec app sh"
echo ""
echo "数据库连接信息（宿主机）:"
echo "  请确保 MySQL 允许来自 Docker 网络的连接"
echo ""
