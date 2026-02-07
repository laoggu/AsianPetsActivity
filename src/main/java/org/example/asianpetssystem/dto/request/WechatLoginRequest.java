package org.example.asianpetssystem.dto.request;

import lombok.Data;

@Data
public class WechatLoginRequest {
    private String code;  // 微信登录临时凭证
    private String encryptedData;  // 加密数据（可选，用于获取手机号）
    private String iv;  // 加密算法的初始向量（可选）
}
