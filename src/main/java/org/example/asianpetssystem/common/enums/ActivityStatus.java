package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum ActivityStatus {
    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "已发布"),
    CANCELLED("CANCELLED", "已取消"),
    ENDED("ENDED", "已结束");

    private final String code;
    private final String description;

    ActivityStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
