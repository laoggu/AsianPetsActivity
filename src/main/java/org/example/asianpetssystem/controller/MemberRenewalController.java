package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.RenewalCreateRequest;
import org.example.asianpetssystem.dto.request.RenewalPaymentRequest;
import org.example.asianpetssystem.dto.response.RenewalResponse;
import org.example.asianpetssystem.service.MemberRenewalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/renewals")
@Tag(name = "会员续费管理", description = "会员续费记录管理")
@PreAuthorize("hasRole('ADMIN')")
public class MemberRenewalController {

    private static final Logger logger = LoggerFactory.getLogger(MemberRenewalController.class);

    @Autowired
    private MemberRenewalService renewalService;

    @PostMapping
    @Operation(summary = "创建续费记录", description = "为会员创建续费记录")
    public ApiResponse<RenewalResponse> createRenewal(@RequestBody RenewalCreateRequest request) {
        logger.info("创建续费记录 - memberId={}", request.getMemberId());
        return ApiResponse.success(renewalService.createRenewal(request));
    }

    @GetMapping
    @Operation(summary = "获取续费列表", description = "分页获取续费记录列表")
    public ApiResponse<PageResponse<RenewalResponse>> getRenewalList(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        return ApiResponse.success(renewalService.getRenewalList(memberId, status, pageRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取续费详情", description = "根据ID获取续费记录详情")
    public ApiResponse<RenewalResponse> getRenewalById(@PathVariable Long id) {
        return ApiResponse.success(renewalService.getRenewalById(id));
    }

    @PutMapping("/{id}/payment")
    @Operation(summary = "处理续费支付", description = "处理续费记录的支付")
    public ApiResponse<RenewalResponse> processPayment(
            @PathVariable Long id,
            @RequestBody RenewalPaymentRequest request) {
        logger.info("处理续费支付 - renewalId={}", id);
        return ApiResponse.success(renewalService.processPayment(id, request));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消续费", description = "取消待支付的续费记录")
    public ApiResponse<Void> cancelRenewal(@PathVariable Long id) {
        renewalService.cancelRenewal(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "获取会员续费记录", description = "获取指定会员的所有续费记录")
    public ApiResponse<List<RenewalResponse>> getMemberRenewals(@PathVariable Long memberId) {
        return ApiResponse.success(renewalService.getMemberRenewals(memberId));
    }
}
