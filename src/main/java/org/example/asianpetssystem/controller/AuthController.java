package org.example.asianpetssystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.dto.request.LoginRequest;
import org.example.asianpetssystem.dto.request.RegisterRequest;
import org.example.asianpetssystem.dto.response.LoginResponse;
import org.example.asianpetssystem.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、注册等相关接口")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取JWT令牌")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("开始处理用户登录请求 - 用户名: {}", request.getUsername());
        long startTime = System.currentTimeMillis();
        
        try {
            LoginResponse response = authService.login(request.getUsername(), request.getPassword());
            long duration = System.currentTimeMillis() - startTime;
            logger.info("用户登录成功 - 用户名: {}, 耗时: {}ms", request.getUsername(), duration);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("用户登录失败 - 用户名: {}, 耗时: {}ms, 错误: {}", request.getUsername(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "管理员用户注册")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("开始处理用户注册请求 - 用户名: {}", request.getUsername());
        long startTime = System.currentTimeMillis();
        
        try {
            authService.register(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("用户注册成功 - 用户名: {}, 角色: {}, 耗时: {}ms", request.getUsername(), request.getRole(), duration);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("用户注册失败 - 用户名: {}, 耗时: {}ms, 错误: {}", request.getUsername(), duration, e.getMessage(), e);
            throw e;
        }
    }
}
