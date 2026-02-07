package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserResponse {

    private Long id;
    private String username;
    private String realName;
    private String mobile;
    private String email;
    private Long roleId;
    private String roleName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginTime;
}
