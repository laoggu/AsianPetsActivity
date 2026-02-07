package org.example.asianpetssystem.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageSendRequest {
    private String title;
    private String content;
    private String type;
    private String targetType;
    private String targetValue;
    private String sendType;
    private LocalDateTime scheduledTime;
}
