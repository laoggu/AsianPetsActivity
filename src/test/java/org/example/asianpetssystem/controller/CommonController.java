package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.dto.response.LevelRightsResponse;
import org.example.asianpetssystem.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/common")
@Tag(name = "通用接口", description = "通用配置、权益说明等公共接口")
public class CommonController {

    @Autowired
    private CommonService commonService;

    @GetMapping("/level-rights")
    @Operation(summary = "获取会员等级权益", description = "获取各会员等级对应的权益说明")
    public ResponseEntity<List<LevelRightsResponse>> getLevelRights() {
        List<LevelRightsResponse> rights = commonService.getLevelRights();
        return ResponseEntity.ok(rights);
    }

    @GetMapping("/system-config")
    @Operation(summary = "获取系统配置", description = "获取系统通用配置信息")
    public ResponseEntity<SystemConfigResponse> getSystemConfig() {
        SystemConfigResponse config = commonService.getSystemConfig();
        return ResponseEntity.ok(config);
    }

    @GetMapping("/activity-types")
    @Operation(summary = "获取活动类型", description = "获取系统支持的所有活动类型")
    public ResponseEntity<List<ActivityTypeResponse>> getActivityTypes() {
        List<ActivityTypeResponse> types = commonService.getActivityTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/announcement")
    @Operation(summary = "获取系统公告", description = "获取最新的系统公告信息")
    public ResponseEntity<AnnouncementResponse> getLatestAnnouncement() {
        AnnouncementResponse announcement = commonService.getLatestAnnouncement();
        return ResponseEntity.ok(announcement);
    }
}
