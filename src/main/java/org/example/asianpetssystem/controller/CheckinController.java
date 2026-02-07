package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.QRCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 扫码签到控制器
 * 支持活动现场通用二维码（多人可扫）
 */
@RestController
@RequestMapping("/api/checkin")
@Tag(name = "扫码签到", description = "活动现场二维码扫码签到，一个码多人可扫")
public class CheckinController {

    private static final Logger logger = LoggerFactory.getLogger(CheckinController.class);

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    /**
     * 扫描二维码签到（活动现场通用码）
     * 
     * 使用场景：
     * 1. 活动现场展示通用签到二维码
     * 2. 参会人员打开小程序扫码
     * 3. 系统自动识别用户身份完成签到
     */
    @GetMapping("/scan")
    @Operation(summary = "扫码签到", description = "扫描活动现场通用二维码完成签到")
    public ResponseEntity<ApiResponse<QRCodeService.QRCheckinResult>> scanAndCheckin(
            @Parameter(description = "签到码，从二维码解析获得") @RequestParam String code) {

        logger.info("收到扫码签到请求 - checkinCode={}", code);
        long start = System.currentTimeMillis();

        try {
            // 获取当前登录用户ID
            Long currentUserId = authenticationFacade.getCurrentUserId();
            if (currentUserId == null) {
                logger.warn("扫码签到失败 - 用户未登录");
                return ResponseEntity.ok(ApiResponse.error(401, "请先登录后再进行签到"));
            }

            // 执行签到
            QRCodeService.QRCheckinResult result = qrCodeService.scanAndCheckin(code, currentUserId);

            long duration = System.currentTimeMillis() - start;
            if (result.isSuccess()) {
                logger.info("扫码签到成功 - userId={}, activityId={}, 耗时:{}ms", 
                        currentUserId, result.getActivityId(), duration);
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                logger.warn("扫码签到失败 - userId={}, 原因:{}, 耗时:{}ms", 
                        currentUserId, result.getMessage(), duration);
                return ResponseEntity.ok(ApiResponse.error(400, result.getMessage(), result));
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            logger.error("扫码签到异常 - 耗时:{}ms, 错误:{}", duration, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error("签到失败：" + e.getMessage()));
        }
    }

    /**
     * 验证签到码是否有效（用于预览）
     */
    @GetMapping("/validate")
    @Operation(summary = "验证签到码", description = "验证签到码是否有效（不执行签到）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateCheckinCode(
            @Parameter(description = "签到码") @RequestParam String code) {

        logger.info("验证签到码 - checkinCode={}", code);

        try {
            boolean valid = qrCodeService.validateCheckinCode(code);
            Long activityId = valid ? qrCodeService.getActivityIdFromCheckinCode(code) : null;

            Map<String, Object> result = new HashMap<>();
            result.put("valid", valid);
            result.put("activityId", activityId);

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            logger.error("验证签到码失败", e);
            return ResponseEntity.ok(ApiResponse.error("验证失败：" + e.getMessage()));
        }
    }
}
