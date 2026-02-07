package org.example.asianpetssystem.service;

/**
 * 二维码服务接口
 * 支持活动现场通用签到码（多人可扫）
 */
public interface QRCodeService {

    /**
     * 生成活动现场通用签到二维码（多人可扫）
     * 
     * @param activityId 活动ID
     * @param width 二维码宽度
     * @param height 二维码高度
     * @return Base64编码的图片字符串
     */
    String generateActivityCheckinQRCode(Long activityId, int width, int height);

    /**
     * 生成活动现场通用签到二维码（默认300x300）
     * 
     * @param activityId 活动ID
     * @return Base64编码的图片字符串
     */
    String generateActivityCheckinQRCode(Long activityId);

    /**
     * 用户扫描二维码签到
     * 根据扫码用户的登录态自动找到对应的报名记录完成签到
     * 
     * @param checkinCode 签到码（从二维码解析）
     * @param memberId 当前扫码用户ID（从登录态获取）
     * @return 签到结果
     */
    QRCheckinResult scanAndCheckin(String checkinCode, Long memberId);

    /**
     * 验证签到码是否有效（仅验证码本身，不签到）
     * 
     * @param checkinCode 签到码
     * @return 是否有效
     */
    boolean validateCheckinCode(String checkinCode);

    /**
     * 获取签到码对应的活动ID
     * 
     * @param checkinCode 签到码
     * @return 活动ID，无效则返回null
     */
    Long getActivityIdFromCheckinCode(String checkinCode);

    /**
     * 签到结果类
     */
    class QRCheckinResult {
        private boolean success;
        private String message;
        private Long activityId;
        private String activityTitle;
        private String memberName;
        private String checkinTime;
        private String checkinType; // QR - 扫码签到

        public QRCheckinResult() {}

        public QRCheckinResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getActivityId() { return activityId; }
        public void setActivityId(Long activityId) { this.activityId = activityId; }
        public String getActivityTitle() { return activityTitle; }
        public void setActivityTitle(String activityTitle) { this.activityTitle = activityTitle; }
        public String getMemberName() { return memberName; }
        public void setMemberName(String memberName) { this.memberName = memberName; }
        public String getCheckinTime() { return checkinTime; }
        public void setCheckinTime(String checkinTime) { this.checkinTime = checkinTime; }
        public String getCheckinType() { return checkinType; }
        public void setCheckinType(String checkinType) { this.checkinType = checkinType; }
    }
}
