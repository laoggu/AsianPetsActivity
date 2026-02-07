package org.example.asianpetssystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.dto.request.AuditConfigRequest;
import org.example.asianpetssystem.dto.request.SystemConfigUpdateRequest;
import org.example.asianpetssystem.dto.response.AuditConfigResponse;
import org.example.asianpetssystem.dto.response.SystemConfigDetailResponse;
import org.example.asianpetssystem.entity.AuditConfig;
import org.example.asianpetssystem.entity.SystemConfig;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.AuditConfigRepository;
import org.example.asianpetssystem.repository.SystemConfigRepository;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private AuditConfigRepository auditConfigRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public SystemConfigDetailResponse getConfig(String key) {
        logger.info("开始获取系统配置 - key={}", key);
        long startTime = System.currentTimeMillis();

        try {
            SystemConfig config = systemConfigRepository.findByConfigKey(key)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.CONFIG_NOT_FOUND));

            SystemConfigDetailResponse response = convertToConfigResponse(config);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取系统配置成功 - key={}, 耗时: {}ms", key, duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取系统配置失败 - key={}, 耗时: {}ms, 错误: {}", key, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfigDetailResponse> getConfigsByPrefix(String prefix) {
        logger.info("开始根据前缀获取配置列表 - prefix={}", prefix);
        long startTime = System.currentTimeMillis();

        try {
            List<SystemConfig> configs = systemConfigRepository.findByConfigKeyStartingWith(prefix);

            List<SystemConfigDetailResponse> response = configs.stream()
                    .map(this::convertToConfigResponse)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("根据前缀获取配置列表成功 - prefix={}, 返回 {} 条记录, 耗时: {}ms", prefix, response.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("根据前缀获取配置列表失败 - prefix={}, 耗时: {}ms, 错误: {}", prefix, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SystemConfigDetailResponse updateConfig(SystemConfigUpdateRequest request) {
        logger.info("开始更新系统配置 - key={}", request.getConfigKey());
        long startTime = System.currentTimeMillis();

        try {
            SystemConfig config = systemConfigRepository.findByConfigKey(request.getConfigKey())
                    .orElseGet(() -> {
                        SystemConfig newConfig = new SystemConfig();
                        newConfig.setConfigKey(request.getConfigKey());
                        return newConfig;
                    });

            if (StringUtils.hasText(request.getConfigValue())) {
                config.setConfigValue(request.getConfigValue());
            }
            if (StringUtils.hasText(request.getDescription())) {
                config.setDescription(request.getDescription());
            }
            config.setUpdatedBy(getCurrentUserId());
            config.setUpdatedAt(LocalDateTime.now());

            SystemConfig savedConfig = systemConfigRepository.save(config);
            SystemConfigDetailResponse response = convertToConfigResponse(savedConfig);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新系统配置成功 - key={}, 耗时: {}ms", request.getConfigKey(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新系统配置失败 - key={}, 耗时: {}ms, 错误: {}", request.getConfigKey(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateConfigsBatch(List<SystemConfigUpdateRequest> configs) {
        logger.info("开始批量更新配置 - 数量: {}", configs != null ? configs.size() : 0);
        long startTime = System.currentTimeMillis();

        try {
            if (configs == null || configs.isEmpty()) {
                logger.warn("批量更新配置列表为空");
                return;
            }

            for (SystemConfigUpdateRequest request : configs) {
                updateConfig(request);
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.info("批量更新配置成功 - 数量: {}, 耗时: {}ms", configs.size(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("批量更新配置失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditConfigResponse getAuditConfig() {
        logger.info("开始获取审核配置");
        long startTime = System.currentTimeMillis();

        try {
            AuditConfig auditConfig = auditConfigRepository.findFirstByOrderByIdDesc()
                    .orElseGet(() -> {
                        AuditConfig defaultConfig = new AuditConfig();
                        defaultConfig.setAutoAudit(false);
                        defaultConfig.setRequireMaterials("[]");
                        defaultConfig.setAuditFlow("[]");
                        return defaultConfig;
                    });

            AuditConfigResponse response = convertToAuditConfigResponse(auditConfig);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取审核配置成功 - 耗时: {}ms", duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取审核配置失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AuditConfigResponse updateAuditConfig(AuditConfigRequest request) {
        logger.info("开始更新审核配置 - autoAudit={}", request.getAutoAudit());
        long startTime = System.currentTimeMillis();

        try {
            AuditConfig auditConfig = auditConfigRepository.findFirstByOrderByIdDesc()
                    .orElseGet(AuditConfig::new);

            if (request.getAutoAudit() != null) {
                auditConfig.setAutoAudit(request.getAutoAudit());
            }
            if (request.getRequireMaterials() != null) {
                auditConfig.setRequireMaterials(objectMapper.writeValueAsString(request.getRequireMaterials()));
            }
            if (request.getAuditFlow() != null) {
                auditConfig.setAuditFlow(objectMapper.writeValueAsString(request.getAuditFlow()));
            }
            auditConfig.setUpdatedBy(getCurrentUserId());
            auditConfig.setUpdatedAt(LocalDateTime.now());

            AuditConfig savedConfig = auditConfigRepository.save(auditConfig);
            AuditConfigResponse response = convertToAuditConfigResponse(savedConfig);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新审核配置成功 - ID={}, 耗时: {}ms", savedConfig.getId(), duration);
            return response;
        } catch (JsonProcessingException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新审核配置失败 - JSON转换错误, 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw new BusinessException(BusinessErrorEnum.SYSTEM_ERROR, "审核配置JSON转换失败");
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新审核配置失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        String username = authenticationFacade.getCurrentUsername();
        // 简化处理，实际应根据username查询用户ID
        return username != null ? 1L : null;
    }

    /**
     * 转换为系统配置响应对象
     */
    private SystemConfigDetailResponse convertToConfigResponse(SystemConfig config) {
        SystemConfigDetailResponse response = new SystemConfigDetailResponse();
        response.setId(config.getId());
        response.setConfigKey(config.getConfigKey());
        response.setConfigValue(config.getConfigValue());
        response.setDescription(config.getDescription());
        response.setUpdatedAt(config.getUpdatedAt());
        return response;
    }

    /**
     * 转换为审核配置响应对象
     */
    private AuditConfigResponse convertToAuditConfigResponse(AuditConfig auditConfig) {
        AuditConfigResponse response = new AuditConfigResponse();
        response.setId(auditConfig.getId());
        response.setAutoAudit(auditConfig.getAutoAudit());
        response.setUpdatedAt(auditConfig.getUpdatedAt());

        // 解析必填材料JSON
        try {
            if (StringUtils.hasText(auditConfig.getRequireMaterials())) {
                List<String> materials = objectMapper.readValue(auditConfig.getRequireMaterials(),
                        new TypeReference<List<String>>() {});
                response.setRequireMaterials(materials);
            }
        } catch (JsonProcessingException e) {
            logger.warn("解析必填材料JSON失败: {}", e.getMessage());
            response.setRequireMaterials(null);
        }

        // 解析审核流程JSON
        try {
            if (StringUtils.hasText(auditConfig.getAuditFlow())) {
                List<AuditConfigRequest.FlowStep> steps = objectMapper.readValue(auditConfig.getAuditFlow(),
                        new TypeReference<List<AuditConfigRequest.FlowStep>>() {});
                List<AuditConfigResponse.FlowStepResponse> stepResponses = steps.stream()
                        .map(step -> {
                            AuditConfigResponse.FlowStepResponse stepResponse = new AuditConfigResponse.FlowStepResponse();
                            stepResponse.setStep(step.getStep());
                            stepResponse.setName(step.getName());
                            stepResponse.setDescription(step.getDescription());
                            return stepResponse;
                        })
                        .collect(Collectors.toList());
                response.setAuditFlow(stepResponses);
            }
        } catch (JsonProcessingException e) {
            logger.warn("解析审核流程JSON失败: {}", e.getMessage());
            response.setAuditFlow(null);
        }

        return response;
    }
}
