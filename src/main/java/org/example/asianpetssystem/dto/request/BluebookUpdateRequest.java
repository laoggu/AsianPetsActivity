package org.example.asianpetssystem.dto.request;

import lombok.Data;

@Data
public class BluebookUpdateRequest {
    private String title;
    private Integer year;
    private String description;
    private String fileUrl;
    private Long fileSize;
    private Boolean isMemberOnly;
}
