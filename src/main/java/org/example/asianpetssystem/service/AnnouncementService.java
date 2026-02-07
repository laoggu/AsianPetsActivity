// src/main/java/org/example/asianpetssystem/service/AnnouncementService.java
package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.AnnouncementCreateRequest;
import org.example.asianpetssystem.dto.request.AnnouncementUpdateRequest;
import org.example.asianpetssystem.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {

    /**
     * 获取公告列表
     *
     * @param type        类型筛选
     * @param pageRequest 分页请求
     * @return 分页响应
     */
    PageResponse<AnnouncementResponse> getAnnouncementList(String type, PageRequest pageRequest);

    /**
     * 获取置顶公告列表
     *
     * @return 置顶公告列表
     */
    List<AnnouncementResponse> getTopAnnouncements();

    /**
     * 根据ID获取公告详情
     *
     * @param id 公告ID
     * @return 公告响应
     */
    AnnouncementResponse getAnnouncementById(Long id);

    /**
     * 创建公告
     *
     * @param request 创建请求
     * @return 创建的公告响应
     */
    AnnouncementResponse createAnnouncement(AnnouncementCreateRequest request);

    /**
     * 更新公告
     *
     * @param id      公告ID
     * @param request 更新请求
     * @return 更新后的公告响应
     */
    AnnouncementResponse updateAnnouncement(Long id, AnnouncementUpdateRequest request);

    /**
     * 删除公告（软删除）
     *
     * @param id 公告ID
     */
    void deleteAnnouncement(Long id);

    /**
     * 置顶/取消置顶公告
     *
     * @param id    公告ID
     * @param isTop 是否置顶
     */
    void topAnnouncement(Long id, Boolean isTop);
}
