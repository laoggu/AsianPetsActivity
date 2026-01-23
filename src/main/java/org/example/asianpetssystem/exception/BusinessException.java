package org.example.asianpetssystem.exception;


import org.example.asianpetssystem.common.enums.BusinessErrorEnum;

public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;

    public BusinessException(BusinessErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
        this.message = errorEnum.getMessage();
    }

    public BusinessException(String message) {
        super(message);
        this.code = 600; // 业务错误码
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
