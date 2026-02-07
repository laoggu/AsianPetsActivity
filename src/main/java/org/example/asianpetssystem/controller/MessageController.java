package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.response.MessageResponse;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "消息中心", description = "会员消息管理")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @GetMapping
    @Operation(summary = "获取消息列表", description = "获取当前登录会员的消息列表")
    public ApiResponse<PageResponse<MessageResponse>> getMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        
        return ApiResponse.success(messageService.getMemberMessages(memberId, pageRequest));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取未读消息数量", description = "获取当前登录会员的未读消息数量")
    public ApiResponse<Integer> getUnreadCount() {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return ApiResponse.success(0);
        }
        return ApiResponse.success(messageService.getUnreadCount(memberId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记消息为已读", description = "将指定消息标记为已读")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        messageService.markAsRead(id, memberId);
        return ApiResponse.success(null);
    }

    @PutMapping("/read-all")
    @Operation(summary = "标记所有消息为已读", description = "将所有消息标记为已读")
    public ApiResponse<Void> markAllAsRead() {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        messageService.markAllAsRead(memberId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息", description = "删除指定消息")
    public ApiResponse<Void> deleteMessage(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        messageService.deleteMessage(id, memberId);
        return ApiResponse.success(null);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取消息统计", description = "获取消息统计信息")
    public ApiResponse<Map<String, Object>> getMessageStats() {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(messageService.getMessageStats(memberId));
    }

    private Long getCurrentMemberId() {
        // 这里简化处理，实际应该从token中解析
        return authenticationFacade.getCurrentUserId();
    }
}
