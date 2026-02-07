package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.response.OperationLogResponse;

/**
 * 操作日志服务
 */
public interface OperationLogService {

    /**
     * 记录操作日志
     * @param module 模块
     * @param action 操作
     * @param description 描述
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param requestData 请求数据
     * @param responseData 响应数据
     * @param ipAddress IP地址
     */
    void log(String module, String action, String description, 
             Long operatorId, String operatorName, 
             Object requestData, Object responseData, 
             String ipAddress);

    /**
     * 获取操作日志列表
     */
    PageResponse<OperationLogResponse> getLogList(String module, String action, 
                                                   Long operatorId, PageRequest pageRequest);

    /**
     * 获取操作日志详情
     */
    OperationLogResponse getLogById(Long id);
}
