package org.example.asianpetssystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivityEvaluationRequest {

    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @NotNull(message = "报名ID不能为空")
    private Long signupId;

    @NotNull(message = "总体评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer overallRating;

    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer contentRating;

    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer organizationRating;

    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer speakerRating;

    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer venueRating;

    private String comment;

    private Boolean isAnonymous = false;

    private Boolean hasSuggestion = false;

    private String suggestion;
}
