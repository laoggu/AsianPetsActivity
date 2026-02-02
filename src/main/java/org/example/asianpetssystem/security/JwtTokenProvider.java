// src/main/java/org/example\asianpetssystem\security\JwtTokenProvider.java
package org.example.asianpetssystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:xK9mN2pQ8vR4sL7wE1yU3iO6aH5jF0gB}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}") // 默认24小时
    private Long jwtExpiration;

    private Key getSignKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        logger.trace("开始提取JWT中的用户名");
        try {
            String username = extractClaim(token, Claims::getSubject);
            logger.debug("成功提取用户名: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("提取JWT用户名失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Date extractExpiration(String token) {
        logger.trace("开始提取JWT过期时间");
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            logger.debug("成功提取过期时间: {}", expiration);
            return expiration;
        } catch (Exception e) {
            logger.error("提取JWT过期时间失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        logger.trace("开始提取JWT声明");
        try {
            final Claims claims = extractAllClaims(token);
            T result = claimsResolver.apply(claims);
            logger.debug("成功提取声明: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("提取JWT声明失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        logger.trace("开始解析JWT所有声明");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("成功解析JWT声明");
            return claims;
        } catch (Exception e) {
            logger.error("解析JWT声明失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        logger.trace("开始检查JWT是否过期");
        try {
            Date expiration = extractExpiration(token);
            boolean expired = expiration.before(new Date());
            logger.debug("JWT过期检查结果: {}", expired ? "已过期" : "未过期");
            return expired;
        } catch (Exception e) {
            logger.error("检查JWT过期状态失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String generateToken(UserDetails userDetails) {
        logger.info("开始生成JWT令牌 - 用户名: {}", userDetails.getUsername());
        try {
            Map<String, Object> claims = new HashMap<>();
            String token = createToken(claims, userDetails.getUsername());
            logger.info("JWT令牌生成成功 - 用户名: {}", userDetails.getUsername());
            return token;
        } catch (Exception e) {
            logger.error("生成JWT令牌失败 - 用户名: {}, 错误: {}", userDetails.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.info("开始生成包含额外声明的JWT令牌 - 用户名: {}", userDetails.getUsername());
        try {
            String token = createToken(extraClaims, userDetails.getUsername());
            logger.info("包含额外声明的JWT令牌生成成功 - 用户名: {}", userDetails.getUsername());
            return token;
        } catch (Exception e) {
            logger.error("生成包含额外声明的JWT令牌失败 - 用户名: {}, 错误: {}", userDetails.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.trace("开始创建JWT令牌 - 主题: {}", subject);
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.debug("JWT令牌创建成功 - 主题: {}, 过期时间: {}秒", subject, jwtExpiration);
            return token;
        } catch (Exception e) {
            logger.error("创建JWT令牌失败 - 主题: {}, 错误: {}", subject, e.getMessage(), e);
            throw e;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        logger.trace("开始验证JWT令牌 - 用户名: {}", userDetails.getUsername());
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            logger.debug("JWT令牌验证结果: {} - 用户名: {}", isValid ? "有效" : "无效", userDetails.getUsername());
            return isValid;
        } catch (Exception e) {
            logger.error("验证JWT令牌失败 - 用户名: {}, 错误: {}", userDetails.getUsername(), e.getMessage(), e);
            return false;
        }
    }

    // 添加单参数的验证方法
    public Boolean validateToken(String token) {
        logger.trace("开始验证JWT令牌");
        try {
            extractAllClaims(token);
            boolean isValid = !isTokenExpired(token);
            logger.debug("JWT令牌验证结果: {}", isValid ? "有效" : "无效");
            return isValid;
        } catch (Exception e) {
            logger.warn("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    public String generateToken(String username) {
        logger.info("开始生成JWT令牌 - 用户名: {}", username);
        try {
            Map<String, Object> claims = new HashMap<>();
            String token = createToken(claims, username);
            logger.info("JWT令牌生成成功 - 用户名: {}", username);
            return token;
        } catch (Exception e) {
            logger.error("生成JWT令牌失败 - 用户名: {}, 错误: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    // 添加获取用户名的方法
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }
}
