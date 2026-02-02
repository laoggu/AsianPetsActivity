package org.example.asianpetssystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    // 白名单路径 - 这些路径不需要JWT认证
    private static final List<String> WHITE_LIST_PATHS = Arrays.asList(
        "/api/auth/",              // 认证接口
        "/api/member/apply",       // 会员申请接口
        "/api/member/upload",      // 文件上传接口
        "/api/common/",            // 通用接口
        "/swagger-ui/",            // Swagger UI
        "/v3/api-docs/",           // API文档
        "/webjars/",               // WebJars静态资源
        "/swagger-resources/",     // Swagger资源
        "/actuator/",              // 健康检查等监控端点
        "/favicon.ico"             // 网站图标
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            
            // 检查是否在白名单路径中
            if (isWhitelistedPath(requestURI)) {
                logger.debug("跳过JWT认证 - URI在白名单中: {}, Method: {}", requestURI, method);
                filterChain.doFilter(request, response);
                return;
            }
            
            logger.debug("开始JWT认证过滤 - URI: {}, Method: {}", requestURI, method);
            
            String jwt = parseJwt(request);
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.info("JWT认证成功 - 用户名: {}, URI: {}, Method: {}", username, requestURI, method);
            } else {
                logger.debug("JWT令牌无效或不存在 - URI: {}, Method: {}", requestURI, method);
            }

            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("JWT认证过程中发生错误 - URI: {}, 错误: {}", request.getRequestURI(), e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
    
    /**
     * 检查请求路径是否在白名单中
     * @param requestURI 请求URI
     * @return 是否在白名单中
     */
    private boolean isWhitelistedPath(String requestURI) {
        // 精确匹配根路径
        if ("/".equals(requestURI)) {
            return true;
        }
        
        // 前缀匹配其他路径
        return WHITE_LIST_PATHS.stream()
            .anyMatch(path -> requestURI.startsWith(path));
    }
}
