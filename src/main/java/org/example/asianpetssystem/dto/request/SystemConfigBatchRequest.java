package org.example.asianpetssystem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SystemConfigBatchRequest {

    @NotEmpty(message = "配置列表不能为空")
    @Valid
    private List<ConfigItem> configs;

    @Data
    public static class ConfigItem {
        @NotEmpty(message = "配置键不能为空")
        private String configKey;
        private String configValue;
        private String description;
    }
}
