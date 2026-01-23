package org.example.asianpetssystem.dto.response;

import lombok.Data;
import org.example.asianpetssystem.common.enums.MemberStatus;

import java.time.LocalDateTime;

@Data
public class ApplyListResponse {
    private Long id;
    private String companyName;
    private String creditCode;
    private MemberStatus status;
    private LocalDateTime applyTime;
    private String primaryContactName;
    private String primaryContactMobile;
}
