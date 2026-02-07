package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.AdminUserCreateRequest;
import org.example.asianpetssystem.dto.request.AdminUserStatusRequest;
import org.example.asianpetssystem.dto.request.AdminUserUpdateRequest;
import org.example.asianpetssystem.dto.request.ResetPasswordRequest;
import org.example.asianpetssystem.dto.response.AdminUserResponse;
import org.example.asianpetssystem.service.AdminUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "后台用户管理", description = "后台用户的增删改查、状态管理和密码重置")
public class AdminUserManageController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserManageController.class);

    @Autowired
    private AdminUserManageService adminUserManageService;

    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持按用户名和状态筛选")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("开始获取用户列表 - username={}, status={}, page={}, size={}", username, status, page, size);
        long startTime = System.currentTimeMillis();

        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            PageResponse<AdminUserResponse> result = adminUserManageService.getUserList(username, status, pageRequest);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取用户列表成功 - 返回 {} 条记录, 耗时: {}ms", result.getContent().size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取用户列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(
            @Valid @RequestBody AdminUserCreateRequest request) {

        logger.info("开始创建用户 - username={}", request.getUsername());
        long startTime = System.currentTimeMillis();

        try {
            AdminUserResponse result = adminUserManageService.createUser(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建用户成功 - ID={}, 耗时: {}ms", result.getId(), duration);
            return ResponseEntity.ok(ApiResponse.success(result, "用户创建成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建用户失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(@PathVariable Long id) {

        logger.info("开始获取用户详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AdminUserResponse result = adminUserManageService.getUserById(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取用户详情成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取用户详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新指定ID的用户信息")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateRequest request) {

        logger.info("开始更新用户 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AdminUserResponse result = adminUserManageService.updateUser(id, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新用户成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result, "用户更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新用户失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定ID的用户")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

        logger.info("开始删除用户 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            adminUserManageService.deleteUser(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除用户成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "用户删除成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除用户失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "修改用户状态", description = "修改指定用户的状态（ACTIVE/INACTIVE）")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserStatusRequest request) {

        logger.info("开始更新用户状态 - ID={}, status={}", id, request.getStatus());
        long startTime = System.currentTimeMillis();

        try {
            adminUserManageService.updateUserStatus(id, request.getStatus());
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新用户状态成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "用户状态更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新用户状态失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "重置密码", description = "重置指定用户的密码")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request) {

        logger.info("开始重置密码 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            adminUserManageService.resetPassword(id, request.getNewPassword());
            long duration = System.currentTimeMillis() - startTime;
            logger.info("重置密码成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "密码重置成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("重置密码失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }
}
