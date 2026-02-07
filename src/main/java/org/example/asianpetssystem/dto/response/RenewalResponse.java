package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RenewalResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private String renewalNo;
    private LocalDateTime originalExpireDate;
    private LocalDateTime newExpireDate;
    private BigDecimal amount;
    private String level;
    private String levelName;
    private String status;
    private String statusName;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    private String transactionNo;
    private String remark;
    private LocalDateTime createdAt;
}
