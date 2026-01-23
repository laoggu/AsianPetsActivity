package org.example.asianpetssystem.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.asianpetssystem.entity.AuditAction;

@Data
public class AuditRequest {

    @NotNull(message = "审核操作不能为空")
    private AuditAction action;

    @Size(max = 500, message = "审核备注不能超过500个字符")
    private String remark;
}
