#!/bin/bash

# JWT密钥生成和配置工具

echo "🔐 JWT密钥生成工具"
echo "=================="

# 生成安全的JWT密钥
generate_jwt_secret() {
    if command -v openssl &> /dev/null; then
        # 使用OpenSSL生成
        JWT_SECRET=$(openssl rand -base64 48)
        echo "✅ 使用OpenSSL生成JWT密钥"
    elif command -v python3 &> /dev/null; then
        # 使用Python生成
        JWT_SECRET=$(python3 -c "import secrets; print(secrets.token_urlsafe(32))")
        echo "✅ 使用Python生成JWT密钥"
    else
        # 使用系统随机数
        JWT_SECRET=$(tr -dc 'A-Za-z0-9' < /dev/urandom | head -c 48)
        echo "✅ 使用系统随机数生成JWT密钥"
    fi
    
    echo "🔑 生成的JWT密钥: $JWT_SECRET"
    echo "📋 密钥长度: ${#JWT_SECRET} 字符"
}

# 验证JWT密钥安全性
validate_jwt_secret() {
    local secret=$1
    local length=${#secret}
    
    echo "🔍 验证JWT密钥安全性..."
    
    if [ $length -lt 32 ]; then
        echo "❌ 警告: 密钥长度不足32字符，安全性较低"
        return 1
    elif [ $length -lt 48 ]; then
        echo "⚠️  注意: 建议使用48字符以上的密钥以获得更好的安全性"
        return 0
    else
        echo "✅ 密钥长度符合安全要求"
        return 0
    fi
}

# 更新.env文件
update_env_file() {
    local secret=$1
    local env_file=".env"
    
    if [ ! -f "$env_file" ]; then
        echo "❌ .env文件不存在"
        return 1
    fi
    
    echo "📝 更新.env文件..."
    
    # 备份原文件
    cp "$env_file" "${env_file}.backup.$(date +%Y%m%d_%H%M%S)"
    
    # 更新JWT_SECRET行
    if grep -q "^JWT_SECRET=" "$env_file"; then
        sed -i "s|^JWT_SECRET=.*|JWT_SECRET=$secret|" "$env_file"
    else
        echo "JWT_SECRET=$secret" >> "$env_file"
    fi
    
    echo "✅ .env文件更新完成"
    echo "💾 原文件已备份为: ${env_file}.backup.*"
}

# 主程序
main() {
    echo "请选择操作:"
    echo "1) 生成新的JWT密钥"
    echo "2) 验证现有JWT密钥"
    echo "3) 更新.env文件中的JWT密钥"
    echo "4) 执行完整流程（生成+验证+更新）"
    
    read -p "请输入选项 (1-4): " choice
    
    case $choice in
        1)
            generate_jwt_secret
            echo "📋 请将此密钥配置到您的.env文件中"
            ;;
        2)
            read -p "请输入要验证的JWT密钥: " user_secret
            validate_jwt_secret "$user_secret"
            ;;
        3)
            read -p "请输入新的JWT密钥: " new_secret
            if validate_jwt_secret "$new_secret"; then
                update_env_file "$new_secret"
            else
                echo "❌ 密钥不符合安全要求"
            fi
            ;;
        4)
            generate_jwt_secret
            if validate_jwt_secret "$JWT_SECRET"; then
                update_env_file "$JWT_SECRET"
                echo "🎉 完整流程执行完成！"
            fi
            ;;
        *)
            echo "❌ 无效选项"
            ;;
    esac
}

# 运行主程序
main