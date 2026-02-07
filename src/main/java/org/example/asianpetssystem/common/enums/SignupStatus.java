package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum SignupStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    SignupStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
