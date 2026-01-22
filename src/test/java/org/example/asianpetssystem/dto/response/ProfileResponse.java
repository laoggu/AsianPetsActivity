package org.example.asianpetssystem.dto.response;

import lombok.Data;
import org.example.asianpetssystem.entity.MemberLevel;
import org.example.asianpetssystem.entity.MemberStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProfileResponse {
    private Long id;
    private String companyName;
    private String creditCode;
    private MemberLevel level;
    private MemberStatus status;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
    private List<ContactInfo> contacts;

    @Data
    public static class ContactInfo {
        private String name;
        private String mobile; // 已脱敏
        private String email;
        private Boolean isPrimary;
    }
}
