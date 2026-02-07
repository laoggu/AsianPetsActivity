package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityCheckinResponse {
    private Long id;
    private Long activityId;
    private Long signupId;
    private Long memberId;
    private String contactName;
    private LocalDateTime checkinTime;
    private String checkinType;
}
