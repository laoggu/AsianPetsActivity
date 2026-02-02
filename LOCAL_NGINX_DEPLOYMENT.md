# 本地Nginx部署方案

## 方案概述

本方案采用**服务器本地Nginx + Docker应用容器**的架构，解决了容器化Nginx带来的DNS解析和SSL配置复杂问题。

## 架构优势

- ✅ 避免容器网络DNS解析问题
- ✅ 简化SSL证书配置和管理
- ✅ 降低网络复杂度
- ✅ 便于监控和故障排查
- ✅ 支持灵活的负载均衡配置

## 部署文件

```
├── docker-compose-local-nginx.yml    # 不包含Nginx的Docker配置
├── nginx/conf/
│   ├── nginx-local.conf             # HTTP版本本地Nginx配置
│   └── nginx-local-https.conf       # HTTPS版本本地Nginx配置
├── server-deploy-local-nginx.sh     # 服务器部署脚本
└── LOCAL_NGINX_DEPLOYMENT.md        # 本说明文件
```

## 部署步骤

### 1. 上传文件到服务器
```bash
# 在本地执行
scp -r C:\Users\14199\AsianPetsSystem ubuntu@your-server-ip:/home/ubuntu/
```

### 2. 在服务器上执行部署
```bash
# 登录服务器
ssh ubuntu@your-server-ip

# 进入项目目录
cd AsianPetsSystem

# 给脚本添加执行权限
chmod +x server-deploy-local-nginx.sh

# 执行部署（需要root权限）
sudo ./server-deploy-local-nginx.sh
```

## 配置说明

### HTTP部署
使用 `nginx-local.conf` 配置文件，默认监听80端口。

### HTTPS部署
1. 将SSL证书文件放置到服务器：
   - 证书文件：`/etc/ssl/certs/cailanzikzh.xin.pem`
   - 私钥文件：`/etc/ssl/private/cailanzikzh.xin.key`

2. 使用 `nginx-local-https.conf` 配置文件

3. 在部署脚本中修改Nginx配置文件引用：
```bash
# 修改 server-deploy-local-nginx.sh 中的这行
cp /opt/AsianPetsActivity/nginx/conf/nginx-local-https.conf /etc/nginx/sites-available/asianpets
```

## 管理命令

```bash
# 查看应用日志
docker-compose -f docker-compose-local-nginx.yml logs -f app

# 重启应用服务
docker-compose -f docker-compose-local-nginx.yml restart

# 停止所有服务
docker-compose -f docker-compose-local-nginx.yml down

# 查看Nginx状态
systemctl status nginx

# 重新加载Nginx配置
sudo nginx -t && sudo systemctl reload nginx

# 查看Nginx访问日志
tail -f /var/log/nginx/access.log
```

## 故障排查

### 应用无法访问
```bash
# 检查Docker容器状态
docker-compose -f docker-compose-local-nginx.yml ps

# 查看应用日志
docker-compose -f docker-compose-local-nginx.yml logs app

# 测试本地应用连通性
curl http://localhost:8081/actuator/health
```

### Nginx配置问题
```bash
# 测试Nginx配置
sudo nginx -t

# 查看Nginx错误日志
tail -f /var/log/nginx/error.log

# 重新启动Nginx
sudo systemctl restart nginx
```

## 注意事项

1. 确保服务器80/443端口已开放
2. 域名已正确解析到服务器IP
3. SSL证书文件路径和权限正确
4. 服务器有足够的磁盘空间和内存资源

## 性能优化建议

1. 启用Nginx缓存
2. 配置适当的worker进程数
3. 设置合理的超时时间
4. 启用Gzip压缩
5. 配置静态资源缓存策略