package org.example.asianpetssystem.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.common.enums.SignupStatus;
import org.example.asianpetssystem.entity.Activity;
import org.example.asianpetssystem.entity.ActivityCheckin;
import org.example.asianpetssystem.entity.ActivitySignup;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.ActivityCheckinRepository;
import org.example.asianpetssystem.repository.ActivityRepository;
import org.example.asianpetssystem.repository.ActivitySignupRepository;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.service.QRCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${app.qrcode.expire-minutes:120}")
    private int qrCodeExpireMinutes;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivitySignupRepository activitySignupRepository;

    @Autowired
    private ActivityCheckinRepository activityCheckinRepository;

    @Autowired
    private MemberRepository memberRepository;

    // Redis key前缀
    private static final String CHECKIN_CODE_PREFIX = "checkin:code:";

    @Override
    public String generateActivityCheckinQRCode(Long activityId, int width, int height) {
        logger.info("生成活动现场签到二维码 - activityId={}, size={}x{}", activityId, width, height);
        long start = System.currentTimeMillis();

        try {
            // 验证活动是否存在且有效
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ACTIVITY_NOT_FOUND));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已删除");
            }

            // 生成通用签到码
            String checkinCode = generateUniqueCheckinCode(activityId);

            // 二维码内容：签到页面URL + 签到码
            String qrContent = buildCheckinPageUrl(checkinCode);

            // 生成二维码图片
            String qrCodeBase64 = generateQRCodeBase64(qrContent, width, height);

            long duration = System.currentTimeMillis() - start;
            logger.info("生成活动现场签到二维码成功 - activityId={}, checkinCode={}, 有效期:{}分钟, 耗时:{}ms", 
                    activityId, checkinCode, qrCodeExpireMinutes, duration);

            return qrCodeBase64;
        } catch (Exception e) {
            logger.error("生成活动现场签到二维码失败 - activityId={}", activityId, e);
            throw e;
        }
    }

    @Override
    public String generateActivityCheckinQRCode(Long activityId) {
        return generateActivityCheckinQRCode(activityId, 300, 300);
    }

    @Override
    @Transactional
    public QRCheckinResult scanAndCheckin(String checkinCode, Long memberId) {
        logger.info("用户扫码签到 - checkinCode={}, memberId={}", checkinCode, memberId);
        long start = System.currentTimeMillis();

        try {
            // 1. 验证签到码有效性并获取活动ID
            Long activityId = getActivityIdFromCheckinCode(checkinCode);
            if (activityId == null) {
                logger.warn("签到码无效或已过期 - checkinCode={}", checkinCode);
                return new QRCheckinResult(false, "签到码无效或已过期，请刷新二维码重试");
            }

            // 2. 获取活动信息
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ACTIVITY_NOT_FOUND));

            // 3. 获取会员信息
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

            // 4. 查找用户的报名记录
            Optional<ActivitySignup> signupOpt = activitySignupRepository
                    .findByActivityIdAndMemberId(activityId, memberId);

            if (signupOpt.isEmpty()) {
                logger.warn("用户未报名该活动 - activityId={}, memberId={}", activityId, memberId);
                return new QRCheckinResult(false, "您未报名该活动，无法签到");
            }

            ActivitySignup signup = signupOpt.get();

            // 5. 检查报名状态
            if (signup.getStatus() != SignupStatus.APPROVED) {
                logger.warn("报名未通过审核 - signupId={}, status={}", signup.getId(), signup.getStatus());
                return new QRCheckinResult(false, "您的报名尚未通过审核，无法签到");
            }

            // 6. 检查是否已签到
            Optional<ActivityCheckin> existingCheckin = activityCheckinRepository
                    .findByActivityIdAndSignupId(activityId, signup.getId());

            if (existingCheckin.isPresent()) {
                logger.info("用户已签到，无需重复签到 - memberId={}, activityId={}", memberId, activityId);
                QRCheckinResult result = new QRCheckinResult(false, "您已完成签到，无需重复签到");
                result.setActivityId(activityId);
                result.setActivityTitle(activity.getTitle());
                result.setMemberName(member.getCompanyName());
                result.setCheckinTime(existingCheckin.get().getCheckinTime().format(DATE_FORMATTER));
                return result;
            }

            // 7. 创建签到记录
            ActivityCheckin checkin = new ActivityCheckin();
            checkin.setActivityId(activityId);
            checkin.setSignupId(signup.getId());
            checkin.setMemberId(memberId);
            checkin.setCheckinTime(LocalDateTime.now());
            checkin.setCheckinType("QR");
            checkin.setCheckinCode(checkinCode);

            activityCheckinRepository.save(checkin);

            // 8. 返回成功结果
            QRCheckinResult result = new QRCheckinResult(true, "签到成功！欢迎参加" + activity.getTitle());
            result.setActivityId(activityId);
            result.setActivityTitle(activity.getTitle());
            result.setMemberName(member.getCompanyName());
            result.setCheckinTime(checkin.getCheckinTime().format(DATE_FORMATTER));
            result.setCheckinType("QR");

            long duration = System.currentTimeMillis() - start;
            logger.info("扫码签到成功 - memberId={}, activityId={}, signupId={}, 耗时:{}ms", 
                    memberId, activityId, signup.getId(), duration);

            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            logger.error("扫码签到失败 - checkinCode={}, memberId={}, 耗时:{}ms, 错误:{}", 
                    checkinCode, memberId, duration, e.getMessage(), e);
            return new QRCheckinResult(false, "签到失败：" + e.getMessage());
        }
    }

    @Override
    public boolean validateCheckinCode(String checkinCode) {
        String redisKey = CHECKIN_CODE_PREFIX + checkinCode;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    @Override
    public Long getActivityIdFromCheckinCode(String checkinCode) {
        String redisKey = CHECKIN_CODE_PREFIX + checkinCode;
        String value = redisTemplate.opsForValue().get(redisKey);
        
        if (value == null) {
            return null;
        }

        try {
            // value格式: activityId:expireTime
            String[] parts = value.split(":");
            if (parts.length >= 1) {
                return Long.parseLong(parts[0]);
            }
        } catch (Exception e) {
            logger.warn("解析签到码失败 - checkinCode={}, value={}", checkinCode, value);
        }

        return null;
    }

    /**
     * 生成唯一的签到码
     */
    private String generateUniqueCheckinCode(Long activityId) {
        // 生成8位随机码
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        
        // 存储到Redis，设置过期时间
        String redisKey = CHECKIN_CODE_PREFIX + code;
        String redisValue = activityId + ":" + LocalDateTime.now().plusMinutes(qrCodeExpireMinutes).format(DATE_FORMATTER);
        
        redisTemplate.opsForValue().set(redisKey, redisValue, qrCodeExpireMinutes, TimeUnit.MINUTES);
        
        logger.debug("生成签到码 - activityId={}, code={}, expireMinutes={}", 
                activityId, code, qrCodeExpireMinutes);
        return code;
    }

    /**
     * 构建签到页面URL
     * 前端小程序扫描后会跳转到此页面，自动完成签到
     */
    private String buildCheckinPageUrl(String checkinCode) {
        // 小程序页面路径或H5页面URL
        // 格式：pages/checkin/checkin?code=XXX
        return "pages/checkin/checkin?code=" + checkinCode;
    }

    /**
     * 生成Base64格式的二维码图片
     */
    private String generateQRCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            logger.error("生成二维码图片失败", e);
            throw new BusinessException("生成二维码失败：" + e.getMessage());
        }
    }
}
