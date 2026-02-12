# CDN部署解决方案

## 问题背景
域名 `cailanzikzh.xin` 在某些网络环境下出现"链接已重置"问题，这是由于网络中间设备对HTTPS 443端口的干扰造成的。

## CDN解决方案优势
1. **绕过网络限制**: CDN节点分布在全球，避免单一网络环境的限制
2. **提升访问速度**: 用户就近访问CDN节点，响应更快
3. **隐藏真实服务器**: 不直接暴露服务器IP和端口
4. **增强安全性**: CDN提供DDoS防护等安全功能

## 腾讯云CDN部署步骤

### 1. 准备工作
```bash
# 确保服务器基础配置正确
sudo apt update
sudo apt install curl wget -y
```

### 2. 腾讯云CDN配置
1. 登录腾讯云控制台
2. 进入CDN产品页面
3. 添加域名加速
4. 配置源站信息：
   - 源站类型：IP
   - 源站地址：101.43.57.35
   - 源站端口：80（或443）

### 3. 本地Nginx调整
创建专门用于CDN的配置：

```nginx
# nginx/conf/nginx-cdn.conf
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    # 基础配置
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    
    # 日志格式
    log_format cdn '$remote_addr - $remote_user [$time_local] "$request" '
                   '$status $body_bytes_sent "$http_referer" '
                   '"$http_user_agent" "$http_x_forwarded_for" '
                   '"$http_x_real_ip" "$http_cdn_src_ip"';
    
    access_log /var/log/nginx/cdn-access.log cdn;
    error_log /var/log/nginx/cdn-error.log warn;
    
    upstream app_server {
        server app:8081 max_fails=3 fail_timeout=30s;
    }

    # CDN专用HTTP服务器
    server {
        listen 80;
        server_name cailanzikzh.xin www.cailanzikzh.xin;
        
        # 获取真实客户端IP
        set_real_ip_from 0.0.0.0/0;
        real_ip_header X-Forwarded-For;
        real_ip_recursive on;
        
        # 健康检查
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
        
        # API请求代理
        location / {
            proxy_pass http://app_server;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # CDN优化设置
            proxy_buffering on;
            proxy_buffer_size 128k;
            proxy_buffers 4 256k;
            proxy_busy_buffers_size 256k;
            
            proxy_connect_timeout 30s;
            proxy_send_timeout 30s;
            proxy_read_timeout 60s;
        }
    }
}
```

### 4. Docker配置调整
```yaml
# docker-compose-cdn.yml
version: '3.8'

services:
  nginx-cdn:
    image: nginx:alpine
    ports:
      - "80:80"  # CDN只需要HTTP端口
    volumes:
      - ./nginx/conf/nginx-cdn.conf:/etc/nginx/nginx.conf
      - ./nginx/logs:/var/log/nginx
    depends_on:
      - app
    networks:
      - asianpets-network
    restart: unless-stopped

  app:
    # 应用配置保持不变
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8081:8081"
    env_file:
      - .env
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.10:3306/asian_pets_system
      - SPRING_DATASOURCE_USERNAME=AsainPetsAdmin
      - SPRING_DATASOURCE_PASSWORD=Yk731207
    networks:
      - asianpets-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

networks:
  asianpets-network:
    driver: bridge
```

## Cloudflare免费方案

### 1. 注册Cloudflare
1. 访问 cloudflare.com
2. 注册免费账户
3. 添加站点 `cailanzikzh.xin`

### 2. DNS配置
将域名DNS服务器指向Cloudflare：
```
annie.ns.cloudflare.com
clayton.ns.cloudflare.com
```

### 3. SSL/TLS设置
1. 在Cloudflare控制台设置SSL/TLS为"Full"模式
2. 启用Always Use HTTPS
3. 配置页面规则优化性能

### 4. 本地配置调整
```nginx
# nginx/conf/nginx-cloudflare.conf
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    
    # Cloudflare真实IP模块
    set_real_ip_from 103.21.244.0/22;
    set_real_ip_from 103.22.200.0/22;
    set_real_ip_from 103.31.4.0/22;
    set_real_ip_from 104.16.0.0/12;
    set_real_ip_from 108.162.192.0/18;
    set_real_ip_from 131.0.72.0/22;
    set_real_ip_from 141.101.64.0/18;
    set_real_ip_from 162.158.0.0/15;
    set_real_ip_from 172.64.0.0/13;
    set_real_ip_from 173.245.48.0/20;
    set_real_ip_from 188.114.96.0/20;
    set_real_ip_from 190.93.240.0/20;
    set_real_ip_from 197.234.240.0/22;
    set_real_ip_from 198.41.128.0/17;
    
    real_ip_header CF-Connecting-IP;
    real_ip_recursive on;
    
    # 其余配置与标准配置相同...
}
```

## 部署命令

### 腾讯云CDN方案
```bash
# 1. 备份当前配置
cp docker-compose.prod.yml docker-compose.prod.backup.yml

# 2. 部署CDN版本
docker-compose -f docker-compose-cdn.yml up -d

# 3. 验证服务
curl -I http://localhost/health
```

### Cloudflare方案
```bash
# 1. 更新DNS记录指向Cloudflare
# 2. 部署优化后的Nginx配置
docker-compose -f docker-compose-cloudflare.yml up -d

# 3. 在Cloudflare控制台验证配置
```

## 成本对比

| 方案 | 月费用 | 优势 | 劣势 |
|------|--------|------|------|
| 腾讯云CDN | ¥50-200 | 国内访问快，支持中文 | 国际访问一般 |
| Cloudflare | 免费 | 全球节点，功能丰富 | 国内有时不稳定 |
| 自建多端口 | ¥0 | 完全免费，可控性强 | 仍有网络限制风险 |

## 推荐方案

**短期**: 使用备用端口 + Cloudflare免费方案
**长期**: 腾讯云CDN + 多端口备份

这样既能快速解决问题，又能保证服务的稳定性和可扩展性。