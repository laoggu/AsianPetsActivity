package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum CommonStatusEnum {
    ENABLE(1, "启用"),
    DISABLE(0, "禁用"),
    DELETED(-1, "已删除");

    private final Integer code;
    private final String description;

    CommonStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
