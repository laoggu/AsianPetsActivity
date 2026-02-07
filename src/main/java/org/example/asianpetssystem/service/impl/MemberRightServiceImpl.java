package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.common.enums.MemberLevel;
import org.example.asianpetssystem.dto.request.MemberRightCreateRequest;
import org.example.asianpetssystem.dto.request.MemberRightUpdateRequest;
import org.example.asianpetssystem.dto.response.LevelRightsResponse;
import org.example.asianpetssystem.dto.response.MemberRightResponse;
import org.example.asianpetssystem.entity.MemberRight;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.MemberRightRepository;
import org.example.asianpetssystem.service.MemberRightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberRightServiceImpl implements MemberRightService {

    private static final Logger logger = LoggerFactory.getLogger(MemberRightServiceImpl.class);

    @Autowired
    private MemberRightRepository memberRightRepository;

    @Override
    public List<LevelRightsResponse> getRightsByLevel() {
        logger.info("开始获取按等级分组的权益列表");
        long startTime = System.currentTimeMillis();

        try {
            List<MemberRight> rights = memberRightRepository.findByIsActiveTrueOrderByLevelAscSortOrderAsc();

            // 按等级分组
            Map<String, List<MemberRight>> rightsByLevel = rights.stream()
                    .collect(Collectors.groupingBy(MemberRight::getLevel));

            // 构建响应
            List<LevelRightsResponse> result = new ArrayList<>();

            // 确保按照等级顺序返回（REGULAR -> GOLD -> PLATINUM）
            List<String> levelOrder = Arrays.asList("REGULAR", "GOLD", "PLATINUM");

            for (String level : levelOrder) {
                List<MemberRight> levelRights = rightsByLevel.getOrDefault(level, new ArrayList<>());

                LevelRightsResponse response = new LevelRightsResponse();
                response.setLevel(level);
                response.setLevelName(getLevelName(level));
                response.setRights(levelRights.stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList()));

                result.add(response);
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取按等级分组的权益列表成功 - 返回 {} 个等级, 耗时: {}ms", result.size(), duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取按等级分组的权益列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<MemberRightResponse> getRightsByLevel(String level) {
        logger.info("开始获取等级权益 - level={}", level);
        long startTime = System.currentTimeMillis();

        try {
            List<MemberRight> rights = memberRightRepository.findByLevelAndIsActiveTrueOrderBySortOrderAsc(level);

            List<MemberRightResponse> result = rights.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取等级权益成功 - level={}, 返回 {} 条记录, 耗时: {}ms", level, result.size(), duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取等级权益失败 - level={}, 耗时: {}ms, 错误: {}", level, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MemberRightResponse createRight(MemberRightCreateRequest request) {
        logger.info("开始创建权益 - title={}, level={}", request.getTitle(), request.getLevel());
        long startTime = System.currentTimeMillis();

        try {
            MemberRight right = new MemberRight();
            right.setLevel(request.getLevel());
            right.setTitle(request.getTitle());
            right.setDescription(request.getDescription());
            right.setIcon(request.getIcon());
            right.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
            right.setIsActive(true);

            MemberRight savedRight = memberRightRepository.save(right);
            MemberRightResponse response = convertToResponse(savedRight);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建权益成功 - ID={}, 标题: {}, 耗时: {}ms", savedRight.getId(), savedRight.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建权益失败 - 标题: {}, 耗时: {}ms, 错误: {}", request.getTitle(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MemberRightResponse updateRight(Long id, MemberRightUpdateRequest request) {
        logger.info("开始更新权益 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            MemberRight right = memberRightRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.RIGHT_NOT_FOUND));

            right.setLevel(request.getLevel());
            right.setTitle(request.getTitle());
            right.setDescription(request.getDescription());
            right.setIcon(request.getIcon());
            if (request.getSortOrder() != null) {
                right.setSortOrder(request.getSortOrder());
            }
            if (request.getIsActive() != null) {
                right.setIsActive(request.getIsActive());
            }

            MemberRight updatedRight = memberRightRepository.save(right);
            MemberRightResponse response = convertToResponse(updatedRight);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新权益成功 - ID={}, 标题: {}, 耗时: {}ms", id, updatedRight.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新权益失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteRight(Long id) {
        logger.info("开始删除权益 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            MemberRight right = memberRightRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.RIGHT_NOT_FOUND));

            memberRightRepository.delete(right);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除权益成功 - ID={}, 标题: {}, 耗时: {}ms", id, right.getTitle(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除权益失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateLevelRights(String level, List<Long> rightIds) {
        logger.info("开始更新等级权益配置 - level={}, rightIds={}", level, rightIds);
        long startTime = System.currentTimeMillis();

        try {
            // 先将该等级下所有权益置为禁用
            List<MemberRight> existingRights = memberRightRepository.findByLevel(level);
            for (MemberRight right : existingRights) {
                right.setIsActive(false);
            }
            memberRightRepository.saveAll(existingRights);

            // 启用指定的权益
            if (rightIds != null && !rightIds.isEmpty()) {
                for (Long rightId : rightIds) {
                    MemberRight right = memberRightRepository.findById(rightId).orElse(null);
                    if (right != null) {
                        right.setLevel(level);
                        right.setIsActive(true);
                        memberRightRepository.save(right);
                    }
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新等级权益配置成功 - level={}, 耗时: {}ms", level, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新等级权益配置失败 - level={}, 耗时: {}ms, 错误: {}", level, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取等级名称
     */
    private String getLevelName(String level) {
        try {
            return MemberLevel.valueOf(level).getDescription();
        } catch (IllegalArgumentException e) {
            return level;
        }
    }

    /**
     * 转换为响应对象
     */
    private MemberRightResponse convertToResponse(MemberRight right) {
        MemberRightResponse response = new MemberRightResponse();
        response.setId(right.getId());
        response.setLevel(right.getLevel());
        response.setTitle(right.getTitle());
        response.setDescription(right.getDescription());
        response.setIcon(right.getIcon());
        response.setSortOrder(right.getSortOrder());
        response.setIsActive(right.getIsActive());
        response.setCreatedAt(right.getCreatedAt());
        response.setUpdatedAt(right.getUpdatedAt());
        return response;
    }
}
