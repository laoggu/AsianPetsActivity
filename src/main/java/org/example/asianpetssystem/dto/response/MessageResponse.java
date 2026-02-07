package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String status;  // UNREAD, READ
    private LocalDateTime sentTime;
    private LocalDateTime readTime;
}
