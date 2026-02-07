package org.example.asianpetssystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.response.OperationLogResponse;
import org.example.asianpetssystem.entity.AuditLog;
import org.example.asianpetssystem.entity.AuditAction;
import org.example.asianpetssystem.repository.AuditLogRepository;
import org.example.asianpetssystem.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OperationLogServiceImpl implements OperationLogService {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogServiceImpl.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void log(String module, String action, String description, 
                    Long operatorId, String operatorName, 
                    Object requestData, Object responseData, 
                    String ipAddress) {
        try {
            AuditLog log = new AuditLog();
            log.setMemberId(operatorId != null ? operatorId : 0L);
            log.setOperatorId(operatorId != null ? operatorId : 0L);
            
            // 将操作类型映射到AuditAction
            AuditAction auditAction = mapActionToAuditAction(action);
            log.setAction(auditAction);
            
            log.setRemark(description);
            log.setCreatedAt(LocalDateTime.now());
            
            // 这里可以扩展AuditLog实体添加更多字段
            // 如：module, requestData, responseData, ipAddress等
            
            auditLogRepository.save(log);
            
            logger.debug("操作日志记录成功 - module={}, action={}", module, action);
        } catch (Exception e) {
            logger.error("操作日志记录失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperationLogResponse> getLogList(String module, String action, 
                                                         Long operatorId, PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(), pageRequest.getSize());
        
        // 简化查询，实际应该添加更多筛选条件
        Page<AuditLog> page = auditLogRepository.findAll(pageable);
        
        List<OperationLogResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResponse<OperationLogResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OperationLogResponse getLogById(Long id) {
        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("日志不存在"));
        return convertToResponse(log);
    }

    private AuditAction mapActionToAuditAction(String action) {
        try {
            return AuditAction.valueOf(action.toUpperCase());
        } catch (Exception e) {
            return AuditAction.VIEW;
        }
    }

    private OperationLogResponse convertToResponse(AuditLog log) {
        OperationLogResponse response = new OperationLogResponse();
        response.setId(log.getId());
        response.setMemberId(log.getMemberId());
        response.setOperatorId(log.getOperatorId());
        response.setAction(log.getAction().name());
        response.setRemark(log.getRemark());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }
}
