// src/main/java/org/example\asianpetssystem\controller\MemberController.java
package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.asianpetssystem.dto.request.MemberApplyRequest;
import org.example.asianpetssystem.dto.request.UpdateProfileRequest;
import org.example.asianpetssystem.dto.response.MemberStatusResponse;
import org.example.asianpetssystem.dto.response.ProfileResponse;
import org.example.asianpetssystem.entity.AttachmentType;
import org.example.asianpetssystem.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/member")
@Tag(name = "会员管理", description = "会员申请、个人中心等相关接口")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @PostMapping("/apply")
    @Operation(summary = "提交会员申请", description = "新用户提交会员资格申请")
    public ResponseEntity<?> applyForMember(@Valid @RequestBody MemberApplyRequest request) {
        logger.info("开始处理会员申请 - 公司名称: {}, 统一信用代码: {}", 
                   request.getCompanyName(), request.getCreditCode());
        long startTime = System.currentTimeMillis();
        
        try {
            memberService.applyForMember(request);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员申请提交成功 - 公司名称: {}, 耗时: {}ms", request.getCompanyName(), duration);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员申请提交失败 - 公司名称: {}, 耗时: {}ms, 错误: {}", 
                        request.getCompanyName(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "查询申请状态", description = "根据会员ID查询当前申请状态")
    public ResponseEntity<MemberStatusResponse> getMemberStatus(@PathVariable Long id) {
        logger.info("开始查询会员申请状态 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            MemberStatusResponse response = memberService.getMemberStatus(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员申请状态查询成功 - ID={}, 状态: {}, 耗时: {}ms", 
                       id, response.getStatus(), duration);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员申请状态查询失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "获取个人信息", description = "获取当前登录会员的详细信息")
    public ResponseEntity<ProfileResponse> getMemberProfile(Authentication authentication) {
        String username = authentication.getName();
        logger.info("开始获取会员个人信息 - 用户名: {}", username);
        long startTime = System.currentTimeMillis();
        
        try {
            ProfileResponse response = memberService.getMemberProfile(username);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员个人信息获取成功 - 用户名: {}, 公司: {}, 耗时: {}ms", 
                       username, response.getCompanyName(), duration);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员个人信息获取失败 - 用户名: {}, 耗时: {}ms, 错误: {}", 
                        username, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "更新个人信息", description = "更新当前登录会员的个人信息")
    public ResponseEntity<?> updateMemberProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        logger.info("开始更新会员个人信息 - 用户名: {}", username);
        long startTime = System.currentTimeMillis();
        
        try {
            memberService.updateMemberProfile(request, username);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员个人信息更新成功 - 用户名: {}, 耗时: {}ms", username, duration);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员个人信息更新失败 - 用户名: {}, 耗时: {}ms, 错误: {}", 
                        username, duration, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/upload")
    @Operation(summary = "上传附件", description = "上传会员申请所需的相关附件")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("type") AttachmentType type) {
        logger.info("开始上传附件 - 文件名: {}, 类型: {}, 大小: {} bytes", 
                   file.getOriginalFilename(), type, file.getSize());
        long startTime = System.currentTimeMillis();
        
        try {
            String fileKey = memberService.uploadFile(file, type);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("附件上传成功 - 文件名: {}, 存储路径: {}, 耗时: {}ms", 
                       file.getOriginalFilename(), fileKey, duration);
            return ResponseEntity.ok(new org.example.asianpetssystem.dto.response.UploadResponse(fileKey));
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("附件上传失败 - 文件名: {}, 类型: {}, 耗时: {}ms, 错误: {}", 
                        file.getOriginalFilename(), type, duration, e.getMessage(), e);
            throw e;
        }
    }
}
