package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivitySignupResponse {
    private Long id;
    private Long activityId;
    private Long memberId;
    private String contactName;
    private String contactMobile;
    private String contactEmail;
    private String companyName;
    private String status;
    private LocalDateTime signupTime;
    private LocalDateTime auditTime;
    private String auditRemark;
}
