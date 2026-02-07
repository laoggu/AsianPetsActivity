package org.example.asianpetssystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 地域分布响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "地域分布数据")
public class GeographicDistributionResponse {

    @Schema(description = "省份分布")
    private List<RegionStat> provinceDistribution;

    @Schema(description = "城市分布（前10）")
    private List<RegionStat> cityDistribution;

    /**
     * 地区统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "地区统计数据")
    public static class RegionStat {
        @Schema(description = "地区名称", example = "北京市")
        private String region;

        @Schema(description = "数量", example = "100")
        private Long count;

        @Schema(description = "百分比", example = "15.5")
        private Double percentage;
    }
}
