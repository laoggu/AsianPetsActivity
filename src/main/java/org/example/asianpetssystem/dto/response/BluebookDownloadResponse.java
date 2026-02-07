package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BluebookDownloadResponse {
    private Long id;
    private Long bluebookId;
    private String bluebookTitle;
    private Long memberId;
    private String memberName;
    private LocalDateTime downloadTime;
    private String ipAddress;
    private String fileName;
    private Long fileSize;
    private Boolean hasWatermark;
    private String watermarkContent;
}
