package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BluebookResponse {
    private Long id;
    private String title;
    private Integer year;
    private String description;
    private String fileUrl;
    private Long fileSize;
    private Integer downloadCount;
    private String status;
    private Boolean isMemberOnly;
    private LocalDateTime createdAt;
}
