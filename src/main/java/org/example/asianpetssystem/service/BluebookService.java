// src/main/java/org/example/asianpetssystem/service/BluebookService.java
package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.BluebookCreateRequest;
import org.example.asianpetssystem.dto.request.BluebookUpdateRequest;
import org.example.asianpetssystem.dto.response.BluebookResponse;

import java.util.Map;

public interface BluebookService {

    /**
     * 获取蓝皮书列表
     *
     * @param year        年份筛选
     * @param keyword     关键词搜索（标题）
     * @param pageRequest 分页请求
     * @return 分页响应
     */
    PageResponse<BluebookResponse> getBluebookList(Integer year, String keyword, PageRequest pageRequest);

    /**
     * 根据ID获取蓝皮书详情
     *
     * @param id 蓝皮书ID
     * @return 蓝皮书响应
     */
    BluebookResponse getBluebookById(Long id);

    /**
     * 创建蓝皮书
     *
     * @param request 创建请求
     * @return 创建的蓝皮书响应
     */
    BluebookResponse createBluebook(BluebookCreateRequest request);

    /**
     * 更新蓝皮书
     *
     * @param id      蓝皮书ID
     * @param request 更新请求
     * @return 更新后的蓝皮书响应
     */
    BluebookResponse updateBluebook(Long id, BluebookUpdateRequest request);

    /**
     * 删除蓝皮书（软删除）
     *
     * @param id 蓝皮书ID
     */
    void deleteBluebook(Long id);

    /**
     * 增加下载次数
     *
     * @param id 蓝皮书ID
     */
    void incrementDownloadCount(Long id);

    /**
     * 获取下载统计
     *
     * @param id 蓝皮书ID
     * @return 统计信息
     */
    Map<String, Object> getDownloadStats(Long id);
}
