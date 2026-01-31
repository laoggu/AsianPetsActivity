package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.dto.request.LoginRequest;
import org.example.asianpetssystem.dto.request.RegisterRequest;
import org.example.asianpetssystem.dto.response.LoginResponse;
import org.example.asianpetssystem.entity.AdminUser;
import org.example.asianpetssystem.repository.AdminUserRepository;
import org.example.asianpetssystem.security.JwtTokenProvider;
import org.example.asianpetssystem.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(String username, String password) {
        logger.info("开始用户登录认证 - 用户名: {}", username);
        long startTime = System.currentTimeMillis();
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 修复：使用正确的用户名生成token
            String jwt = jwtTokenProvider.generateToken(username);
            AdminUser user = (AdminUser) authentication.getPrincipal();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("用户登录认证成功 - 用户名: {}, 角色: {}, 耗时: {}ms", 
                       username, user.getRole(), duration);
            
            return new LoginResponse(jwt, null, user.getRole(), user.getId());
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("用户登录认证失败 - 用户名: {}, 耗时: {}ms, 错误: {}", 
                        username, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void register(RegisterRequest request) {
        logger.info("开始用户注册 - 用户名: {}, 角色: {}", request.getUsername(), request.getRole());
        long startTime = System.currentTimeMillis();
        
        try {
            if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
                logger.warn("用户注册失败 - 用户名已存在: {}", request.getUsername());
                throw new RuntimeException("用户名已存在");
            }

            AdminUser user = new AdminUser();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setCreatedAt(LocalDateTime.now());
            user.setStatus(1); // 启用状态

            adminUserRepository.save(user);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("用户注册成功 - 用户名: {}, 角色: {}, 耗时: {}ms", 
                       request.getUsername(), request.getRole(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("用户注册失败 - 用户名: {}, 角色: {}, 耗时: {}ms, 错误: {}", 
                        request.getUsername(), request.getRole(), duration, e.getMessage(), e);
            throw e;
        }
    }
}
