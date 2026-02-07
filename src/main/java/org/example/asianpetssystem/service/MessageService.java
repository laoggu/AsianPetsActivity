package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.response.MessageResponse;

import java.util.Map;

public interface MessageService {

    /**
     * 获取会员的消息列表
     */
    PageResponse<MessageResponse> getMemberMessages(Long memberId, PageRequest pageRequest);

    /**
     * 获取未读消息数量
     */
    int getUnreadCount(Long memberId);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId, Long memberId);

    /**
     * 标记所有消息为已读
     */
    void markAllAsRead(Long memberId);

    /**
     * 删除消息
     */
    void deleteMessage(Long messageId, Long memberId);

    /**
     * 获取消息统计
     */
    Map<String, Object> getMessageStats(Long memberId);
}
