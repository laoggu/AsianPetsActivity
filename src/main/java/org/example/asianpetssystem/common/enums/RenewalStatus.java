package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum RenewalStatus {
    PENDING_PAYMENT("PENDING_PAYMENT", "待支付"),
    PAID("PAID", "已支付"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消"),
    OVERDUE("OVERDUE", "已过期");

    private final String code;
    private final String description;

    RenewalStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
