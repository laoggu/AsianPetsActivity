package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.response.OperationLogResponse;
import org.example.asianpetssystem.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/logs")
@Tag(name = "操作日志", description = "操作日志查询")
@PreAuthorize("hasRole('ADMIN')")
public class OperationLogController {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogController.class);

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    @Operation(summary = "获取操作日志列表", description = "分页获取操作日志，支持按操作人、操作类型筛选")
    public ResponseEntity<ApiResponse<PageResponse<OperationLogResponse>>> getLogList(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("开始获取操作日志列表 - module={}, action={}, operatorId={}, page={}, size={}",
                module, action, operatorId, page, size);
        long start = System.currentTimeMillis();

        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            PageResponse<OperationLogResponse> result = operationLogService.getLogList(
                    module, action, operatorId, pageRequest);

            long duration = System.currentTimeMillis() - start;
            logger.info("获取操作日志列表成功 - 返回 {} 条记录, 耗时: {}ms",
                    result.getContent().size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            logger.error("获取操作日志列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取操作日志详情", description = "根据ID获取操作日志详情")
    public ResponseEntity<ApiResponse<OperationLogResponse>> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(operationLogService.getLogById(id)));
    }
}
