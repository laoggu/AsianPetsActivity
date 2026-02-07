package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.response.MessageResponse;
import org.example.asianpetssystem.entity.Message;
import org.example.asianpetssystem.entity.MessageRecipient;
import org.example.asianpetssystem.repository.MessageRecipientRepository;
import org.example.asianpetssystem.repository.MessageRepository;
import org.example.asianpetssystem.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageRecipientRepository recipientRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> getMemberMessages(Long memberId, PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(), pageRequest.getSize());
        
        Page<MessageRecipient> page = recipientRepository.findByMemberId(memberId, pageable);
        
        List<MessageResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResponse<MessageResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        return (int) recipientRepository.countUnreadByMemberId(memberId);
    }

    @Override
    public void markAsRead(Long messageId, Long memberId) {
        MessageRecipient recipient = recipientRepository
                .findByMessageIdAndMemberId(messageId, memberId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        recipient.setStatus("READ");
        recipient.setReadTime(LocalDateTime.now());
        recipientRepository.save(recipient);
    }

    @Override
    public void markAllAsRead(Long memberId) {
        List<MessageRecipient> unreadMessages = recipientRepository
                .findByMemberIdAndStatus(memberId, "UNREAD");
        
        for (MessageRecipient recipient : unreadMessages) {
            recipient.setStatus("READ");
            recipient.setReadTime(LocalDateTime.now());
        }
        recipientRepository.saveAll(unreadMessages);
    }

    @Override
    public void deleteMessage(Long messageId, Long memberId) {
        MessageRecipient recipient = recipientRepository
                .findByMessageIdAndMemberId(messageId, memberId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        recipientRepository.delete(recipient);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMessageStats(Long memberId) {
        Map<String, Object> stats = new HashMap<>();
        
        long total = recipientRepository.countByMemberId(memberId);
        long unread = recipientRepository.countUnreadByMemberId(memberId);
        long read = total - unread;
        
        stats.put("total", total);
        stats.put("unread", unread);
        stats.put("read", read);
        
        return stats;
    }

    private MessageResponse convertToResponse(MessageRecipient recipient) {
        MessageResponse response = new MessageResponse();
        response.setId(recipient.getMessage().getId());
        response.setTitle(recipient.getMessage().getTitle());
        response.setContent(recipient.getMessage().getContent());
        response.setType(recipient.getMessage().getType());
        response.setStatus(recipient.getStatus());
        response.setSentTime(recipient.getMessage().getSentTime());
        response.setReadTime(recipient.getReadTime());
        return response;
    }
}
