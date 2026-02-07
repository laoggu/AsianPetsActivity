package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.RoleCreateRequest;
import org.example.asianpetssystem.dto.request.RoleUpdateRequest;
import org.example.asianpetssystem.dto.response.RolePermissionResponse;
import org.example.asianpetssystem.dto.response.RoleResponse;
import org.example.asianpetssystem.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/roles")
@Tag(name = "角色管理", description = "角色的增删改查和权限分配管理")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @GetMapping
    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getRoleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("开始获取角色列表 - page={}, size={}", page, size);
        long startTime = System.currentTimeMillis();

        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            PageResponse<RoleResponse> result = roleService.getRoleList(pageRequest);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取角色列表成功 - 返回 {} 条记录, 耗时: {}ms", result.getContent().size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取角色列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @Valid @RequestBody RoleCreateRequest request) {

        logger.info("开始创建角色 - name={}, code={}", request.getName(), request.getCode());
        long startTime = System.currentTimeMillis();

        try {
            RoleResponse result = roleService.createRole(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建角色成功 - ID={}, 耗时: {}ms", result.getId(), duration);
            return ResponseEntity.ok(ApiResponse.success(result, "角色创建成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建角色失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {

        logger.info("开始获取角色详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            // 通过getRoleList获取并转换，或者可以添加getRoleById方法
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(0);
            pageRequest.setSize(1);
            PageResponse<RoleResponse> pageResponse = roleService.getRoleList(pageRequest);
            RoleResponse result = pageResponse.getContent().stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取角色详情成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取角色详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新指定ID的角色信息")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {

        logger.info("开始更新角色 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            RoleResponse result = roleService.updateRole(id, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新角色成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result, "角色更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新角色失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除指定ID的角色")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {

        logger.info("开始删除角色 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            roleService.deleteRole(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除角色成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "角色删除成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除角色失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取角色权限", description = "获取指定角色的权限列表")
    public ResponseEntity<ApiResponse<RolePermissionResponse>> getRolePermissions(@PathVariable Long id) {

        logger.info("开始获取角色权限 - roleId={}", id);
        long startTime = System.currentTimeMillis();

        try {
            RolePermissionResponse result = roleService.getRolePermissions(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取角色权限成功 - roleId={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取角色权限失败 - roleId={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/permissions")
    @Operation(summary = "更新角色权限", description = "更新指定角色的权限")
    public ResponseEntity<ApiResponse<Void>> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> request) {

        List<Long> permissionIds = request.get("permissionIds");
        logger.info("开始更新角色权限 - roleId={}, permissionCount={}", id,
                permissionIds != null ? permissionIds.size() : 0);
        long startTime = System.currentTimeMillis();

        try {
            roleService.updateRolePermissions(id, permissionIds);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新角色权限成功 - roleId={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "角色权限更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新角色权限失败 - roleId={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }
}
