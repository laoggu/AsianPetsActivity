package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.dto.response.ActivityTypeResponse;
import org.example.asianpetssystem.dto.response.AnnouncementResponse;
import org.example.asianpetssystem.dto.response.LevelRightsResponse;
import org.example.asianpetssystem.dto.response.MemberRightResponse;
import org.example.asianpetssystem.dto.response.SystemConfigResponse;
import org.example.asianpetssystem.service.CommonService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public List<LevelRightsResponse> getLevelRights() {
        List<LevelRightsResponse> result = new ArrayList<>();
        
        // 普通会员权益
        LevelRightsResponse regular = new LevelRightsResponse();
        regular.setLevel("REGULAR");
        regular.setLevelName("普通会员");
        regular.setRights(Arrays.asList(
            createMemberRightResponse(1L, "REGULAR", "参与普通活动", "可参与协会组织的各类普通活动", "activity", 1),
            createMemberRightResponse(2L, "REGULAR", "获取行业资讯", "定期获取宠物行业最新资讯", "news", 2),
            createMemberRightResponse(3L, "REGULAR", "享受基本咨询服务", "享受协会提供的基本咨询服务", "service", 3)
        ));
        result.add(regular);

        // 黄金会员权益
        LevelRightsResponse gold = new LevelRightsResponse();
        gold.setLevel("GOLD");
        gold.setLevelName("黄金会员");
        gold.setRights(Arrays.asList(
            createMemberRightResponse(4L, "GOLD", "参与高级活动", "可参与协会组织的高级专属活动", "activity", 1),
            createMemberRightResponse(5L, "GOLD", "优先报名权", "热门活动优先报名", "priority", 2),
            createMemberRightResponse(6L, "GOLD", "专属顾问服务", "享受一对一专属顾问服务", "service", 3),
            createMemberRightResponse(7L, "GOLD", "定制化服务", "根据需求提供定制化服务方案", "custom", 4)
        ));
        result.add(gold);

        // 白金会员权益
        LevelRightsResponse platinum = new LevelRightsResponse();
        platinum.setLevel("PLATINUM");
        platinum.setLevelName("白金会员");
        platinum.setRights(Arrays.asList(
            createMemberRightResponse(8L, "PLATINUM", "参与顶级活动", "可参与协会顶级VIP专属活动", "activity", 1),
            createMemberRightResponse(9L, "PLATINUM", "VIP通道", "享受VIP专属服务通道", "vip", 2),
            createMemberRightResponse(10L, "PLATINUM", "专属客服", "24小时专属客服支持", "service", 3),
            createMemberRightResponse(11L, "PLATINUM", "定制化解决方案", "提供全方位定制化解决方案", "custom", 4)
        ));
        result.add(platinum);

        return result;
    }
    
    private MemberRightResponse createMemberRightResponse(Long id, String level, String title, String description, String icon, Integer sortOrder) {
        MemberRightResponse response = new MemberRightResponse();
        response.setId(id);
        response.setLevel(level);
        response.setTitle(title);
        response.setDescription(description);
        response.setIcon(icon);
        response.setSortOrder(sortOrder);
        response.setIsActive(true);
        return response;
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
        announcement.setPublishTime(java.time.LocalDateTime.now());
        return announcement;
    }
}
