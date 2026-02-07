package org.example.asianpetssystem.dto.response;

import lombok.Data;

@Data
public class PermissionResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private String module;
}
