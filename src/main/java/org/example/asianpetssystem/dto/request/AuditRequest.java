package org.example.asianpetssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.asianpetssystem.entity.AuditAction;

@Data
public class AuditRequest {
    @NotNull(message = "审核操作不能为空")
    private AuditAction action;

    private String remark;
}