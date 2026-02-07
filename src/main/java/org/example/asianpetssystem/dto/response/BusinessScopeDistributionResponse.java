package org.example.asianpetssystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 业务范畴分布响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "业务范畴分布数据")
public class BusinessScopeDistributionResponse {

    @Schema(description = "业务范畴分布")
    private List<BusinessStat> distribution;

    /**
     * 业务统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "业务范畴统计数据")
    public static class BusinessStat {
        @Schema(description = "业务范畴名称", example = "宠物食品")
        private String scope;

        @Schema(description = "数量", example = "50")
        private Long count;

        @Schema(description = "百分比", example = "25.0")
        private Double percentage;
    }
}
