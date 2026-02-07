package org.example.asianpetssystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仪表盘概览响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表盘概览数据")
public class DashboardOverviewResponse {

    @Schema(description = "总会员数", example = "1500")
    private Long totalMembers;

    @Schema(description = "待审核申请数", example = "25")
    private Long pendingApplications;

    @Schema(description = "总活动数", example = "100")
    private Long totalActivities;

    @Schema(description = "进行中活动数", example = "5")
    private Long ongoingActivities;

    @Schema(description = "蓝皮书数量", example = "50")
    private Long totalBluebooks;

    @Schema(description = "公告数量", example = "30")
    private Long totalAnnouncements;
}
