package org.example.asianpetssystem.entity;

import lombok.Getter;

@Getter
public enum AuditAction {
    APPROVE("approve", "批准"),
    REJECT("reject", "拒绝"),
    SUPPLEMENT("supplement", "要求补充材料");

    private final String code;
    private final String description;

    AuditAction(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
