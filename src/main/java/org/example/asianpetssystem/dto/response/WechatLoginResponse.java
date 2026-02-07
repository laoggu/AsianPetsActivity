package org.example.asianpetssystem.dto.response;

import lombok.Data;

@Data
public class WechatLoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Boolean isNewUser;
    private String openid;
    private String unionid;
    private String memberStatus;  // PENDING, APPROVED, REJECTED, SUSPENDED
    private String memberLevel;
    private String memberNo;
}
