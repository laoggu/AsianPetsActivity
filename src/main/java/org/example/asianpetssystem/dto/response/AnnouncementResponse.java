package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementResponse {
    private String title;
    private String content;
    private LocalDateTime publishDate;
    private String publisher;
}
