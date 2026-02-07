package org.example.asianpetssystem.dto.request;

import lombok.Data;

@Data
public class AnnouncementUpdateRequest {
    private String title;
    private String content;
    private String type;
    private Boolean isTop;
}
