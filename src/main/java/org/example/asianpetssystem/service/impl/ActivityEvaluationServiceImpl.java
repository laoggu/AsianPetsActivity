package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.ActivityStatus;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.common.enums.SignupStatus;
import org.example.asianpetssystem.dto.request.ActivityEvaluationRequest;
import org.example.asianpetssystem.dto.response.ActivityEvaluationResponse;
import org.example.asianpetssystem.dto.response.ActivityEvaluationStatsResponse;
import org.example.asianpetssystem.entity.Activity;
import org.example.asianpetssystem.entity.ActivityEvaluation;
import org.example.asianpetssystem.entity.ActivitySignup;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.ActivityEvaluationRepository;
import org.example.asianpetssystem.repository.ActivityRepository;
import org.example.asianpetssystem.repository.ActivitySignupRepository;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.service.ActivityEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityEvaluationServiceImpl implements ActivityEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityEvaluationServiceImpl.class);

    @Autowired
    private ActivityEvaluationRepository evaluationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivitySignupRepository signupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public ActivityEvaluationResponse submitEvaluation(Long memberId, ActivityEvaluationRequest request) {
        logger.info("提交活动评价 - activityId={}, memberId={}", request.getActivityId(), memberId);

        // 检查活动是否存在
        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ACTIVITY_NOT_FOUND));

        // 检查活动是否已结束
        if (activity.getStatus() != ActivityStatus.ENDED && 
            activity.getEndTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorEnum.ACTIVITY_NOT_ENDED);
        }

        // 检查是否已评价
        if (evaluationRepository.existsByActivityIdAndMemberId(request.getActivityId(), memberId)) {
            throw new BusinessException(BusinessErrorEnum.ALREADY_EVALUATED);
        }

        // 检查报名记录
        Optional<ActivitySignup> signupOpt = signupRepository
                .findByActivityIdAndMemberId(request.getActivityId(), memberId);
        if (signupOpt.isEmpty() || signupOpt.get().getStatus() != SignupStatus.APPROVED) {
            throw new BusinessException("您未报名或报名未通过审核，无法评价");
        }

        ActivityEvaluation evaluation = new ActivityEvaluation();
        evaluation.setActivityId(request.getActivityId());
        evaluation.setMemberId(memberId);
        evaluation.setSignupId(request.getSignupId());
        evaluation.setOverallRating(request.getOverallRating());
        evaluation.setContentRating(request.getContentRating());
        evaluation.setOrganizationRating(request.getOrganizationRating());
        evaluation.setSpeakerRating(request.getSpeakerRating());
        evaluation.setVenueRating(request.getVenueRating());
        evaluation.setComment(request.getComment());
        evaluation.setIsAnonymous(request.getIsAnonymous());
        evaluation.setHasSuggestion(request.getHasSuggestion());
        evaluation.setSuggestion(request.getSuggestion());

        evaluationRepository.save(evaluation);

        logger.info("活动评价提交成功 - evaluationId={}", evaluation.getId());
        return convertToResponse(evaluation, activity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityEvaluationResponse> getActivityEvaluations(Long activityId, PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(), pageRequest.getSize());
        
        Page<ActivityEvaluation> page = evaluationRepository.findByActivityId(activityId, pageable);
        Activity activity = activityRepository.findById(activityId).orElse(null);

        List<ActivityEvaluationResponse> content = page.getContent().stream()
                .map(e -> convertToResponse(e, activity))
                .collect(Collectors.toList());

        PageResponse<ActivityEvaluationResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityEvaluationResponse getEvaluationById(Long id) {
        ActivityEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.EVALUATION_NOT_FOUND));
        Activity activity = activityRepository.findById(evaluation.getActivityId()).orElse(null);
        return convertToResponse(evaluation, activity);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityEvaluationResponse getMemberEvaluation(Long activityId, Long memberId) {
        ActivityEvaluation evaluation = evaluationRepository
                .findByActivityIdAndMemberId(activityId, memberId)
                .orElseThrow(() -> new BusinessException("评价记录不存在"));
        Activity activity = activityRepository.findById(activityId).orElse(null);
        return convertToResponse(evaluation, activity);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityEvaluationStatsResponse getEvaluationStats(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ACTIVITY_NOT_FOUND));

        ActivityEvaluationStatsResponse stats = new ActivityEvaluationStatsResponse();
        stats.setActivityId(activityId);
        stats.setActivityTitle(activity.getTitle());

        // 计算平均评分
        Double avgOverall = evaluationRepository.calculateAverageRating(activityId);
        stats.setAverageOverallRating(avgOverall != null ? avgOverall : 0.0);
        stats.setTotalEvaluations(evaluationRepository.countByActivityId(activityId));

        // 获取评分分布
        List<Object[]> distribution = evaluationRepository.getRatingDistribution(activityId);
        Map<Integer, Long> ratingDist = new HashMap<>();
        for (Object[] obj : distribution) {
            ratingDist.put((Integer) obj[0], (Long) obj[1]);
        }
        stats.setRatingDistribution(ratingDist);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEvaluate(Long activityId, Long memberId) {
        // 检查活动是否已结束
        Optional<Activity> activityOpt = activityRepository.findById(activityId);
        if (activityOpt.isEmpty()) return false;
        
        Activity activity = activityOpt.get();
        if (activity.getStatus() != ActivityStatus.ENDED && 
            activity.getEndTime().isAfter(LocalDateTime.now())) {
            return false;
        }

        // 检查是否已评价
        if (evaluationRepository.existsByActivityIdAndMemberId(activityId, memberId)) {
            return false;
        }

        // 检查是否报名且通过审核
        Optional<ActivitySignup> signupOpt = signupRepository
                .findByActivityIdAndMemberId(activityId, memberId);
        return signupOpt.isPresent() && signupOpt.get().getStatus() == SignupStatus.APPROVED;
    }

    private ActivityEvaluationResponse convertToResponse(ActivityEvaluation evaluation, Activity activity) {
        ActivityEvaluationResponse response = new ActivityEvaluationResponse();
        response.setId(evaluation.getId());
        response.setActivityId(evaluation.getActivityId());
        response.setActivityTitle(activity != null ? activity.getTitle() : "");
        response.setMemberId(evaluation.getMemberId());
        
        // 匿名评价不显示会员名称
        if (!Boolean.TRUE.equals(evaluation.getIsAnonymous())) {
            Member member = memberRepository.findById(evaluation.getMemberId()).orElse(null);
            response.setMemberName(member != null ? member.getCompanyName() : "");
        } else {
            response.setMemberName("匿名用户");
        }
        
        response.setSignupId(evaluation.getSignupId());
        response.setOverallRating(evaluation.getOverallRating());
        response.setContentRating(evaluation.getContentRating());
        response.setOrganizationRating(evaluation.getOrganizationRating());
        response.setSpeakerRating(evaluation.getSpeakerRating());
        response.setVenueRating(evaluation.getVenueRating());
        response.setComment(evaluation.getComment());
        response.setIsAnonymous(evaluation.getIsAnonymous());
        response.setHasSuggestion(evaluation.getHasSuggestion());
        response.setSuggestion(evaluation.getSuggestion());
        response.setCreatedAt(evaluation.getCreatedAt());
        return response;
    }
}
