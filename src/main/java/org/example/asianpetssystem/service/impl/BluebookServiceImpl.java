// src/main/java/org/example/asianpetssystem/service/impl/BluebookServiceImpl.java
package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.dto.request.BluebookCreateRequest;
import org.example.asianpetssystem.dto.request.BluebookUpdateRequest;
import org.example.asianpetssystem.dto.response.BluebookResponse;
import org.example.asianpetssystem.entity.Bluebook;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.BluebookRepository;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.BluebookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BluebookServiceImpl implements BluebookService {

    private static final Logger logger = LoggerFactory.getLogger(BluebookServiceImpl.class);

    @Autowired
    private BluebookRepository bluebookRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    public PageResponse<BluebookResponse> getBluebookList(Integer year, String keyword, PageRequest pageRequest) {
        logger.info("开始获取蓝皮书列表 - year={}, keyword={}, page={}, size={}",
                year, keyword, pageRequest.getPage(), pageRequest.getSize());
        long startTime = System.currentTimeMillis();

        try {
            Pageable pageable = createPageable(pageRequest);
            Page<Bluebook> bluebooks;

            if (year != null && StringUtils.hasText(keyword)) {
                // 按年份和关键词筛选
                bluebooks = bluebookRepository.findByYearAndTitleContainingAndIsDeletedFalse(year, keyword, pageable);
            } else if (year != null) {
                // 仅按年份筛选
                bluebooks = bluebookRepository.findByYearAndIsDeletedFalse(year, pageable);
            } else if (StringUtils.hasText(keyword)) {
                // 仅按关键词筛选
                bluebooks = bluebookRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);
            } else {
                // 查询所有
                bluebooks = bluebookRepository.findByIsDeletedFalse(pageable);
            }

            List<BluebookResponse> content = bluebooks.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            PageResponse<BluebookResponse> response = new PageResponse<>();
            response.setContent(content);
            response.setTotalElements(bluebooks.getTotalElements());
            response.setTotalPages(bluebooks.getTotalPages());
            response.setNumber(bluebooks.getNumber());
            response.setSize(bluebooks.getSize());
            response.setFirst(bluebooks.isFirst());
            response.setLast(bluebooks.isLast());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取蓝皮书列表成功 - 返回 {} 条记录, 耗时: {}ms", content.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取蓝皮书列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BluebookResponse getBluebookById(Long id) {
        logger.info("开始获取蓝皮书详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Bluebook bluebook = bluebookRepository.findById(id)
                    .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.BLUEBOOK_NOT_FOUND));

            BluebookResponse response = convertToResponse(bluebook);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取蓝皮书详情成功 - ID={}, 标题: {}, 耗时: {}ms", id, bluebook.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取蓝皮书详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BluebookResponse createBluebook(BluebookCreateRequest request) {
        logger.info("开始创建蓝皮书 - title={}", request.getTitle());
        long startTime = System.currentTimeMillis();

        try {
            Bluebook bluebook = new Bluebook();
            bluebook.setTitle(request.getTitle());
            bluebook.setYear(request.getYear());
            bluebook.setDescription(request.getDescription());
            bluebook.setFileUrl(request.getFileUrl());
            bluebook.setFileSize(request.getFileSize());
            bluebook.setIsMemberOnly(request.getIsMemberOnly());
            bluebook.setStatus("PUBLISHED");
            bluebook.setDownloadCount(0);
            bluebook.setIsDeleted(false);
            bluebook.setCreatedBy(getCurrentUserId());

            Bluebook savedBluebook = bluebookRepository.save(bluebook);
            BluebookResponse response = convertToResponse(savedBluebook);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建蓝皮书成功 - ID={}, 标题: {}, 耗时: {}ms", savedBluebook.getId(), savedBluebook.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建蓝皮书失败 - 标题: {}, 耗时: {}ms, 错误: {}", request.getTitle(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BluebookResponse updateBluebook(Long id, BluebookUpdateRequest request) {
        logger.info("开始更新蓝皮书 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Bluebook bluebook = bluebookRepository.findById(id)
                    .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.BLUEBOOK_NOT_FOUND));

            bluebook.setTitle(request.getTitle());
            bluebook.setYear(request.getYear());
            bluebook.setDescription(request.getDescription());
            bluebook.setFileUrl(request.getFileUrl());
            bluebook.setFileSize(request.getFileSize());
            bluebook.setIsMemberOnly(request.getIsMemberOnly());

            Bluebook updatedBluebook = bluebookRepository.save(bluebook);
            BluebookResponse response = convertToResponse(updatedBluebook);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新蓝皮书成功 - ID={}, 标题: {}, 耗时: {}ms", id, updatedBluebook.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新蓝皮书失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteBluebook(Long id) {
        logger.info("开始删除蓝皮书 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Bluebook bluebook = bluebookRepository.findById(id)
                    .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.BLUEBOOK_NOT_FOUND));

            bluebook.setIsDeleted(true);
            bluebookRepository.save(bluebook);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除蓝皮书成功 - ID={}, 标题: {}, 耗时: {}ms", id, bluebook.getTitle(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除蓝皮书失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void incrementDownloadCount(Long id) {
        logger.info("开始增加蓝皮书下载次数 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Bluebook bluebook = bluebookRepository.findById(id)
                    .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.BLUEBOOK_NOT_FOUND));

            Integer currentCount = bluebook.getDownloadCount();
            bluebook.setDownloadCount(currentCount != null ? currentCount + 1 : 1);
            bluebookRepository.save(bluebook);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("增加蓝皮书下载次数成功 - ID={}, 新下载次数: {}, 耗时: {}ms", id, bluebook.getDownloadCount(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("增加蓝皮书下载次数失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Object> getDownloadStats(Long id) {
        logger.info("开始获取蓝皮书下载统计 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Bluebook bluebook = bluebookRepository.findById(id)
                    .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.BLUEBOOK_NOT_FOUND));

            Map<String, Object> stats = new HashMap<>();
            stats.put("id", bluebook.getId());
            stats.put("title", bluebook.getTitle());
            stats.put("downloadCount", bluebook.getDownloadCount());
            stats.put("year", bluebook.getYear());

            // 计算总下载量和排名
            long totalDownloads = bluebookRepository.findByIsDeletedFalse(org.springframework.data.domain.Pageable.unpaged())
                    .getContent().stream()
                    .mapToInt(b -> b.getDownloadCount() != null ? b.getDownloadCount() : 0)
                    .sum();
            stats.put("totalDownloads", totalDownloads);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取蓝皮书下载统计成功 - ID={}, 耗时: {}ms", id, duration);
            return stats;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取蓝皮书下载统计失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 创建分页对象
     */
    private Pageable createPageable(PageRequest pageRequest) {
        int page = pageRequest.getPage() != null ? pageRequest.getPage() : 0;
        int size = pageRequest.getSize() != null ? pageRequest.getSize() : 20;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (StringUtils.hasText(pageRequest.getSortBy())) {
            Sort.Direction direction = "asc".equalsIgnoreCase(pageRequest.getSortDirection())
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, pageRequest.getSortBy());
        }

        return org.springframework.data.domain.PageRequest.of(page, size, sort);
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
     * 转换为响应对象
     */
    private BluebookResponse convertToResponse(Bluebook bluebook) {
        BluebookResponse response = new BluebookResponse();
        response.setId(bluebook.getId());
        response.setTitle(bluebook.getTitle());
        response.setYear(bluebook.getYear());
        response.setDescription(bluebook.getDescription());
        response.setFileUrl(bluebook.getFileUrl());
        response.setFileSize(bluebook.getFileSize());
        response.setDownloadCount(bluebook.getDownloadCount());
        response.setStatus(bluebook.getStatus());
        response.setIsMemberOnly(bluebook.getIsMemberOnly());
        response.setCreatedAt(bluebook.getCreatedAt());
        return response;
    }
}
