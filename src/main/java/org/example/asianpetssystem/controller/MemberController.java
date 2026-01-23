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

    @Autowired
    private MemberService memberService;

    @PostMapping("/apply")
    @Operation(summary = "提交会员申请", description = "新用户提交会员资格申请")
    public ResponseEntity<?> applyForMember(@Valid @RequestBody MemberApplyRequest request) {
        memberService.applyForMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "查询申请状态", description = "根据会员ID查询当前申请状态")
    public ResponseEntity<MemberStatusResponse> getMemberStatus(@PathVariable Long id) {
        MemberStatusResponse response = memberService.getMemberStatus(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "获取个人信息", description = "获取当前登录会员的详细信息")
    public ResponseEntity<ProfileResponse> getMemberProfile(Authentication authentication) {
        String username = authentication.getName();
        ProfileResponse response = memberService.getMemberProfile(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "更新个人信息", description = "更新当前登录会员的个人信息")
    public ResponseEntity<?> updateMemberProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        memberService.updateMemberProfile(request, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    @Operation(summary = "上传附件", description = "上传会员申请所需的相关附件")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("type") AttachmentType type) {
        String fileKey = memberService.uploadFile(file, type);
        return ResponseEntity.ok(new org.example.asianpetssystem.dto.response.UploadResponse(fileKey));
    }
}
