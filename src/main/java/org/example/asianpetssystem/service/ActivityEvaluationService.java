package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.ActivityEvaluationRequest;
import org.example.asianpetssystem.dto.response.ActivityEvaluationResponse;
import org.example.asianpetssystem.dto.response.ActivityEvaluationStatsResponse;

public interface ActivityEvaluationService {

    /**
     * 提交活动评价
     */
    ActivityEvaluationResponse submitEvaluation(Long memberId, ActivityEvaluationRequest request);

    /**
     * 获取活动的评价列表
     */
    PageResponse<ActivityEvaluationResponse> getActivityEvaluations(Long activityId, PageRequest pageRequest);

    /**
     * 获取评价详情
     */
    ActivityEvaluationResponse getEvaluationById(Long id);

    /**
     * 获取会员对活动的评价
     */
    ActivityEvaluationResponse getMemberEvaluation(Long activityId, Long memberId);

    /**
     * 获取活动评价统计
     */
    ActivityEvaluationStatsResponse getEvaluationStats(Long activityId);

    /**
     * 检查会员是否可以评价活动
     */
    boolean canEvaluate(Long activityId, Long memberId);
}
