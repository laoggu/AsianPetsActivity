package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.request.WechatLoginRequest;
import org.example.asianpetssystem.dto.response.WechatLoginResponse;

import java.util.Map;

public interface WechatAuthService {

    /**
     * 微信小程序登录
     * @param request 登录请求
     * @return 登录响应
     */
    WechatLoginResponse wechatLogin(WechatLoginRequest request);

    /**
     * 获取微信登录配置（前端调用微信登录接口所需参数）
     * @return 配置信息
     */
    Map<String, String> getWechatConfig();

    /**
     * 解密微信加密数据（用于获取手机号等敏感信息）
     * @param encryptedData 加密数据
     * @param iv 初始向量
     * @param sessionKey 会话密钥
     * @return 解密后的数据
     */
    String decryptData(String encryptedData, String iv, String sessionKey);
}
