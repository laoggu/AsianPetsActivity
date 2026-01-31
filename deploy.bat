@echo off
setlocal enabledelayedexpansion

echo 🚀 开始部署亚洲宠物协会系统...

REM 检查必要文件
echo 🔍 检查必要文件...
if not exist "Dockerfile" (
    echo ❌ Dockerfile 不存在
    exit /b 1
)

if not exist "docker-compose.prod.yml" (
    echo ❌ docker-compose.prod.yml 不存在
    exit /b 1
)

if not exist ".env" (
    echo ❌ .env 文件不存在
    exit /b 1
)

REM 构建镜像
echo 🔨 构建Docker镜像...
docker-compose -f docker-compose.prod.yml build
if %errorlevel% neq 0 (
    echo ❌ 镜像构建失败
    exit /b 1
)

REM 停止现有容器
echo ⏹️ 停止现有容器...
docker-compose -f docker-compose.prod.yml down

REM 启动服务
echo ▶️ 启动服务...
docker-compose -f docker-compose.prod.yml up -d

REM 等待服务启动
echo ⏳ 等待服务启动...
timeout /t 30 /nobreak >nul

REM 检查服务状态
echo 📋 检查服务状态...
docker-compose -f docker-compose.prod.yml ps

echo 🎉 部署完成！
echo 查看日志: docker-compose -f docker-compose.prod.yml logs -f
echo 停止服务: docker-compose -f docker-compose.prod.yml down
echo 重启服务: docker-compose -f docker-compose.prod.yml restart

pause