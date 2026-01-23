package org.example.asianpetssystem.common.enums;

import lombok.Getter;

@Getter
public enum ResponseCodeEnum {
    SUCCESS(200, "成功"),
    ERROR(500, "服务器内部错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    VALIDATE_ERROR(422, "参数校验失败"),
    BUSINESS_ERROR(600, "业务错误");

    private final int code;
    private final String message;

    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
