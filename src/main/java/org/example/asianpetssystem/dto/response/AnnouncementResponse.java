package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Boolean isTop;
    private LocalDateTime publishTime;
    private String status;
    private Integer viewCount;
    private LocalDateTime createdAt;
}
