package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.dto.request.BatchMessageRequest;
import org.example.asianpetssystem.dto.request.BatchUpdateRequest;
import org.example.asianpetssystem.service.BatchOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/batch")
@Tag(name = "批量操作", description = "批量操作接口")
@PreAuthorize("hasRole('ADMIN')")
public class BatchOperationController {

    private static final Logger logger = LoggerFactory.getLogger(BatchOperationController.class);

    @Autowired
    private BatchOperationService batchOperationService;

    @PostMapping("/members/status")
    @Operation(summary = "批量更新会员状态", description = "批量更新多个会员的状态")
    public ApiResponse<Map<String, Object>> batchUpdateMemberStatus(@RequestBody BatchUpdateRequest request) {
        logger.info("批量更新会员状态 - count={}", request.getIds().size());
        return ApiResponse.success(batchOperationService.batchUpdateMemberStatus(request));
    }

    @PostMapping("/messages/send")
    @Operation(summary = "批量发送消息", description = "批量向多个会员发送消息")
    public ApiResponse<Map<String, Object>> batchSendMessage(@RequestBody BatchMessageRequest request) {
        logger.info("批量发送消息 - member count={}", request.getMemberIds().size());
        return ApiResponse.success(batchOperationService.batchSendMessage(request));
    }

    @PostMapping("/delete/{entityType}")
    @Operation(summary = "批量删除", description = "批量删除指定类型的实体")
    public ApiResponse<Map<String, Object>> batchDelete(
            @PathVariable String entityType,
            @RequestBody List<Long> ids) {
        logger.info("批量删除 - entityType={}, count={}", entityType, ids.size());
        return ApiResponse.success(batchOperationService.batchDelete(entityType, ids));
    }
}
