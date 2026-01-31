package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.dto.request.AuditRequest;
import org.example.asianpetssystem.dto.response.ApplyListResponse;
import org.example.asianpetssystem.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口", description = "管理员专用功能接口")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @GetMapping("/apply/list")
    @Operation(summary = "获取待审核列表", description = "分页获取待审核的会员申请列表")
    public ResponseEntity<List<ApplyListResponse>> getPendingApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        logger.info("开始获取待审核申请列表 - page={}, size={}, status={}", page, size, status);
        long startTime = System.currentTimeMillis();
        
        try {
            List<ApplyListResponse> applications = adminService.getPendingApplications(page, size, status);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取待审核申请列表成功 - 返回 {} 条记录, 耗时: {}ms", applications.size(), duration);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取待审核申请列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }



    @PutMapping("/apply/{id}/audit")
    @Operation(summary = "审核会员申请", description = "审核会员申请（通过/拒绝/要求补充材料）")
    public ResponseEntity<?> auditApplication(
            @PathVariable Long id,
            @Valid @RequestBody AuditRequest request) {
        
        logger.info("开始审核会员申请 - ID={}, 操作={}", id, request.getAction());
        long startTime = System.currentTimeMillis();
        
        try {
            adminService.auditApplication(id, request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员申请审核完成 - ID={}, 操作={}, 耗时: {}ms", id, request.getAction(), duration);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员申请审核失败 - ID={}, 操作={}, 耗时: {}ms, 错误: {}", id, request.getAction(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/member/export")
    @Operation(summary = "导出会员列表", description = "导出所有会员信息到Excel文件")
    public ResponseEntity<Resource> exportMembers() {
        logger.info("开始导出会员列表到Excel");
        long startTime = System.currentTimeMillis();
        
        try {
            Resource resource = adminService.exportMembersToExcel();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员列表导出成功 - 耗时: {}ms", duration);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"members_" + System.currentTimeMillis() + ".xlsx\"")
                    .body(resource);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员列表导出失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/member/{id}")
    @Operation(summary = "删除会员", description = "删除指定ID的会员记录")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        logger.info("开始删除会员 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            adminService.deleteMember(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员删除成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员删除失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/member/{id}/suspend")
    @Operation(summary = "暂停会员资格", description = "暂停指定ID的会员资格")
    public ResponseEntity<?> suspendMember(@PathVariable Long id) {
        logger.info("开始暂停会员资格 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            adminService.suspendMember(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员资格暂停成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员资格暂停失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/member/{id}/activate")
    @Operation(summary = "激活会员资格", description = "重新激活指定ID的会员资格")
    public ResponseEntity<?> activateMember(@PathVariable Long id) {
        logger.info("开始激活会员资格 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            adminService.activateMember(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员资格激活成功 - ID={}, 耗时: {}ms", id, duration);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员资格激活失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }
}
