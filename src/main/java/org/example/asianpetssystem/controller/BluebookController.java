package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.BluebookCreateRequest;
import org.example.asianpetssystem.dto.request.BluebookUpdateRequest;
import org.example.asianpetssystem.dto.response.BluebookResponse;
import org.example.asianpetssystem.service.BluebookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/bluebooks")
@Tag(name = "蓝皮书管理", description = "蓝皮书发布、更新、删除和统计管理")
public class BluebookController {

    private static final Logger logger = LoggerFactory.getLogger(BluebookController.class);

    @Autowired
    private BluebookService bluebookService;

    @GetMapping
    @Operation(summary = "获取蓝皮书列表", description = "分页获取蓝皮书列表，支持按年份筛选")
    public ResponseEntity<ApiResponse<PageResponse<BluebookResponse>>> getBluebookList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("开始获取蓝皮书列表 - year={}, keyword={}, page={}, size={}", year, keyword, page, size);
        long startTime = System.currentTimeMillis();

        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            PageResponse<BluebookResponse> result = bluebookService.getBluebookList(year, keyword, pageRequest);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取蓝皮书列表成功 - 返回 {} 条记录, 耗时: {}ms", result.getContent().size(), duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取蓝皮书列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "发布蓝皮书", description = "创建并发布新的蓝皮书")
    public ResponseEntity<ApiResponse<BluebookResponse>> createBluebook(
            @Valid @RequestBody BluebookCreateRequest request) {

        logger.info("开始发布蓝皮书 - title={}, year={}", request.getTitle(), request.getYear());
        long startTime = System.currentTimeMillis();

        try {
            BluebookResponse result = bluebookService.createBluebook(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("蓝皮书发布成功 - ID={}, 耗时: {}ms", result.getId(), duration);
            return ResponseEntity.ok(ApiResponse.success(result, "蓝皮书发布成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("蓝皮书发布失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取蓝皮书详情", description = "根据ID获取蓝皮书详细信息")
    public ResponseEntity<ApiResponse<BluebookResponse>> getBluebookById(@PathVariable Long id) {

        logger.info("开始获取蓝皮书详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            BluebookResponse result = bluebookService.getBluebookById(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取蓝皮书详情成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取蓝皮书详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新蓝皮书", description = "更新指定ID的蓝皮书信息")
    public ResponseEntity<ApiResponse<BluebookResponse>> updateBluebook(
            @PathVariable Long id,
            @Valid @RequestBody BluebookUpdateRequest request) {

        logger.info("开始更新蓝皮书 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            BluebookResponse result = bluebookService.updateBluebook(id, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("蓝皮书更新成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result, "蓝皮书更新成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("蓝皮书更新失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除蓝皮书", description = "删除指定ID的蓝皮书（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteBluebook(@PathVariable Long id) {

        logger.info("开始删除蓝皮书 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            bluebookService.deleteBluebook(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("蓝皮书删除成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(null, "蓝皮书删除成功"));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("蓝皮书删除失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}/download-stats")
    @Operation(summary = "下载统计", description = "获取指定蓝皮书的下载统计数据")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDownloadStats(@PathVariable Long id) {

        logger.info("开始获取蓝皮书下载统计 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> result = bluebookService.getDownloadStats(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取蓝皮书下载统计成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取蓝皮书下载统计失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }
}
