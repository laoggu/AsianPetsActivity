package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionResponse {

    private Long roleId;
    private String roleName;
    private String roleCode;
    private List<PermissionResponse> permissions;
}
