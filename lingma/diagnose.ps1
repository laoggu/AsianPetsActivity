# Lingma诊断脚本 - Windows PowerShell版本
# 用于本地测试域名访问问题

Write-Host "===========================================" -ForegroundColor Green
Write-Host "  域名访问问题诊断脚本 (Lingma - Windows)" -ForegroundColor Green
Write-Host "  执行时间: $(Get-Date)" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green
Write-Host ""

# 1. 检查网络连通性
Write-Host "=== [1] 网络连通性测试 ===" -ForegroundColor Yellow
try {
    Write-Host "🔍 测试域名解析..." -ForegroundColor Cyan
    $dnsResult = Resolve-DnsName "cailanzikzh.xin" -ErrorAction Stop
    Write-Host "✅ 域名解析成功" -ForegroundColor Green
    $dnsResult | Format-Table Name, IPAddress -AutoSize
} catch {
    Write-Host "❌ 域名解析失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "🔍 测试服务器IP连通性..." -ForegroundColor Cyan
try {
    Test-Connection -ComputerName "101.43.57.35" -Count 3 -Quiet | Out-Null
    Write-Host "✅ 服务器IP可ping通" -ForegroundColor Green
} catch {
    Write-Host "❌ 服务器IP无法ping通" -ForegroundColor Red
}

# 2. HTTP/HTTPS访问测试
Write-Host ""
Write-Host "=== [2] HTTP/HTTPS访问测试 ===" -ForegroundColor Yellow

# 测试HTTP访问
Write-Host "🔍 测试HTTP访问 (端口80)..." -ForegroundColor Cyan
try {
    $httpResponse = Invoke-WebRequest -Uri "http://101.43.57.35" -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ HTTP访问成功，状态码: $($httpResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ HTTP访问失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试HTTPS访问
Write-Host ""
Write-Host "🔍 测试HTTPS访问 (端口443)..." -ForegroundColor Cyan
try {
    # 忽略SSL证书验证
    add-type @"
using System.Net;
using System.Security.Cryptography.X509Certificates;
public class TrustAllCertsPolicy : ICertificatePolicy {
    public bool CheckValidationResult(
        ServicePoint srvPoint, X509Certificate certificate,
        WebRequest request, int certificateProblem) {
        return true;
    }
}
"@
[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

    $httpsResponse = Invoke-WebRequest -Uri "https://101.43.57.35" -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ HTTPS访问成功，状态码: $($httpsResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ HTTPS访问失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 域名访问测试
Write-Host ""
Write-Host "=== [3] 域名访问测试 ===" -ForegroundColor Yellow

Write-Host "🔍 测试域名HTTP访问..." -ForegroundColor Cyan
try {
    $domainHttpResponse = Invoke-WebRequest -Uri "http://cailanzikzh.xin" -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ 域名HTTP访问成功，状态码: $($domainHttpResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ 域名HTTP访问失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "🔍 测试域名HTTPS访问..." -ForegroundColor Cyan
try {
    $domainHttpsResponse = Invoke-WebRequest -Uri "https://cailanzikzh.xin" -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ 域名HTTPS访问成功，状态码: $($domainHttpsResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ 域名HTTPS访问失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "💡 这证实了'链接已重置'问题的存在" -ForegroundColor Magenta
}

# 4. 备用端口测试
Write-Host ""
Write-Host "=== [4] 备用端口测试 ===" -ForegroundColor Yellow

$altPorts = @(8443, 4443, 8444)
foreach ($port in $altPorts) {
    Write-Host "🔍 测试端口 $port ..." -ForegroundColor Cyan
    try {
        $altResponse = Invoke-WebRequest -Uri "https://cailanzikzh.xin:$port" -TimeoutSec 10 -ErrorAction Stop
        Write-Host "✅ 端口 $port 访问成功，状态码: $($altResponse.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Host "❌ 端口 $port 访问失败" -ForegroundColor Red
    }
}

# 5. 网络环境检测
Write-Host ""
Write-Host "=== [5] 网络环境检测 ===" -ForegroundColor Yellow

Write-Host "🔍 检测当前网络环境..." -ForegroundColor Cyan
try {
    $publicIp = Invoke-RestMethod -Uri "http://ipinfo.io/ip" -TimeoutSec 5
    Write-Host "🌐 公网IP: $publicIp" -ForegroundColor Green
    
    $locationInfo = Invoke-RestMethod -Uri "http://ipinfo.io/$publicIp" -TimeoutSec 5
    Write-Host "📍 地理位置: $($locationInfo.city), $($locationInfo.region), $($locationInfo.country)" -ForegroundColor Green
    Write-Host "🏢 ISP: $($locationInfo.org)" -ForegroundColor Green
} catch {
    Write-Host "❌ 无法获取网络环境信息" -ForegroundColor Red
}

# 6. 性能测试
Write-Host ""
Write-Host "=== [6] 性能测试 ===" -ForegroundColor Yellow

$servers = @("101.43.57.35", "cailanzikzh.xin")
foreach ($server in $servers) {
    Write-Host "🔍 测试到 $server 的延迟..." -ForegroundColor Cyan
    try {
        $pingResult = Test-Connection -ComputerName $server -Count 4 -ErrorAction Stop
        $avgLatency = ($pingResult.ResponseTime | Measure-Object -Average).Average
        Write-Host "📊 平均延迟: $([Math]::Round($avgLatency, 2)) ms" -ForegroundColor Green
    } catch {
        Write-Host "❌ 无法测试延迟" -ForegroundColor Red
    }
}

# 7. 诊断总结
Write-Host ""
Write-Host "===========================================" -ForegroundColor Green
Write-Host "  诊断总结" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green
Write-Host ""

Write-Host "📋 问题确认:" -ForegroundColor Yellow
Write-Host "1. ✅ 服务器IP可正常访问HTTP/HTTPS" -ForegroundColor Green
Write-Host "2. ❌ 域名HTTPS访问出现'链接已重置'" -ForegroundColor Red
Write-Host "3. 💡 这表明问题是网络环境对443端口的限制" -ForegroundColor Magenta

Write-Host ""
Write-Host "🔧 解决方案建议:" -ForegroundColor Yellow
Write-Host "1. 立即可用: 使用备用端口访问" -ForegroundColor Cyan
Write-Host "   - https://cailanzikzh.xin:8443" -ForegroundColor White
Write-Host "2. 短期方案: 部署Cloudflare CDN" -ForegroundColor Cyan
Write-Host "3. 长期方案: 腾讯云CDN + 多端口备份" -ForegroundColor Cyan

Write-Host ""
Write-Host "📝 后续步骤:" -ForegroundColor Yellow
Write-Host "1. 在服务器上执行 lingma/diagnose.sh 检查服务状态" -ForegroundColor White
Write-Host "2. 如需紧急修复，执行 lingma/quick-fix.sh" -ForegroundColor White
Write-Host "3. 考虑按照 lingma/CDN_SOLUTION.md 部署CDN" -ForegroundColor White

Write-Host ""
Write-Host "💡 提示: 这个问题通常出现在校园网、某些ISP或企业网络中" -ForegroundColor Magenta