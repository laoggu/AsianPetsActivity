package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.ActivityCreateRequest;
import org.example.asianpetssystem.dto.request.ActivitySignupAuditRequest;
import org.example.asianpetssystem.dto.request.ActivityUpdateRequest;
import org.example.asianpetssystem.dto.response.ActivityCheckinResponse;
import org.example.asianpetssystem.dto.response.ActivityResponse;
import org.example.asianpetssystem.dto.response.ActivitySignupResponse;
import org.example.asianpetssystem.service.ActivityService;
import org.example.asianpetssystem.service.QRCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动管理Controller
 * 提供管理员活动相关的API接口
 */
@RestController
@RequestMapping("/api/admin/activities")
@Tag(name = "活动管理", description = "活动管理相关接口")
@PreAuthorize("hasRole('ADMIN')")
public class ActivityController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private QRCodeService qrCodeService;

    /**
     * 获取活动列表（分页、筛选、搜索）
     *
     * @param status  活动状态（可选）
     * @param keyword 搜索关键词（可选）
     * @param page    页码（默认0）
     * @param size    每页大小（默认20）
     * @return 分页活动列表
     */
    @GetMapping
    @Operation(summary = "获取活动列表", description = "分页获取活动列表，支持状态筛选和关键词搜索")
    public ApiResponse<PageResponse<ActivityResponse>> getActivityList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("开始获取活动列表 - status={}, keyword={}, page={}, size={}", status, keyword, page, size);
        long startTime = System.currentTimeMillis();

        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            PageResponse<ActivityResponse> result = activityService.getActivityList(status, keyword, pageRequest);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取活动列表成功 - 返回 {} 条记录, 总记录数: {}, 耗时: {}ms",
                    result.getContent().size(), result.getTotalElements(), duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取活动列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 创建活动
     *
     * @param request 创建活动请求
     * @return 创建后的活动详情
     */
    @PostMapping
    @Operation(summary = "创建活动", description = "创建新的活动")
    public ApiResponse<ActivityResponse> createActivity(
            @Valid @RequestBody ActivityCreateRequest request) {

        logger.info("开始创建活动 - title={}", request.getTitle());
        long startTime = System.currentTimeMillis();

        try {
            ActivityResponse result = activityService.createActivity(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建活动成功 - ID={}, title={}, 耗时: {}ms", result.getId(), result.getTitle(), duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建活动失败 - title={}, 耗时: {}ms, 错误: {}", request.getTitle(), duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取活动详情
     *
     * @param id 活动ID
     * @return 活动详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取活动详情", description = "根据ID获取活动详细信息")
    public ApiResponse<ActivityResponse> getActivityById(@PathVariable Long id) {

        logger.info("开始获取活动详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            ActivityResponse result = activityService.getActivityById(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取活动详情成功 - ID={}, title={}, 耗时: {}ms", id, result.getTitle(), duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取活动详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新活动
     *
     * @param id      活动ID
     * @param request 更新活动请求
     * @return 更新后的活动详情
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新活动", description = "更新指定ID的活动信息")
    public ApiResponse<ActivityResponse> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateRequest request) {

        logger.info("开始更新活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            ActivityResponse result = activityService.updateActivity(id, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新活动成功 - ID={}, title={}, 耗时: {}ms", id, result.getTitle(), duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除活动（软删除）
     *
     * @param id 活动ID
     * @return 空响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动", description = "删除指定ID的活动（软删除）")
    public ApiResponse<Void> deleteActivity(@PathVariable Long id) {

        logger.info("开始删除活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            activityService.deleteActivity(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除活动成功 - ID={}, 耗时: {}ms", id, duration);
            return ApiResponse.success(null);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发布活动
     *
     * @param id 活动ID
     * @return 空响应
     */
    @PutMapping("/{id}/publish")
    @Operation(summary = "发布活动", description = "发布指定ID的活动")
    public ApiResponse<Void> publishActivity(@PathVariable Long id) {

        logger.info("开始发布活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            activityService.publishActivity(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("发布活动成功 - ID={}, 耗时: {}ms", id, duration);
            return ApiResponse.success(null);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("发布活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 取消活动
     *
     * @param id 活动ID
     * @return 空响应
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消活动", description = "取消指定ID的活动")
    public ApiResponse<Void> cancelActivity(@PathVariable Long id) {

        logger.info("开始取消活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            activityService.cancelActivity(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("取消活动成功 - ID={}, 耗时: {}ms", id, duration);
            return ApiResponse.success(null);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("取消活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取活动报名列表
     *
     * @param activityId 活动ID
     * @param status     报名状态（可选）
     * @param page       页码（默认0）
     * @param size       每页大小（默认20）
     * @return 分页报名列表
     */
    @GetMapping("/{id}/signups")
    @Operation(summary = "获取报名列表", description = "获取指定活动的报名列表，支持状态筛选")
    public ApiResponse<PageResponse<ActivitySignupResponse>> getSignupList(
            @PathVariable("id") Long activityId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("开始获取报名列表 - activityId={}, status={}, page={}, size={}", activityId, status, page, size);
        long startTime = System.currentTimeMillis();

        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            PageResponse<ActivitySignupResponse> result = activityService.getSignupList(activityId, status, pageRequest);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取报名列表成功 - activityId={}, 返回 {} 条记录, 总记录数: {}, 耗时: {}ms",
                    activityId, result.getContent().size(), result.getTotalElements(), duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取报名列表失败 - activityId={}, 耗时: {}ms, 错误: {}", activityId, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 审核报名
     *
     * @param activityId 活动ID
     * @param userId     用户ID
     * @param request    审核请求
     * @return 空响应
     */
    @PutMapping("/{id}/signups/{userId}/audit")
    @Operation(summary = "审核报名", description = "审核指定活动的报名申请")
    public ApiResponse<Void> auditSignup(
            @PathVariable("id") Long activityId,
            @PathVariable Long userId,
            @Valid @RequestBody ActivitySignupAuditRequest request) {

        logger.info("开始审核报名 - activityId={}, userId={}, status={}", activityId, userId, request.getStatus());
        long startTime = System.currentTimeMillis();

        try {
            activityService.auditSignup(activityId, userId, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("审核报名成功 - activityId={}, userId={}, status={}, 耗时: {}ms",
                    activityId, userId, request.getStatus(), duration);
            return ApiResponse.success(null);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("审核报名失败 - activityId={}, userId={}, 耗时: {}ms, 错误: {}",
                    activityId, userId, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取活动签到数据
     *
     * @param activityId 活动ID
     * @return 签到数据列表
     */
    @GetMapping("/{id}/checkin")
    @Operation(summary = "获取签到数据", description = "获取指定活动的签到数据列表")
    public ApiResponse<List<ActivityCheckinResponse>> getCheckinList(@PathVariable("id") Long activityId) {

        logger.info("开始获取签到数据 - activityId={}", activityId);
        long startTime = System.currentTimeMillis();

        try {
            List<ActivityCheckinResponse> result = activityService.getCheckinList(activityId);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取签到数据成功 - activityId={}, 返回 {} 条记录, 耗时: {}ms",
                    activityId, result.size(), duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取签到数据失败 - activityId={}, 耗时: {}ms, 错误: {}", activityId, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 现场签到
     *
     * @param activityId  活动ID
     * @param checkinCode 签到码
     * @return 空响应
     */
    @PostMapping("/{id}/checkin")
    @Operation(summary = "现场签到", description = "使用签到码进行活动现场签到")
    public ApiResponse<Void> checkin(
            @PathVariable("id") Long activityId,
            @RequestParam String checkinCode) {

        logger.info("开始现场签到 - activityId={}, checkinCode={}", activityId, checkinCode);
        long startTime = System.currentTimeMillis();

        try {
            activityService.checkin(activityId, checkinCode);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("现场签到成功 - activityId={}, checkinCode={}, 耗时: {}ms", activityId, checkinCode, duration);
            return ApiResponse.success(null);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("现场签到失败 - activityId={}, checkinCode={}, 耗时: {}ms, 错误: {}",
                    activityId, checkinCode, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 生成活动签到二维码
     *
     * @param activityId 活动ID
     * @param width      二维码宽度（默认300）
     * @param height     二维码高度（默认300）
     * @return Base64编码的二维码图片
     */
    @GetMapping("/{id}/qrcode")
    @Operation(summary = "生成签到二维码", description = "生成活动的现场签到二维码，用户扫码即可完成签到")
    public ApiResponse<java.util.Map<String, String>> generateCheckinQRCode(
            @PathVariable("id") Long activityId,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {

        logger.info("开始生成活动签到二维码 - activityId={}, size={}x{}", activityId, width, height);
        long startTime = System.currentTimeMillis();

        try {
            String qrCodeBase64 = qrCodeService.generateActivityCheckinQRCode(activityId, width, height);

            java.util.Map<String, String> result = new java.util.HashMap<>();
            result.put("qrCode", qrCodeBase64);
            result.put("activityId", activityId.toString());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("生成活动签到二维码成功 - activityId={}, 耗时: {}ms", activityId, duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("生成活动签到二维码失败 - activityId={}, 耗时: {}ms, 错误: {}",
                    activityId, duration, e.getMessage(), e);
            throw e;
        }
    }
}
