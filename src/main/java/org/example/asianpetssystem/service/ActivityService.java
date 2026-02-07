package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.ActivityCreateRequest;
import org.example.asianpetssystem.dto.request.ActivitySignupAuditRequest;
import org.example.asianpetssystem.dto.request.ActivityUpdateRequest;
import org.example.asianpetssystem.dto.response.ActivityCheckinResponse;
import org.example.asianpetssystem.dto.response.ActivityResponse;
import org.example.asianpetssystem.dto.response.ActivitySignupResponse;

import java.util.List;

/**
 * 活动管理Service接口
 */
public interface ActivityService {

    /**
     * 获取活动列表
     *
     * @param status      活动状态
     * @param keyword     搜索关键词（标题）
     * @param pageRequest 分页请求
     * @return 活动列表分页响应
     */
    PageResponse<ActivityResponse> getActivityList(String status, String keyword, PageRequest pageRequest);

    /**
     * 根据ID获取活动详情
     *
     * @param id 活动ID
     * @return 活动详情
     */
    ActivityResponse getActivityById(Long id);

    /**
     * 创建活动
     *
     * @param request 创建请求
     * @return 创建后的活动详情
     */
    ActivityResponse createActivity(ActivityCreateRequest request);

    /**
     * 更新活动
     *
     * @param id      活动ID
     * @param request 更新请求
     * @return 更新后的活动详情
     */
    ActivityResponse updateActivity(Long id, ActivityUpdateRequest request);

    /**
     * 删除活动（软删除）
     *
     * @param id 活动ID
     */
    void deleteActivity(Long id);

    /**
     * 发布活动
     *
     * @param id 活动ID
     */
    void publishActivity(Long id);

    /**
     * 取消活动
     *
     * @param id 活动ID
     */
    void cancelActivity(Long id);

    /**
     * 获取活动报名列表
     *
     * @param activityId  活动ID
     * @param status      报名状态
     * @param pageRequest 分页请求
     * @return 报名列表分页响应
     */
    PageResponse<ActivitySignupResponse> getSignupList(Long activityId, String status, PageRequest pageRequest);

    /**
     * 审核报名
     *
     * @param activityId 活动ID
     * @param userId     用户ID
     * @param request    审核请求
     */
    void auditSignup(Long activityId, Long userId, ActivitySignupAuditRequest request);

    /**
     * 获取活动签到列表
     *
     * @param activityId 活动ID
     * @return 签到列表
     */
    List<ActivityCheckinResponse> getCheckinList(Long activityId);

    /**
     * 现场签到
     *
     * @param activityId  活动ID
     * @param checkinCode 签到码
     */
    void checkin(Long activityId, String checkinCode);
}
