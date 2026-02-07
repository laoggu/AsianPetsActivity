package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.response.*;

/**
 * 仪表盘统计服务接口
 */
public interface DashboardService {

    /**
     * 获取概览数据
     *
     * @return 仪表盘概览数据
     */
    DashboardOverviewResponse getOverview();

    /**
     * 获取会员统计数据
     *
     * @return 会员统计数据
     */
    MemberStatsResponse getMemberStats();

    /**
     * 获取活动统计数据
     *
     * @return 活动统计数据
     */
    ActivityStatsResponse getActivityStats();

    /**
     * 获取地域分布数据
     *
     * @return 地域分布数据
     */
    GeographicDistributionResponse getGeographicDistribution();

    /**
     * 获取业务范畴分布数据
     *
     * @return 业务范畴分布数据
     */
    BusinessScopeDistributionResponse getBusinessScopeDistribution();
}
