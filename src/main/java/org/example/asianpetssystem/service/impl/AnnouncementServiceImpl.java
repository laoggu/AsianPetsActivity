// src/main/java/org/example/asianpetssystem/service/impl/AnnouncementServiceImpl.java
package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.dto.request.AnnouncementCreateRequest;
import org.example.asianpetssystem.dto.request.AnnouncementUpdateRequest;
import org.example.asianpetssystem.dto.response.AnnouncementResponse;
import org.example.asianpetssystem.entity.Announcement;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.AnnouncementRepository;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.AnnouncementService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    public PageResponse<AnnouncementResponse> getAnnouncementList(String type, PageRequest pageRequest) {
        logger.info("开始获取公告列表 - type={}, page={}, size={}",
                type, pageRequest.getPage(), pageRequest.getSize());
        long startTime = System.currentTimeMillis();

        try {
            Pageable pageable = createPageable(pageRequest);
            Page<Announcement> announcements;

            if (StringUtils.hasText(type)) {
                announcements = announcementRepository.findByTypeAndIsDeletedFalse(type, pageable);
            } else {
                announcements = announcementRepository.findByIsDeletedFalse(pageable);
            }

            List<AnnouncementResponse> content = announcements.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            PageResponse<AnnouncementResponse> response = new PageResponse<>();
            response.setContent(content);
            response.setTotalElements(announcements.getTotalElements());
            response.setTotalPages(announcements.getTotalPages());
            response.setNumber(announcements.getNumber());
            response.setSize(announcements.getSize());
            response.setFirst(announcements.isFirst());
            response.setLast(announcements.isLast());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取公告列表成功 - 返回 {} 条记录, 耗时: {}ms", content.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取公告列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AnnouncementResponse> getTopAnnouncements() {
        logger.info("开始获取置顶公告列表");
        long startTime = System.currentTimeMillis();

        try {
            List<Announcement> announcements = announcementRepository
                    .findByIsTopTrueAndStatusAndIsDeletedFalseOrderByTopOrderDesc("PUBLISHED");

            List<AnnouncementResponse> result = announcements.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取置顶公告列表成功 - 返回 {} 条记录, 耗时: {}ms", result.size(), duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取置顶公告列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AnnouncementResponse getAnnouncementById(Long id) {
        logger.info("开始获取公告详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Announcement announcement = announcementRepository.findById(id)
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ANNOUNCEMENT_NOT_FOUND));

            // 增加浏览次数
            Integer currentCount = announcement.getViewCount();
            announcement.setViewCount(currentCount != null ? currentCount + 1 : 1);
            announcementRepository.save(announcement);

            AnnouncementResponse response = convertToResponse(announcement);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取公告详情成功 - ID={}, 标题: {}, 耗时: {}ms", id, announcement.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取公告详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AnnouncementResponse createAnnouncement(AnnouncementCreateRequest request) {
        logger.info("开始创建公告 - title={}", request.getTitle());
        long startTime = System.currentTimeMillis();

        try {
            Announcement announcement = new Announcement();
            announcement.setTitle(request.getTitle());
            announcement.setContent(request.getContent());
            announcement.setType(request.getType());
            announcement.setIsTop(request.getIsTop() != null ? request.getIsTop() : false);
            announcement.setStatus("PUBLISHED");
            announcement.setViewCount(0);
            announcement.setIsDeleted(false);
            announcement.setPublishTime(LocalDateTime.now());

            // 如果置顶，设置排序值
            if (Boolean.TRUE.equals(announcement.getIsTop())) {
                announcement.setTopOrder(getNextTopOrder());
            }

            announcement.setCreatedBy(getCurrentUserId());

            Announcement savedAnnouncement = announcementRepository.save(announcement);
            AnnouncementResponse response = convertToResponse(savedAnnouncement);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建公告成功 - ID={}, 标题: {}, 耗时: {}ms", savedAnnouncement.getId(), savedAnnouncement.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建公告失败 - 标题: {}, 耗时: {}ms, 错误: {}", request.getTitle(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementUpdateRequest request) {
        logger.info("开始更新公告 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Announcement announcement = announcementRepository.findById(id)
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ANNOUNCEMENT_NOT_FOUND));

            announcement.setTitle(request.getTitle());
            announcement.setContent(request.getContent());
            announcement.setType(request.getType());

            // 处理置顶状态变化
            Boolean newIsTop = request.getIsTop() != null ? request.getIsTop() : false;
            Boolean oldIsTop = announcement.getIsTop() != null ? announcement.getIsTop() : false;

            if (!oldIsTop && newIsTop) {
                // 从未置顶变为置顶
                announcement.setIsTop(true);
                announcement.setTopOrder(getNextTopOrder());
            } else if (oldIsTop && !newIsTop) {
                // 从置顶变为未置顶
                announcement.setIsTop(false);
                announcement.setTopOrder(null);
            }

            Announcement updatedAnnouncement = announcementRepository.save(announcement);
            AnnouncementResponse response = convertToResponse(updatedAnnouncement);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新公告成功 - ID={}, 标题: {}, 耗时: {}ms", id, updatedAnnouncement.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新公告失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAnnouncement(Long id) {
        logger.info("开始删除公告 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Announcement announcement = announcementRepository.findById(id)
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ANNOUNCEMENT_NOT_FOUND));

            announcement.setIsDeleted(true);
            announcementRepository.save(announcement);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除公告成功 - ID={}, 标题: {}, 耗时: {}ms", id, announcement.getTitle(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除公告失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void topAnnouncement(Long id, Boolean isTop) {
        logger.info("开始{}公告 - ID={}", isTop ? "置顶" : "取消置顶", id);
        long startTime = System.currentTimeMillis();

        try {
            Announcement announcement = announcementRepository.findById(id)
                    .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ANNOUNCEMENT_NOT_FOUND));

            if (Boolean.TRUE.equals(isTop)) {
                announcement.setIsTop(true);
                announcement.setTopOrder(getNextTopOrder());
            } else {
                announcement.setIsTop(false);
                announcement.setTopOrder(null);
            }

            announcementRepository.save(announcement);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("{}公告成功 - ID={}, 标题: {}, 耗时: {}ms", isTop ? "置顶" : "取消置顶", id, announcement.getTitle(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("{}公告失败 - ID={}, 耗时: {}ms, 错误: {}", isTop ? "置顶" : "取消置顶", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取下一个置顶排序值
     */
    private Integer getNextTopOrder() {
        List<Announcement> topAnnouncements = announcementRepository
                .findByIsTopTrueAndStatusAndIsDeletedFalseOrderByTopOrderDesc("PUBLISHED");
        if (topAnnouncements.isEmpty()) {
            return 1;
        }
        Integer maxOrder = topAnnouncements.get(0).getTopOrder();
        return maxOrder != null ? maxOrder + 1 : 1;
    }

    /**
     * 创建分页对象
     */
    private Pageable createPageable(PageRequest pageRequest) {
        int page = pageRequest.getPage() != null ? pageRequest.getPage() : 0;
        int size = pageRequest.getSize() != null ? pageRequest.getSize() : 20;

        Sort sort = Sort.by(Sort.Direction.DESC, "isTop")
                .and(Sort.by(Sort.Direction.DESC, "topOrder"))
                .and(Sort.by(Sort.Direction.DESC, "publishTime"));

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
    private AnnouncementResponse convertToResponse(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setContent(announcement.getContent());
        response.setType(announcement.getType());
        response.setIsTop(announcement.getIsTop());
        response.setPublishTime(announcement.getPublishTime());
        response.setStatus(announcement.getStatus());
        response.setViewCount(announcement.getViewCount());
        response.setCreatedAt(announcement.getCreatedAt());
        return response;
    }
}
