package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.request.BatchMessageRequest;
import org.example.asianpetssystem.dto.request.BatchUpdateRequest;

import java.util.List;
import java.util.Map;

public interface BatchOperationService {

    /**
     * 批量更新会员状态
     */
    Map<String, Object> batchUpdateMemberStatus(BatchUpdateRequest request);

    /**
     * 批量发送消息
     */
    Map<String, Object> batchSendMessage(BatchMessageRequest request);

    /**
     * 批量删除
     */
    Map<String, Object> batchDelete(String entityType, List<Long> ids);
}
