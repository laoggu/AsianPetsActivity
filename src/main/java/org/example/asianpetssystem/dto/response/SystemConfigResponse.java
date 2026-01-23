package org.example.asianpetssystem.dto.response;

import lombok.Data;

@Data
public class SystemConfigResponse {
    private String systemName;
    private String version;
    private String supportEmail;
    private String supportPhone;
}
