package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.response.BluebookDownloadResponse;
import org.springframework.core.io.Resource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface BluebookDownloadService {

    /**
     * 下载蓝皮书（带水印）
     * @param bluebookId 蓝皮书ID
     * @param memberId 会员ID
     * @param request HTTP请求
     * @return 文件资源
     */
    Resource downloadBluebook(Long bluebookId, Long memberId, HttpServletRequest request);

    /**
     * 检查会员是否可以下载
     * @param bluebookId 蓝皮书ID
     * @param memberId 会员ID
     * @return 检查结果
     */
    Map<String, Object> checkCanDownload(Long bluebookId, Long memberId);

    /**
     * 获取下载记录
     */
    PageResponse<BluebookDownloadResponse> getDownloadRecords(Long bluebookId, PageRequest pageRequest);

    /**
     * 获取会员的下载记录
     */
    PageResponse<BluebookDownloadResponse> getMemberDownloadRecords(Long memberId, PageRequest pageRequest);

    /**
     * 获取下载统计
     */
    Map<String, Object> getDownloadStats(Long bluebookId);

    /**
     * 获取会员剩余下载次数
     */
    int getRemainingDownloads(Long bluebookId, Long memberId);
}
