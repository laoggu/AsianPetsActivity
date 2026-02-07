package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogResponse {

    private Long id;
    private Long memberId;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String actionDesc;
    private String remark;
    private LocalDateTime createdAt;
}
