package org.example.asianpetssystem.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RenewalCreateRequest {
    private Long memberId;
    private LocalDateTime newExpireDate;
    private BigDecimal amount;
    private String level;
    private String remark;
}
