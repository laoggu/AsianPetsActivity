@echo off
setlocal enabledelayedexpansion

echo 🔧 快速修复JWT认证问题...

REM 备份当前配置
echo 📋 备份当前配置...
copy "src\main\java\org\example\asianpetssystem\security\JwtAuthenticationFilter.java" "src\main\java\org\example\asianpetssystem\security\JwtAuthenticationFilter.java.backup" >nul 2>&1

REM 重新构建镜像
echo 🔨 重新构建Docker镜像...
docker-compose -f docker-compose.prod.yml build --no-cache
if %errorlevel% neq 0 (
    echo ❌ 镜像构建失败
    exit /b 1
)

REM 停止现有服务
echo ⏹️ 停止现有服务...
docker-compose -f docker-compose.prod.yml down

REM 启动新服务
echo ▶️ 启动修复后的服务...
docker-compose -f docker-compose.prod.yml up -d

REM 等待服务启动
echo ⏳ 等待服务启动...
timeout /t 60 /nobreak >nul

REM 验证修复效果
echo 🧪 验证修复效果...

echo 检查Swagger UI访问:
curl -f http://localhost:8081/swagger-ui/index.html >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Swagger UI访问正常
) else (
    echo ❌ Swagger UI访问仍有问题
)

echo 检查健康检查端点:
curl -f http://localhost:8081/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ 健康检查端点正常
) else (
    echo ❌ 健康检查仍有问题
)

echo 检查通用接口:
curl -f http://localhost:8081/api/common/system-config >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ 通用接口访问正常
) else (
    echo ❌ 通用接口访问仍有问题
)

REM 显示服务状态
echo 📋 当前服务状态:
docker-compose -f docker-compose.prod.yml ps

REM 显示最近日志
echo 📝 最近的应用日志:
docker-compose -f docker-compose.prod.yml logs app --tail=20

echo ✅ JWT认证修复完成！
echo 如需查看详细日志，请运行: docker-compose -f docker-compose.prod.yml logs -f app

pause