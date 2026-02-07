package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberRightResponse {

    private Long id;
    private String level;
    private String title;
    private String description;
    private String icon;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
