package org.example.asianpetssystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 会员统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员统计数据")
public class MemberStatsResponse {

    @Schema(description = "会员增长趋势（近12个月）")
    private List<MonthlyStat> memberGrowthTrend;

    @Schema(description = "会员级别分布")
    private Map<String, Long> levelDistribution;

    @Schema(description = "会员状态分布")
    private Map<String, Long> statusDistribution;

    /**
     * 月度统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "月度统计数据")
    public static class MonthlyStat {
        @Schema(description = "月份", example = "2025-01")
        private String month;

        @Schema(description = "数量", example = "50")
        private Long count;
    }
}
