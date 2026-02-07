package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.dto.request.AuditConfigRequest;
import org.example.asianpetssystem.dto.request.SystemConfigBatchRequest;
import org.example.asianpetssystem.dto.request.SystemConfigUpdateRequest;
import org.example.asianpetssystem.dto.response.AuditConfigResponse;
import org.example.asianpetssystem.dto.response.SystemConfigDetailResponse;
import org.example.asianpetssystem.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/config")
@Tag(name = "系统配置管理", description = "系统配置和审核配置管理")
public class SystemConfigController {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigController.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/{key}")
    @Operation(summary = "获取系统配置", description = "根据配置键获取系统配置信息")
    public ResponseEntity<ApiResponse<SystemConfigDetailResponse>> getConfig(@PathVariable String key) {
        logger.info("开始获取系统配置 - key={}", key);
        long startTime = System.currentTimeMillis();

        try {
            SystemConfigDetailResponse result = systemConfigService.getConfig(key);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取系统配置成功 - key={}, 耗时: {}ms", key, duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取系统配置失败 - key={}, 耗时: {}ms, 错误: {}", key, duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/prefix/{prefix}")
    @Operation(summary = "根据前缀获取配置列表", description = "根据配置键前缀批量获取配置列表")
    public ResponseEntity<ApiResponse<List<SystemConfigDetailResponse>>> getConfigsByPrefix(@PathVariable String prefix) {
        logger.info("开始根据前缀获取配置列表 - prefix={}", prefix);
        long startTime = System.currentTimeMillis();

        try {
            List<SystemConfigDetailResponse> result = systemConfigService.getConfigsByPrefix(prefix);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("根据前缀获取配置列表成功 - prefix={}, 返回 {} 条记录, 耗时: {}ms", prefix, result.size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("根据前缀获取配置列表失败 - prefix={}, 耗时: {}ms, 错误: {}", prefix, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping
    @Operation(summary = "更新系统配置", description = "更新单个系统配置项")
    public ResponseEntity<ApiResponse<SystemConfigDetailResponse>> updateConfig(
            @Valid @RequestBody SystemConfigUpdateRequest request) {
        logger.info("开始更新系统配置 - key={}", request.getConfigKey());
        long startTime = System.currentTimeMillis();

        try {
            SystemConfigDetailResponse result = systemConfigService.updateConfig(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新系统配置成功 - key={}, 耗时: {}ms", request.getConfigKey(), duration);
            return ResponseEntity.ok(ApiResponse.success(result, "配置更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新系统配置失败 - key={}, 耗时: {}ms, 错误: {}", request.getConfigKey(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/batch")
    @Operation(summary = "批量更新配置", description = "批量更新多个系统配置项")
    public ResponseEntity<ApiResponse<Void>> updateConfigsBatch(
            @Valid @RequestBody SystemConfigBatchRequest request) {
        logger.info("开始批量更新配置 - 数量: {}", request.getConfigs().size());
        long startTime = System.currentTimeMillis();

        try {
            List<SystemConfigUpdateRequest> configs = request.getConfigs().stream()
                    .map(item -> {
                        SystemConfigUpdateRequest config = new SystemConfigUpdateRequest();
                        config.setConfigKey(item.getConfigKey());
                        config.setConfigValue(item.getConfigValue());
                        config.setDescription(item.getDescription());
                        return config;
                    })
                    .toList();

            systemConfigService.updateConfigsBatch(configs);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("批量更新配置成功 - 数量: {}, 耗时: {}ms", request.getConfigs().size(), duration);
            return ResponseEntity.ok(ApiResponse.success(null, "批量更新配置成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("批量更新配置失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/audit")
    @Operation(summary = "获取审核配置", description = "获取当前审核配置信息")
    public ResponseEntity<ApiResponse<AuditConfigResponse>> getAuditConfig() {
        logger.info("开始获取审核配置");
        long startTime = System.currentTimeMillis();

        try {
            AuditConfigResponse result = systemConfigService.getAuditConfig();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取审核配置成功 - 耗时: {}ms", duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取审核配置失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/audit")
    @Operation(summary = "更新审核配置", description = "更新审核配置信息")
    public ResponseEntity<ApiResponse<AuditConfigResponse>> updateAuditConfig(
            @Valid @RequestBody AuditConfigRequest request) {
        logger.info("开始更新审核配置 - autoAudit={}", request.getAutoAudit());
        long startTime = System.currentTimeMillis();

        try {
            AuditConfigResponse result = systemConfigService.updateAuditConfig(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新审核配置成功 - ID={}, 耗时: {}ms", result.getId(), duration);
            return ResponseEntity.ok(ApiResponse.success(result, "审核配置更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新审核配置失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }
}
