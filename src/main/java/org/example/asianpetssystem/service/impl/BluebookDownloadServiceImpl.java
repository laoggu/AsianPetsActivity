package org.example.asianpetssystem.service.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.dto.response.BluebookDownloadResponse;
import org.example.asianpetssystem.entity.Bluebook;
import org.example.asianpetssystem.entity.BluebookDownload;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.BluebookDownloadRepository;
import org.example.asianpetssystem.repository.BluebookRepository;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.service.BluebookDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BluebookDownloadServiceImpl implements BluebookDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(BluebookDownloadServiceImpl.class);

    @Value("${app.bluebook.max-downloads-per-member:10}")
    private int maxDownloadsPerMember;

    @Autowired
    private BluebookRepository bluebookRepository;

    @Autowired
    private BluebookDownloadRepository downloadRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Resource downloadBluebook(Long bluebookId, Long memberId, HttpServletRequest request) {
        logger.info("下载蓝皮书 - bluebookId={}, memberId={}", bluebookId, memberId);

        // 检查蓝皮书是否存在
        Bluebook bluebook = bluebookRepository.findById(bluebookId)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.BLUEBOOK_NOT_FOUND));

        // 检查会员是否存在
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        // 检查是否仅会员可下载
        if (Boolean.TRUE.equals(bluebook.getIsMemberOnly())) {
            if (member.getStatus() != org.example.asianpetssystem.common.enums.MemberStatus.APPROVED) {
                throw new BusinessException("仅限正式会员下载");
            }
        }

        // 检查下载次数限制
        long downloadCount = downloadRepository.countByBluebookIdAndMemberId(bluebookId, memberId);
        if (downloadCount >= maxDownloadsPerMember) {
            throw new BusinessException(BusinessErrorEnum.DOWNLOAD_LIMIT_EXCEEDED);
        }

        try {
            // 获取文件
            URL fileUrl = new URL(bluebook.getFileUrl());
            InputStream inputStream = fileUrl.openStream();

            // 生成水印内容：会员编号 + 公司名称
            String watermarkContent = member.getCompanyName() + " " + member.getCreditCode();

            // 添加水印
            byte[] watermarkedPdf = addWatermarkToPdf(inputStream, watermarkContent);

            // 记录下载
            BluebookDownload download = new BluebookDownload();
            download.setBluebookId(bluebookId);
            download.setMemberId(memberId);
            download.setIpAddress(getClientIpAddress(request));
            download.setUserAgent(request.getHeader("User-Agent"));
            download.setFileName(bluebook.getTitle() + "_" + member.getCreditCode() + ".pdf");
            download.setFileSize((long) watermarkedPdf.length);
            download.setHasWatermark(true);
            download.setWatermarkContent(watermarkContent);
            downloadRepository.save(download);

            // 增加下载计数
            bluebook.setDownloadCount(bluebook.getDownloadCount() + 1);
            bluebookRepository.save(bluebook);

            logger.info("蓝皮书下载成功 - bluebookId={}, memberId={}, fileSize={}", 
                    bluebookId, memberId, watermarkedPdf.length);

            return new ByteArrayResource(watermarkedPdf);

        } catch (Exception e) {
            logger.error("蓝皮书下载失败", e);
            throw new BusinessException("下载失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> checkCanDownload(Long bluebookId, Long memberId) {
        Map<String, Object> result = new HashMap<>();

        Bluebook bluebook = bluebookRepository.findById(bluebookId).orElse(null);
        if (bluebook == null) {
            result.put("canDownload", false);
            result.put("reason", "蓝皮书不存在");
            return result;
        }

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            result.put("canDownload", false);
            result.put("reason", "会员不存在");
            return result;
        }

        // 检查会员权限
        if (Boolean.TRUE.equals(bluebook.getIsMemberOnly()) && 
            member.getStatus() != org.example.asianpetssystem.common.enums.MemberStatus.APPROVED) {
            result.put("canDownload", false);
            result.put("reason", "仅限正式会员下载");
            return result;
        }

        // 检查下载次数
        long downloadCount = downloadRepository.countByBluebookIdAndMemberId(bluebookId, memberId);
        int remaining = maxDownloadsPerMember - (int) downloadCount;

        result.put("canDownload", remaining > 0);
        result.put("remainingDownloads", remaining);
        result.put("maxDownloads", maxDownloadsPerMember);
        result.put("downloadedCount", downloadCount);

        if (remaining <= 0) {
            result.put("reason", "下载次数已达上限");
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BluebookDownloadResponse> getDownloadRecords(Long bluebookId, PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(), pageRequest.getSize());
        
        Page<BluebookDownload> page = downloadRepository.findByBluebookId(bluebookId, pageable);
        
        List<BluebookDownloadResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResponse<BluebookDownloadResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BluebookDownloadResponse> getMemberDownloadRecords(Long memberId, PageRequest pageRequest) {
        // 获取会员的所有下载记录
        List<BluebookDownload> downloads = downloadRepository.findByMemberId(memberId);
        
        // 手动分页
        int start = pageRequest.getPage() * pageRequest.getSize();
        int end = Math.min(start + pageRequest.getSize(), downloads.size());
        
        List<BluebookDownloadResponse> content = downloads.subList(start, end).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResponse<BluebookDownloadResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setTotalElements((long) downloads.size());
        response.setTotalPages((int) Math.ceil((double) downloads.size() / pageRequest.getSize()));
        response.setNumber(pageRequest.getPage());
        response.setSize(pageRequest.getSize());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDownloadStats(Long bluebookId) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalDownloads = downloadRepository.countByBluebookId(bluebookId);
        long todayDownloads = downloadRepository.countRecentDownloads(bluebookId, LocalDateTime.now().minusDays(1));
        long weekDownloads = downloadRepository.countRecentDownloads(bluebookId, LocalDateTime.now().minusDays(7));
        long monthDownloads = downloadRepository.countRecentDownloads(bluebookId, LocalDateTime.now().minusDays(30));
        
        stats.put("totalDownloads", totalDownloads);
        stats.put("todayDownloads", todayDownloads);
        stats.put("weekDownloads", weekDownloads);
        stats.put("monthDownloads", monthDownloads);
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public int getRemainingDownloads(Long bluebookId, Long memberId) {
        long downloadCount = downloadRepository.countByBluebookIdAndMemberId(bluebookId, memberId);
        return Math.max(0, maxDownloadsPerMember - (int) downloadCount);
    }

    /**
     * 为PDF添加水印
     */
    private byte[] addWatermarkToPdf(InputStream inputStream, String watermarkText) throws Exception {
        PDDocument document = PDDocument.load(inputStream);
        
        for (PDPage page : document.getPages()) {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, 
                    PDPageContentStream.AppendMode.APPEND, true, true);
            
            // 设置透明度
            PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
            graphicsState.setNonStrokingAlphaConstant(0.2f);
            graphicsState.setBlendMode(BlendMode.MULTIPLY);
            contentStream.setGraphicsStateParameters(graphicsState);
            
            // 设置字体和颜色
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.setNonStrokingColor(Color.GRAY);
            
            // 添加水印（页面中央）
            float centerX = page.getMediaBox().getWidth() / 2;
            float centerY = page.getMediaBox().getHeight() / 2;
            
            contentStream.beginText();
            contentStream.setTextMatrix(org.apache.pdfbox.util.Matrix.getRotateInstance(Math.toRadians(45), 
                    centerX - 100, centerY));
            contentStream.showText(watermarkText);
            contentStream.endText();
            
            contentStream.close();
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        
        return outputStream.toByteArray();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private BluebookDownloadResponse convertToResponse(BluebookDownload download) {
        BluebookDownloadResponse response = new BluebookDownloadResponse();
        response.setId(download.getId());
        response.setBluebookId(download.getBluebookId());
        
        Bluebook bluebook = bluebookRepository.findById(download.getBluebookId()).orElse(null);
        response.setBluebookTitle(bluebook != null ? bluebook.getTitle() : "");
        
        response.setMemberId(download.getMemberId());
        Member member = memberRepository.findById(download.getMemberId()).orElse(null);
        response.setMemberName(member != null ? member.getCompanyName() : "");
        
        response.setDownloadTime(download.getDownloadTime());
        response.setIpAddress(download.getIpAddress());
        response.setFileName(download.getFileName());
        response.setFileSize(download.getFileSize());
        response.setHasWatermark(download.getHasWatermark());
        response.setWatermarkContent(download.getWatermarkContent());
        
        return response;
    }
}
