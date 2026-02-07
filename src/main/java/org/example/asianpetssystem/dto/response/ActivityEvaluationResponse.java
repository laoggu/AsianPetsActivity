package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityEvaluationResponse {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long memberId;
    private String memberName;
    private Long signupId;

    private Integer overallRating;
    private Integer contentRating;
    private Integer organizationRating;
    private Integer speakerRating;
    private Integer venueRating;

    private String comment;
    private Boolean isAnonymous;
    private Boolean hasSuggestion;
    private String suggestion;

    private LocalDateTime createdAt;

    // 统计信息
    private Double averageRating;
    private Long totalEvaluations;
    private Map<Integer, Long> ratingDistribution;
}
