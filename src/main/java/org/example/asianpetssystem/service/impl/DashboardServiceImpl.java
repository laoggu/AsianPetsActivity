package org.example.asianpetssystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.asianpetssystem.common.enums.ActivityStatus;
import org.example.asianpetssystem.common.enums.MemberLevel;
import org.example.asianpetssystem.common.enums.MemberStatus;
import org.example.asianpetssystem.dto.response.*;
import org.example.asianpetssystem.entity.Activity;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.repository.*;
import org.example.asianpetssystem.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MemberRepository memberRepository;
    private final ActivityRepository activityRepository;
    private final ActivitySignupRepository activitySignupRepository;
    private final ActivityCheckinRepository activityCheckinRepository;
    private final BluebookRepository bluebookRepository;
    private final AnnouncementRepository announcementRepository;

    @Override
    public DashboardOverviewResponse getOverview() {
        log.info("开始获取仪表盘概览数据");
        long startTime = System.currentTimeMillis();

        try {
            // 总会员数
            long totalMembers = memberRepository.count();

            // 待审核申请数
            long pendingApplications = memberRepository.findAll().stream()
                    .filter(m -> m.getStatus() == MemberStatus.PENDING)
                    .count();

            // 总活动数
            long totalActivities = activityRepository.findAll().stream()
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .count();

            // 进行中活动数（已发布且结束时间大于当前时间）
            LocalDateTime now = LocalDateTime.now();
            long ongoingActivities = activityRepository
                    .findByStatusAndEndTimeAfterAndIsDeletedFalse(ActivityStatus.PUBLISHED, now)
                    .size();

            // 蓝皮书数量
            long totalBluebooks = bluebookRepository.findAll().stream()
                    .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                    .count();

            // 公告数量
            long totalAnnouncements = announcementRepository.findAll().stream()
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .count();

            DashboardOverviewResponse response = DashboardOverviewResponse.builder()
                    .totalMembers(totalMembers)
                    .pendingApplications(pendingApplications)
                    .totalActivities(totalActivities)
                    .ongoingActivities(ongoingActivities)
                    .totalBluebooks(totalBluebooks)
                    .totalAnnouncements(totalAnnouncements)
                    .build();

            long duration = System.currentTimeMillis() - startTime;
            log.info("获取仪表盘概览数据成功 - 耗时: {}ms", duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取仪表盘概览数据失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MemberStatsResponse getMemberStats() {
        log.info("开始获取会员统计数据");
        long startTime = System.currentTimeMillis();

        try {
            List<Member> allMembers = memberRepository.findAll();

            // 会员增长趋势（近12个月）
            List<MemberStatsResponse.MonthlyStat> memberGrowthTrend = calculateMemberGrowthTrend(allMembers);

            // 会员级别分布
            Map<String, Long> levelDistribution = allMembers.stream()
                    .filter(m -> m.getLevel() != null)
                    .collect(Collectors.groupingBy(
                            m -> m.getLevel().getDescription(),
                            Collectors.counting()
                    ));

            // 会员状态分布
            Map<String, Long> statusDistribution = allMembers.stream()
                    .filter(m -> m.getStatus() != null)
                    .collect(Collectors.groupingBy(
                            m -> m.getStatus().getDescription(),
                            Collectors.counting()
                    ));

            MemberStatsResponse response = MemberStatsResponse.builder()
                    .memberGrowthTrend(memberGrowthTrend)
                    .levelDistribution(levelDistribution)
                    .statusDistribution(statusDistribution)
                    .build();

            long duration = System.currentTimeMillis() - startTime;
            log.info("获取会员统计数据成功 - 耗时: {}ms", duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取会员统计数据失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 计算会员增长趋势（近12个月）
     */
    private List<MemberStatsResponse.MonthlyStat> calculateMemberGrowthTrend(List<Member> members) {
        List<MemberStatsResponse.MonthlyStat> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDateTime now = LocalDateTime.now();

        for (int i = 11; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            LocalDateTime monthEnd = monthStart.plusMonths(1);

            String monthStr = monthStart.format(formatter);

            long count = members.stream()
                    .filter(m -> m.getCreatedAt() != null)
                    .filter(m -> !m.getCreatedAt().isBefore(monthStart) && m.getCreatedAt().isBefore(monthEnd))
                    .count();

            trend.add(MemberStatsResponse.MonthlyStat.builder()
                    .month(monthStr)
                    .count(count)
                    .build());
        }

        return trend;
    }

    @Override
    public ActivityStatsResponse getActivityStats() {
        log.info("开始获取活动统计数据");
        long startTime = System.currentTimeMillis();

        try {
            List<Activity> allActivities = activityRepository.findAll().stream()
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .collect(Collectors.toList());

            // 活动总数
            long totalActivities = allActivities.size();

            // 总报名人数
            long totalSignups = allActivities.stream()
                    .mapToLong(a -> activitySignupRepository.countByActivityId(a.getId()))
                    .sum();

            // 总签到人数
            long totalCheckins = allActivities.stream()
                    .mapToLong(a -> activityCheckinRepository.countByActivityId(a.getId()))
                    .sum();

            // 平均参与率
            Double averageParticipationRate = calculateAverageParticipationRate(allActivities);

            // 活动类型分布
            Map<String, Long> activityTypeDistribution = allActivities.stream()
                    .filter(a -> a.getActivityType() != null && !a.getActivityType().isEmpty())
                    .collect(Collectors.groupingBy(
                            Activity::getActivityType,
                            Collectors.counting()
                    ));

            ActivityStatsResponse response = ActivityStatsResponse.builder()
                    .totalActivities(totalActivities)
                    .totalSignups(totalSignups)
                    .totalCheckins(totalCheckins)
                    .averageParticipationRate(averageParticipationRate)
                    .activityTypeDistribution(activityTypeDistribution)
                    .build();

            long duration = System.currentTimeMillis() - startTime;
            log.info("获取活动统计数据成功 - 耗时: {}ms", duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取活动统计数据失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 计算平均参与率
     */
    private Double calculateAverageParticipationRate(List<Activity> activities) {
        if (activities.isEmpty()) {
            return 0.0;
        }

        double totalRate = 0.0;
        int count = 0;

        for (Activity activity : activities) {
            long signups = activitySignupRepository.countByActivityId(activity.getId());
            long checkins = activityCheckinRepository.countByActivityId(activity.getId());

            if (signups > 0) {
                totalRate += (double) checkins / signups;
                count++;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return BigDecimal.valueOf(totalRate / count)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public GeographicDistributionResponse getGeographicDistribution() {
        log.info("开始获取地域分布数据");
        long startTime = System.currentTimeMillis();

        try {
            // 从活动地点统计地域分布
            List<Activity> allActivities = activityRepository.findAll().stream()
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .filter(a -> a.getLocation() != null && !a.getLocation().isEmpty())
                    .collect(Collectors.toList());

            // 统计活动位置分布（模拟省份/城市分布）
            Map<String, Long> locationCount = allActivities.stream()
                    .collect(Collectors.groupingBy(
                            Activity::getLocation,
                            Collectors.counting()
                    ));

            long total = locationCount.values().stream().mapToLong(Long::longValue).sum();

            // 构建省份分布（使用活动地点作为省份统计）
            List<GeographicDistributionResponse.RegionStat> provinceDistribution = 
                    buildRegionStats(locationCount, total);

            // 城市分布（前10）
            List<GeographicDistributionResponse.RegionStat> cityDistribution = 
                    provinceDistribution.stream()
                            .limit(10)
                            .collect(Collectors.toList());

            GeographicDistributionResponse response = GeographicDistributionResponse.builder()
                    .provinceDistribution(provinceDistribution)
                    .cityDistribution(cityDistribution)
                    .build();

            long duration = System.currentTimeMillis() - startTime;
            log.info("获取地域分布数据成功 - 耗时: {}ms", duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取地域分布数据失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 构建地区统计列表
     */
    private List<GeographicDistributionResponse.RegionStat> buildRegionStats(
            Map<String, Long> locationCount, long total) {
        
        return locationCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    double percentage = total > 0 
                            ? BigDecimal.valueOf((double) entry.getValue() * 100 / total)
                                    .setScale(1, RoundingMode.HALF_UP)
                                    .doubleValue()
                            : 0.0;
                    
                    return GeographicDistributionResponse.RegionStat.builder()
                            .region(entry.getKey())
                            .count(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public BusinessScopeDistributionResponse getBusinessScopeDistribution() {
        log.info("开始获取业务范畴分布数据");
        long startTime = System.currentTimeMillis();

        try {
            // 从附件类型统计业务范畴分布
            List<Member> allMembers = memberRepository.findAll();
            
            // 使用会员级别作为业务范畴的替代统计（可扩展为实际的业务范畴字段）
            Map<String, Long> scopeCount = allMembers.stream()
                    .filter(m -> m.getLevel() != null)
                    .collect(Collectors.groupingBy(
                            m -> m.getLevel().getDescription(),
                            Collectors.counting()
                    ));

            long total = scopeCount.values().stream().mapToLong(Long::longValue).sum();

            List<BusinessScopeDistributionResponse.BusinessStat> distribution = 
                    scopeCount.entrySet().stream()
                            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                            .map(entry -> {
                                double percentage = total > 0 
                                        ? BigDecimal.valueOf((double) entry.getValue() * 100 / total)
                                                .setScale(1, RoundingMode.HALF_UP)
                                                .doubleValue()
                                        : 0.0;
                                
                                return BusinessScopeDistributionResponse.BusinessStat.builder()
                                        .scope(entry.getKey())
                                        .count(entry.getValue())
                                        .percentage(percentage)
                                        .build();
                            })
                            .collect(Collectors.toList());

            BusinessScopeDistributionResponse response = BusinessScopeDistributionResponse.builder()
                    .distribution(distribution)
                    .build();

            long duration = System.currentTimeMillis() - startTime;
            log.info("获取业务范畴分布数据成功 - 耗时: {}ms", duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("获取业务范畴分布数据失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }
}
