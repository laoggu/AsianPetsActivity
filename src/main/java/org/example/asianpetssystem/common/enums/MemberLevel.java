// src/main/java/org/example/asianpetssystem/common/enums/MemberLevel.java
package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum MemberLevel {
    REGULAR("REGULAR", "普通会员"),
    GOLD("GOLD", "黄金会员"),
    PLATINUM("PLATINUM", "白金会员");

    private final String code;
    private final String description;

    MemberLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
