package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.enums.MemberStatus;
import org.example.asianpetssystem.dto.request.BatchMessageRequest;
import org.example.asianpetssystem.dto.request.BatchUpdateRequest;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.entity.Message;
import org.example.asianpetssystem.entity.MessageRecipient;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.repository.MessageRecipientRepository;
import org.example.asianpetssystem.repository.MessageRepository;
import org.example.asianpetssystem.service.BatchOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class BatchOperationServiceImpl implements BatchOperationService {

    private static final Logger logger = LoggerFactory.getLogger(BatchOperationServiceImpl.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageRecipientRepository messageRecipientRepository;

    @Override
    public Map<String, Object> batchUpdateMemberStatus(BatchUpdateRequest request) {
        logger.info("批量更新会员状态 - ids={}, status={}", request.getIds(), request.getStatus());
        
        int successCount = 0;
        int failCount = 0;

        MemberStatus status = MemberStatus.valueOf(request.getStatus());

        for (Long memberId : request.getIds()) {
            try {
                Member member = memberRepository.findById(memberId).orElse(null);
                if (member != null) {
                    member.setStatus(status);
                    memberRepository.save(member);
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                logger.error("更新会员状态失败 - memberId={}", memberId, e);
                failCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("totalCount", request.getIds().size());
        return result;
    }

    @Override
    public Map<String, Object> batchSendMessage(BatchMessageRequest request) {
        logger.info("批量发送消息 - memberIds count={}", request.getMemberIds().size());

        // 创建消息
        Message message = new Message();
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setType(request.getType());
        message.setSendType("IMMEDIATE");
        message.setStatus("SENT");
        message.setSentTime(LocalDateTime.now());
        message.setTotalCount(request.getMemberIds().size());
        message.setSuccessCount(request.getMemberIds().size());
        message.setFailCount(0);
        message.setCreatedAt(LocalDateTime.now());

        messageRepository.save(message);

        // 创建接收人记录
        int successCount = 0;
        for (Long memberId : request.getMemberIds()) {
            try {
                MessageRecipient recipient = new MessageRecipient();
                recipient.setMessageId(message.getId());
                recipient.setMemberId(memberId);
                recipient.setStatus("UNREAD");
                messageRecipientRepository.save(recipient);
                successCount++;
            } catch (Exception e) {
                logger.error("创建消息接收人失败 - memberId={}", memberId, e);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("messageId", message.getId());
        result.put("successCount", successCount);
        result.put("totalCount", request.getMemberIds().size());
        return result;
    }

    @Override
    public Map<String, Object> batchDelete(String entityType, java.util.List<Long> ids) {
        logger.info("批量删除 - entityType={}, ids={}", entityType, ids);

        int successCount = 0;

        switch (entityType) {
            case "member":
                for (Long id : ids) {
                    try {
                        memberRepository.deleteById(id);
                        successCount++;
                    } catch (Exception e) {
                        logger.error("删除会员失败 - id={}", id, e);
                    }
                }
                break;
            // 可以添加其他实体的批量删除
            default:
                logger.warn("不支持的实体类型 - {}", entityType);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("totalCount", ids.size());
        return result;
    }
}
