package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.dto.request.AuditRequest;
import org.example.asianpetssystem.dto.response.ApplyListResponse;
import org.example.asianpetssystem.service.AdminService;
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
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员管理", description = "管理员审核、导出等功能接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/apply/list")
    @Operation(summary = "获取待审核列表", description = "获取所有待审核的会员申请")
    public ResponseEntity<List<ApplyListResponse>> getPendingApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        List<ApplyListResponse> applications = adminService.getPendingApplications(page, size, status);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/apply/{id}/audit")
    @Operation(summary = "审核会员申请", description = "审核会员申请（通过/拒绝/要求补充材料）")
    public ResponseEntity<?> auditApplication(
            @PathVariable Long id,
            @Valid @RequestBody AuditRequest request) {
        adminService.auditApplication(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member/export")
    @Operation(summary = "导出会员列表", description = "导出所有会员信息到Excel文件")
    public ResponseEntity<Resource> exportMembers() {
        Resource resource = adminService.exportMembersToExcel();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"members_" + System.currentTimeMillis() + ".xlsx\"")
                .body(resource);
    }

    @DeleteMapping("/member/{id}")
    @Operation(summary = "删除会员", description = "删除指定ID的会员记录")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        adminService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/member/{id}/suspend")
    @Operation(summary = "暂停会员资格", description = "暂停指定ID的会员资格")
    public ResponseEntity<?> suspendMember(@PathVariable Long id) {
        adminService.suspendMember(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/member/{id}/activate")
    @Operation(summary = "激活会员资格", description = "重新激活指定ID的会员资格")
    public ResponseEntity<?> activateMember(@PathVariable Long id) {
        adminService.activateMember(id);
        return ResponseEntity.ok().build();
    }
}
