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
    INVALID_AUDIT_ACTION(1008, "无效的审核操作"),
    BLUEBOOK_NOT_FOUND(1009, "蓝皮书不存在"),
    ANNOUNCEMENT_NOT_FOUND(1010, "公告不存在"),
    RIGHT_NOT_FOUND(1011, "权益不存在"),
    ROLE_NOT_FOUND(1012, "角色不存在"),
    ROLE_CODE_EXISTS(1013, "角色编码已存在"),
    PERMISSION_NOT_FOUND(1014, "权限不存在"),
    ADMIN_USER_NOT_FOUND(1015, "管理员用户不存在"),
    ADMIN_USER_EXISTS(1016, "管理员用户名已存在"),
    CANNOT_DELETE_SELF(1017, "不能删除当前登录用户"),
    CONFIG_NOT_FOUND(1018, "配置不存在"),
    SYSTEM_ERROR(1019, "系统错误"),
    ACTIVITY_NOT_FOUND(1020, "活动不存在"),
    SIGNUP_NOT_FOUND(1021, "报名记录不存在"),
    CHECKIN_CODE_INVALID(1022, "签到码无效或已过期"),
    ALREADY_CHECKED_IN(1023, "您已完成签到，无需重复签到"),
    RENEWAL_NOT_FOUND(1024, "续费记录不存在"),
    DOWNLOAD_LIMIT_EXCEEDED(1025, "下载次数已达上限"),
    EVALUATION_NOT_FOUND(1026, "评价记录不存在"),
    ACTIVITY_NOT_ENDED(1027, "活动尚未结束，无法评价"),
    ALREADY_EVALUATED(1028, "您已评价过该活动");

    private final int code;
    private final String message;

    BusinessErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
