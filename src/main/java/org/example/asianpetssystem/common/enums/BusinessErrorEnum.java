package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum BusinessErrorEnum {
    MEMBER_EXISTS(1001, "会员已存在"),
    CREDIT_CODE_EXISTS(1002, "统一社会信用代码已存在"),
    MEMBER_NOT_FOUND(1003, "会员不存在"),
    INVALID_CREDIT_CODE(1004, "统一社会信用代码格式不正确"),
    INVALID_MOBILE(1005, "手机号格式不正确"),
    INVALID_EMAIL(1006, "邮箱格式不正确"),
    INSUFFICIENT_PERMISSION(1007, "权限不足"),
    INVALID_AUDIT_ACTION(1008, "无效的审核操作");

    private final int code;
    private final String message;

    BusinessErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
