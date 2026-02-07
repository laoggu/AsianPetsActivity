package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityResponse {
    private Long id;
    private String title;
    private String coverImage;
    private String description;
    private String agenda;
    private String location;
    private String activityType;
    private String targetAudience;
    private Integer maxParticipants;
    private BigDecimal fee;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private Boolean needAudit;
    private LocalDateTime createdAt;
    private Integer signupCount;
    private Integer approvedCount;
}
