package org.example.asianpetssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemConfigUpdateRequest {

    @NotBlank(message = "配置键不能为空")
    private String configKey;

    private String configValue;

    private String description;
}
