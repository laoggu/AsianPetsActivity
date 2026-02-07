package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.dto.response.*;
import org.example.asianpetssystem.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘统计接口", description = "数据统计和可视化相关接口")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取概览数据
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取仪表盘概览数据", description = "获取总会员数、待审核申请数、活动数等核心指标")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview() {
        log.info("收到获取仪表盘概览数据请求");
        long startTime = System.currentTimeMillis();

        try {
            DashboardOverviewResponse data = dashboardService.getOverview();
            long duration = System.currentTimeMillis() - startTime;
            log.info("获取仪表盘概览数据请求处理成功 - 耗时: {}ms", duration);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取仪表盘概览数据请求处理失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取会员统计数据
     */
    @GetMapping("/member-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取会员统计数据", description = "获取会员增长趋势、级别分布、状态分布等统计数据")
    public ResponseEntity<ApiResponse<MemberStatsResponse>> getMemberStats() {
        log.info("收到获取会员统计数据请求");
        long startTime = System.currentTimeMillis();

        try {
            MemberStatsResponse data = dashboardService.getMemberStats();
            long duration = System.currentTimeMillis() - startTime;
            log.info("获取会员统计数据请求处理成功 - 耗时: {}ms", duration);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取会员统计数据请求处理失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取活动统计数据
     */
    @GetMapping("/activity-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取活动统计数据", description = "获取活动总数、报名人数、签到人数、参与率等统计数据")
    public ResponseEntity<ApiResponse<ActivityStatsResponse>> getActivityStats() {
        log.info("收到获取活动统计数据请求");
        long startTime = System.currentTimeMillis();

        try {
            ActivityStatsResponse data = dashboardService.getActivityStats();
            long duration = System.currentTimeMillis() - startTime;
            log.info("获取活动统计数据请求处理成功 - 耗时: {}ms", duration);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取活动统计数据请求处理失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取地域分布数据
     */
    @GetMapping("/geographic")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取地域分布数据", description = "获取会员或活动的省份和城市分布数据")
    public ResponseEntity<ApiResponse<GeographicDistributionResponse>> getGeographicDistribution() {
        log.info("收到获取地域分布数据请求");
        long startTime = System.currentTimeMillis();

        try {
            GeographicDistributionResponse data = dashboardService.getGeographicDistribution();
            long duration = System.currentTimeMillis() - startTime;
            log.info("获取地域分布数据请求处理成功 - 耗时: {}ms", duration);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取地域分布数据请求处理失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取业务范畴分布数据
     */
    @GetMapping("/business-scope")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取业务范畴分布数据", description = "获取不同业务范畴的会员分布情况")
    public ResponseEntity<ApiResponse<BusinessScopeDistributionResponse>> getBusinessScopeDistribution() {
        log.info("收到获取业务范畴分布数据请求");
        long startTime = System.currentTimeMillis();

        try {
            BusinessScopeDistributionResponse data = dashboardService.getBusinessScopeDistribution();
            long duration = System.currentTimeMillis() - startTime;
            log.info("获取业务范畴分布数据请求处理成功 - 耗时: {}ms", duration);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取业务范畴分布数据请求处理失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }
}
