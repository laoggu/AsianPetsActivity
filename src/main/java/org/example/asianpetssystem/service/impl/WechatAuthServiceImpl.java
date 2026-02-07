package org.example.asianpetssystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.asianpetssystem.dto.request.WechatLoginRequest;
import org.example.asianpetssystem.dto.response.WechatLoginResponse;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.security.JwtTokenProvider;
import org.example.asianpetssystem.service.WechatAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WechatAuthServiceImpl implements WechatAuthService {

    private static final Logger logger = LoggerFactory.getLogger(WechatAuthServiceImpl.class);

    @Value("${wechat.miniapp.appid:}")
    private String appId;

    @Value("${wechat.miniapp.secret:}")
    private String appSecret;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WechatLoginResponse wechatLogin(WechatLoginRequest request) {
        logger.info("微信小程序登录 - code={}", request.getCode());

        try {
            // 1. 调用微信接口获取openid和session_key
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, appSecret, request.getCode());

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode")) {
                int errCode = jsonNode.get("errcode").asInt();
                String errMsg = jsonNode.get("errmsg").asText();
                logger.error("微信登录失败 - errCode={}, errMsg={}", errCode, errMsg);
                throw new RuntimeException("微信登录失败：" + errMsg);
            }

            String openid = jsonNode.get("openid").asText();
            String sessionKey = jsonNode.get("session_key").asText();
            String unionid = jsonNode.has("unionid") ? jsonNode.get("unionid").asText() : null;

            // 2. 查找或创建会员
            Optional<Member> memberOpt = memberRepository.findByCreditCode(openid); // 临时使用openid作为creditCode
            Member member;
            boolean isNewUser = false;

            if (memberOpt.isPresent()) {
                member = memberOpt.get();
            } else {
                // 新用户，创建临时会员记录
                member = new Member();
                member.setCreditCode(openid); // 临时使用openid作为creditCode
                member.setCompanyName("微信用户" + openid.substring(0, 8));
                member.setLevel(org.example.asianpetssystem.common.enums.MemberLevel.REGULAR);
                member.setStatus(org.example.asianpetssystem.common.enums.MemberStatus.PENDING);
                memberRepository.save(member);
                isNewUser = true;
            }

            // 3. 生成JWT Token
            String token = jwtTokenProvider.generateToken(openid);

            // 4. 构建响应
            WechatLoginResponse loginResponse = new WechatLoginResponse();
            loginResponse.setToken(token);
            loginResponse.setTokenType("Bearer");
            loginResponse.setExpiresIn(86400L); // 24小时
            loginResponse.setIsNewUser(isNewUser);
            loginResponse.setOpenid(openid);
            loginResponse.setUnionid(unionid);
            loginResponse.setMemberStatus(member.getStatus().name());
            loginResponse.setMemberLevel(member.getLevel().name());

            logger.info("微信登录成功 - openid={}, isNewUser={}", openid, isNewUser);
            return loginResponse;

        } catch (Exception e) {
            logger.error("微信登录异常", e);
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, String> getWechatConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("appId", appId);
        return config;
    }

    @Override
    public String decryptData(String encryptedData, String iv, String sessionKey) {
        try {
            // Base64解码
            byte[] encrypted = Base64.getDecoder().decode(encryptedData);
            byte[] key = Base64.getDecoder().decode(sessionKey);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            // AES解密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            logger.error("解密数据失败", e);
            throw new RuntimeException("解密失败：" + e.getMessage());
        }
    }
}
