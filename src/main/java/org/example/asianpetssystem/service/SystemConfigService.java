package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.request.AuditConfigRequest;
import org.example.asianpetssystem.dto.request.SystemConfigUpdateRequest;
import org.example.asianpetssystem.dto.response.AuditConfigResponse;
import org.example.asianpetssystem.dto.response.SystemConfigDetailResponse;

import java.util.List;

public interface SystemConfigService {

    /**
     * 获取系统配置
     *
     * @param key 配置键
     * @return 配置响应
     */
    SystemConfigDetailResponse getConfig(String key);

    /**
     * 根据前缀获取配置列表
     *
     * @param prefix 配置键前缀
     * @return 配置列表
     */
    List<SystemConfigDetailResponse> getConfigsByPrefix(String prefix);

    /**
     * 更新系统配置
     *
     * @param request 更新请求
     * @return 更新后的配置响应
     */
    SystemConfigDetailResponse updateConfig(SystemConfigUpdateRequest request);

    /**
     * 批量更新配置
     *
     * @param configs 配置列表
     */
    void updateConfigsBatch(List<SystemConfigUpdateRequest> configs);

    /**
     * 获取审核配置
     *
     * @return 审核配置响应
     */
    AuditConfigResponse getAuditConfig();

    /**
     * 更新审核配置
     *
     * @param request 更新请求
     * @return 更新后的审核配置响应
     */
    AuditConfigResponse updateAuditConfig(AuditConfigRequest request);
}
