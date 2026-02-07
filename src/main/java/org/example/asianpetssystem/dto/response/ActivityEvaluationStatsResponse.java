package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class ActivityEvaluationStatsResponse {
    private Long activityId;
    private String activityTitle;

    private Double averageOverallRating;
    private Double averageContentRating;
    private Double averageOrganizationRating;
    private Double averageSpeakerRating;
    private Double averageVenueRating;

    private Long totalEvaluations;
    private Map<Integer, Long> ratingDistribution;

    // 各维度评分占比
    private Map<String, Double> dimensionAverages;
}
