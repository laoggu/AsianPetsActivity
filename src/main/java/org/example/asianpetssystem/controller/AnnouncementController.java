package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.AnnouncementCreateRequest;
import org.example.asianpetssystem.dto.request.AnnouncementUpdateRequest;
import org.example.asianpetssystem.dto.response.AnnouncementResponse;
import org.example.asianpetssystem.service.AnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
@Tag(name = "公告管理", description = "公告发布、更新、删除和置顶管理")
public class AnnouncementController {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "获取公告列表", description = "分页获取公告列表，支持按类型筛选")
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementResponse>>> getAnnouncementList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("开始获取公告列表 - type={}, page={}, size={}", type, page, size);
        long startTime = System.currentTimeMillis();

        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            PageResponse<AnnouncementResponse> result = announcementService.getAnnouncementList(type, pageRequest);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取公告列表成功 - 返回 {} 条记录, 耗时: {}ms", result.getContent().size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取公告列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/top")
    @Operation(summary = "获取置顶公告", description = "获取当前所有置顶公告列表")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getTopAnnouncements() {

        logger.info("开始获取置顶公告列表");
        long startTime = System.currentTimeMillis();

        try {
            List<AnnouncementResponse> result = announcementService.getTopAnnouncements();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取置顶公告列表成功 - 返回 {} 条记录, 耗时: {}ms", result.size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取置顶公告列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "发布公告", description = "创建并发布新的公告")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @Valid @RequestBody AnnouncementCreateRequest request) {

        logger.info("开始发布公告 - title={}, type={}", request.getTitle(), request.getType());
        long startTime = System.currentTimeMillis();

        try {
            AnnouncementResponse result = announcementService.createAnnouncement(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("公告发布成功 - ID={}, 耗时: {}ms", result.getId(), duration);
            return ResponseEntity.ok(ApiResponse.success(result, "公告发布成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("公告发布失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取公告详情", description = "根据ID获取公告详细信息")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getAnnouncementById(@PathVariable Long id) {

        logger.info("开始获取公告详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AnnouncementResponse result = announcementService.getAnnouncementById(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取公告详情成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取公告详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公告", description = "更新指定ID的公告信息")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementUpdateRequest request) {

        logger.info("开始更新公告 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AnnouncementResponse result = announcementService.updateAnnouncement(id, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("公告更新成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result, "公告更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("公告更新失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告", description = "删除指定ID的公告（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable Long id) {

        logger.info("开始删除公告 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            announcementService.deleteAnnouncement(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("公告删除成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "公告删除成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("公告删除失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/top")
    @Operation(summary = "置顶/取消置顶公告", description = "设置或取消公告的置顶状态")
    public ResponseEntity<ApiResponse<Void>> topAnnouncement(
            @PathVariable Long id,
            @RequestParam Boolean isTop) {

        logger.info("开始设置公告置顶状态 - ID={}, isTop={}", id, isTop);
        long startTime = System.currentTimeMillis();

        try {
            announcementService.topAnnouncement(id, isTop);
            long duration = System.currentTimeMillis() - startTime;
            String action = isTop ? "置顶" : "取消置顶";
            logger.info("公告{}成功 - ID={}, 耗时: {}ms", action, id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "公告" + action + "成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("公告置顶操作失败 - ID={}, isTop={}, 耗时: {}ms, 错误: {}", id, isTop, duration, e.getMessage(), e);
            throw e;
        }
    }
}
