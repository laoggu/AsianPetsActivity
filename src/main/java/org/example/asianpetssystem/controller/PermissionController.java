package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.dto.response.PermissionResponse;
import org.example.asianpetssystem.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@Tag(name = "权限管理", description = "权限列表查询")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private RoleService roleService;

    @GetMapping
    @Operation(summary = "获取权限列表", description = "获取所有权限列表，按模块分组排序")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionList() {

        logger.info("开始获取权限列表");
        long startTime = System.currentTimeMillis();

        try {
            List<PermissionResponse> result = roleService.getPermissionList();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取权限列表成功 - 返回 {} 条记录, 耗时: {}ms", result.size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取权限列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }
}
