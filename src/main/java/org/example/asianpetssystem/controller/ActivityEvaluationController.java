package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.ActivityEvaluationRequest;
import org.example.asianpetssystem.dto.response.ActivityEvaluationResponse;
import org.example.asianpetssystem.dto.response.ActivityEvaluationStatsResponse;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.ActivityEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "活动评价", description = "活动评价相关接口")
public class ActivityEvaluationController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityEvaluationController.class);

    @Autowired
    private ActivityEvaluationService evaluationService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    /**
     * 用户端：提交活动评价
     */
    @PostMapping("/evaluations")
    @Operation(summary = "提交活动评价", description = "用户对参加过的活动进行评价")
    public ApiResponse<ActivityEvaluationResponse> submitEvaluation(
            @RequestBody ActivityEvaluationRequest request) {
        Long memberId = authenticationFacade.getCurrentUserId();
        if (memberId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(evaluationService.submitEvaluation(memberId, request));
    }

    /**
     * 用户端：获取活动评价列表
     */
    @GetMapping("/activities/{activityId}/evaluations")
    @Operation(summary = "获取活动评价列表", description = "获取指定活动的所有评价")
    public ApiResponse<PageResponse<ActivityEvaluationResponse>> getActivityEvaluations(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        return ApiResponse.success(evaluationService.getActivityEvaluations(activityId, pageRequest));
    }

    /**
     * 用户端：获取我的评价
     */
    @GetMapping("/activities/{activityId}/my-evaluation")
    @Operation(summary = "获取我的评价", description = "获取当前用户对活动的评价")
    public ApiResponse<ActivityEvaluationResponse> getMyEvaluation(@PathVariable Long activityId) {
        Long memberId = authenticationFacade.getCurrentUserId();
        if (memberId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(evaluationService.getMemberEvaluation(activityId, memberId));
    }

    /**
     * 用户端：检查是否可以评价
     */
    @GetMapping("/activities/{activityId}/can-evaluate")
    @Operation(summary = "检查是否可以评价", description = "检查当前用户是否可以评价该活动")
    public ApiResponse<Boolean> canEvaluate(@PathVariable Long activityId) {
        Long memberId = authenticationFacade.getCurrentUserId();
        if (memberId == null) {
            return ApiResponse.success(false);
        }
        return ApiResponse.success(evaluationService.canEvaluate(activityId, memberId));
    }

    /**
     * 管理端：获取评价统计
     */
    @GetMapping("/admin/activities/{activityId}/evaluation-stats")
    @Operation(summary = "获取评价统计", description = "获取活动的评价统计数据")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ActivityEvaluationStatsResponse> getEvaluationStats(@PathVariable Long activityId) {
        return ApiResponse.success(evaluationService.getEvaluationStats(activityId));
    }
}
