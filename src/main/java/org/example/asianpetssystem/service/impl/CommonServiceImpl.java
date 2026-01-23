package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.dto.response.ActivityTypeResponse;
import org.example.asianpetssystem.dto.response.AnnouncementResponse;
import org.example.asianpetssystem.dto.response.LevelRightsResponse;
import org.example.asianpetssystem.dto.response.SystemConfigResponse;
import org.example.asianpetssystem.service.CommonService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public List<LevelRightsResponse> getLevelRights() {
        // 返回会员等级权益信息
        LevelRightsResponse regular = new LevelRightsResponse();
        regular.setLevel("REGULAR");
        regular.setRights(Arrays.asList(
            "参与普通活动",
            "获取行业资讯",
            "享受基本咨询服务"
        ));

        LevelRightsResponse gold = new LevelRightsResponse();
        gold.setLevel("GOLD");
        gold.setRights(Arrays.asList(
            "参与高级活动",
            "优先报名权",
            "专属顾问服务",
            "定制化服务"
        ));

        LevelRightsResponse platinum = new LevelRightsResponse();
        platinum.setLevel("PLATINUM");
        platinum.setRights(Arrays.asList(
            "参与顶级活动",
            "VIP通道",
            "专属客服",
            "定制化解决方案"
        ));

        return Arrays.asList(regular, gold, platinum);
    }

    @Override
    public SystemConfigResponse getSystemConfig() {
        SystemConfigResponse config = new SystemConfigResponse();
        config.setSystemName("亚洲宠物协会管理系统");
        config.setVersion("1.0.0");
        config.setSupportEmail("support@asiapets.org");
        config.setSupportPhone("400-123-4567");
        return config;
    }

    @Override
    public List<ActivityTypeResponse> getActivityTypes() {
        ActivityTypeResponse exhibition = new ActivityTypeResponse();
        exhibition.setId(1L);
        exhibition.setName("展览活动");
        exhibition.setDescription("宠物展览及相关活动");

        ActivityTypeResponse conference = new ActivityTypeResponse();
        conference.setId(2L);
        conference.setName("会议活动");
        conference.setDescription("行业会议及研讨会");

        ActivityTypeResponse training = new ActivityTypeResponse();
        training.setId(3L);
        training.setName("培训活动");
        training.setDescription("专业培训及认证");

        return Arrays.asList(exhibition, conference, training);
    }

    @Override
    public AnnouncementResponse getLatestAnnouncement() {
        AnnouncementResponse announcement = new AnnouncementResponse();
        announcement.setTitle("关于2024年亚洲宠物展览会的通知");
        announcement.setContent("2024年亚洲宠物展览会将于明年春季举办...");
        announcement.setPublishDate(java.time.LocalDateTime.now());
        announcement.setPublisher("亚洲宠物协会");
        return announcement;
    }
}
