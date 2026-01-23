// src/main/java/org/example/asianpetssystem/common/enums/MemberStatus.java
package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum MemberStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    SUSPENDED("SUSPENDED", "已暂停");

    private final String code;
    private final String description;

    MemberStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
