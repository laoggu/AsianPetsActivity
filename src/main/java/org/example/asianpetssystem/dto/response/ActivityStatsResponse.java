package org.example.asianpetssystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 活动统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "活动统计数据")
public class ActivityStatsResponse {

    @Schema(description = "活动总数", example = "100")
    private Long totalActivities;

    @Schema(description = "总报名人数", example = "500")
    private Long totalSignups;

    @Schema(description = "总签到人数", example = "450")
    private Long totalCheckins;

    @Schema(description = "平均参与率", example = "0.85")
    private Double averageParticipationRate;

    @Schema(description = "活动类型分布")
    private Map<String, Long> activityTypeDistribution;
}
