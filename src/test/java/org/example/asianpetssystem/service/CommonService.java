package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.response.LevelRightsResponse;
import org.example.asianpetssystem.dto.response.SystemConfigResponse;
import org.example.asianpetssystem.dto.response.ActivityTypeResponse;
import org.example.asianpetssystem.dto.response.AnnouncementResponse;

import java.util.List;

public interface CommonService {

    /**
     * 获取会员等级权益
     */
    List<LevelRightsResponse> getLevelRights();

    /**
     * 获取系统配置
     */
    SystemConfigResponse getSystemConfig();

    /**
     * 获取活动类型
     */
    List<ActivityTypeResponse> getActivityTypes();

    /**
     * 获取最新公告
     */
    AnnouncementResponse getLatestAnnouncement();
}
