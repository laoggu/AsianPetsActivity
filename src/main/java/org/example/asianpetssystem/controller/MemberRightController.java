package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.dto.request.MemberRightCreateRequest;
import org.example.asianpetssystem.dto.request.MemberRightUpdateRequest;
import org.example.asianpetssystem.dto.response.LevelRightsResponse;
import org.example.asianpetssystem.dto.response.MemberRightResponse;
import org.example.asianpetssystem.service.MemberRightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rights")
@Tag(name = "权益管理", description = "会员权益的增删改查和等级配置")
public class MemberRightController {

    private static final Logger logger = LoggerFactory.getLogger(MemberRightController.class);

    @Autowired
    private MemberRightService memberRightService;

    @GetMapping
    @Operation(summary = "获取权益列表", description = "按等级分组获取所有启用的权益列表")
    public ResponseEntity<ApiResponse<List<LevelRightsResponse>>> getRightsByLevel() {
        logger.info("开始获取权益列表（按等级分组）");
        long startTime = System.currentTimeMillis();

        try {
            List<LevelRightsResponse> result = memberRightService.getRightsByLevel();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取权益列表成功 - 返回 {} 个等级, 耗时: {}ms", result.size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取权益列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{level}")
    @Operation(summary = "获取指定等级的权益", description = "获取指定会员等级的所有启用权益")
    public ResponseEntity<ApiResponse<List<MemberRightResponse>>> getRightsByLevel(
            @PathVariable String level) {
        logger.info("开始获取指定等级的权益 - level={}", level);
        long startTime = System.currentTimeMillis();

        try {
            List<MemberRightResponse> result = memberRightService.getRightsByLevel(level);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取指定等级权益成功 - level={}, 返回 {} 条记录, 耗时: {}ms", level, result.size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取指定等级权益失败 - level={}, 耗时: {}ms, 错误: {}", level, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "添加权益", description = "创建新的会员权益")
    public ResponseEntity<ApiResponse<MemberRightResponse>> createRight(
            @Valid @RequestBody MemberRightCreateRequest request) {
        logger.info("开始添加权益 - title={}, level={}", request.getTitle(), request.getLevel());
        long startTime = System.currentTimeMillis();

        try {
            MemberRightResponse result = memberRightService.createRight(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("添加权益成功 - ID={}, 耗时: {}ms", result.getId(), duration);
            return ResponseEntity.ok(ApiResponse.success(result, "权益添加成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("添加权益失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权益", description = "更新指定ID的权益信息")
    public ResponseEntity<ApiResponse<MemberRightResponse>> updateRight(
            @PathVariable Long id,
            @Valid @RequestBody MemberRightUpdateRequest request) {
        logger.info("开始更新权益 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            MemberRightResponse result = memberRightService.updateRight(id, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新权益成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result, "权益更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新权益失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权益", description = "删除指定ID的权益")
    public ResponseEntity<ApiResponse<Void>> deleteRight(@PathVariable Long id) {
        logger.info("开始删除权益 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            memberRightService.deleteRight(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除权益成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "权益删除成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除权益失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/level/{level}")
    @Operation(summary = "更新等级权益", description = "更新指定等级的权益配置（传入权益ID列表）")
    public ResponseEntity<ApiResponse<Void>> updateLevelRights(
            @PathVariable String level,
            @RequestBody List<Long> rightIds) {
        logger.info("开始更新等级权益配置 - level={}, rightIds={}", level, rightIds);
        long startTime = System.currentTimeMillis();

        try {
            memberRightService.updateLevelRights(level, rightIds);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新等级权益配置成功 - level={}, 耗时: {}ms", level, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "等级权益配置更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新等级权益配置失败 - level={}, 耗时: {}ms, 错误: {}", level, duration, e.getMessage(), e);
            throw e;
        }
    }
}
