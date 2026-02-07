// src/main/java/org/example/asianpetssystem/service/impl/ActivityServiceImpl.java
package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.ActivityStatus;
import org.example.asianpetssystem.common.enums.SignupStatus;
import org.example.asianpetssystem.dto.request.ActivityCreateRequest;
import org.example.asianpetssystem.dto.request.ActivitySignupAuditRequest;
import org.example.asianpetssystem.dto.request.ActivityUpdateRequest;
import org.example.asianpetssystem.dto.response.ActivityCheckinResponse;
import org.example.asianpetssystem.dto.response.ActivityResponse;
import org.example.asianpetssystem.dto.response.ActivitySignupResponse;
import org.example.asianpetssystem.entity.Activity;
import org.example.asianpetssystem.entity.ActivityCheckin;
import org.example.asianpetssystem.entity.ActivitySignup;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.ActivityCheckinRepository;
import org.example.asianpetssystem.repository.ActivityRepository;
import org.example.asianpetssystem.repository.ActivitySignupRepository;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivitySignupRepository activitySignupRepository;

    @Autowired
    private ActivityCheckinRepository activityCheckinRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityResponse> getActivityList(String status, String keyword, PageRequest pageRequest) {
        logger.info("开始获取活动列表 - status={}, keyword={}, page={}, size={}", 
                status, keyword, pageRequest.getPage(), pageRequest.getSize());
        long startTime = System.currentTimeMillis();

        try {
            // 构建分页参数
            Pageable pageable = buildPageable(pageRequest);
            Page<Activity> activityPage;

            // 根据条件查询
            if (StringUtils.hasText(status)) {
                ActivityStatus activityStatus = ActivityStatus.valueOf(status.toUpperCase());
                activityPage = activityRepository.findByStatusAndIsDeletedFalse(activityStatus, pageable);
            } else if (StringUtils.hasText(keyword)) {
                activityPage = activityRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);
            } else {
                activityPage = activityRepository.findByIsDeletedFalse(pageable);
            }

            // 转换为响应对象
            List<ActivityResponse> content = activityPage.getContent().stream()
                    .map(this::convertToActivityResponse)
                    .collect(Collectors.toList());

            PageResponse<ActivityResponse> response = new PageResponse<>();
            response.setContent(content);
            response.setTotalElements(activityPage.getTotalElements());
            response.setTotalPages(activityPage.getTotalPages());
            response.setNumber(activityPage.getNumber());
            response.setSize(activityPage.getSize());
            response.setFirst(activityPage.isFirst());
            response.setLast(activityPage.isLast());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取活动列表成功 - 返回 {} 条记录, 耗时: {}ms", content.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取活动列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponse getActivityById(Long id) {
        logger.info("开始获取活动详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Activity activity = activityRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            ActivityResponse response = convertToActivityResponse(activity);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取活动详情成功 - ID={}, 标题: {}, 耗时: {}ms", id, activity.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取活动详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ActivityResponse createActivity(ActivityCreateRequest request) {
        logger.info("开始创建活动 - 标题={}", request.getTitle());
        long startTime = System.currentTimeMillis();

        try {
            Activity activity = new Activity();
            activity.setTitle(request.getTitle());
            activity.setCoverImage(request.getCoverImage());
            activity.setDescription(request.getDescription());
            activity.setAgenda(request.getAgenda());
            activity.setLocation(request.getLocation());
            activity.setActivityType(request.getActivityType());
            activity.setTargetAudience(request.getTargetAudience());
            activity.setMaxParticipants(request.getMaxParticipants());
            activity.setFee(request.getFee());
            activity.setStartTime(request.getStartTime());
            activity.setEndTime(request.getEndTime());
            activity.setRegistrationStart(request.getRegistrationStart());
            activity.setRegistrationEnd(request.getRegistrationEnd());
            activity.setNeedAudit(request.getNeedAudit());
            activity.setStatus(ActivityStatus.DRAFT);
            activity.setIsDeleted(false);
            
            // 设置创建人
            Long currentUserId = getCurrentUserId();
            activity.setCreatedBy(currentUserId);

            Activity savedActivity = activityRepository.save(activity);
            ActivityResponse response = convertToActivityResponse(savedActivity);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建活动成功 - ID={}, 标题: {}, 耗时: {}ms", savedActivity.getId(), savedActivity.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建活动失败 - 标题={}, 耗时: {}ms, 错误: {}", request.getTitle(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ActivityResponse updateActivity(Long id, ActivityUpdateRequest request) {
        logger.info("开始更新活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Activity activity = activityRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            // 只有草稿状态的活动才能编辑
            if (activity.getStatus() != ActivityStatus.DRAFT && activity.getStatus() != ActivityStatus.PUBLISHED) {
                throw new BusinessException("当前状态的活动不能编辑");
            }

            activity.setTitle(request.getTitle());
            activity.setCoverImage(request.getCoverImage());
            activity.setDescription(request.getDescription());
            activity.setAgenda(request.getAgenda());
            activity.setLocation(request.getLocation());
            activity.setActivityType(request.getActivityType());
            activity.setTargetAudience(request.getTargetAudience());
            activity.setMaxParticipants(request.getMaxParticipants());
            activity.setFee(request.getFee());
            activity.setStartTime(request.getStartTime());
            activity.setEndTime(request.getEndTime());
            activity.setRegistrationStart(request.getRegistrationStart());
            activity.setRegistrationEnd(request.getRegistrationEnd());
            activity.setNeedAudit(request.getNeedAudit());

            Activity updatedActivity = activityRepository.save(activity);
            ActivityResponse response = convertToActivityResponse(updatedActivity);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新活动成功 - ID={}, 标题: {}, 耗时: {}ms", id, updatedActivity.getTitle(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteActivity(Long id) {
        logger.info("开始删除活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Activity activity = activityRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            // 软删除
            activity.setIsDeleted(true);
            activityRepository.save(activity);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除活动成功 - ID={}, 标题: {}, 耗时: {}ms", id, activity.getTitle(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void publishActivity(Long id) {
        logger.info("开始发布活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Activity activity = activityRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            if (activity.getStatus() != ActivityStatus.DRAFT) {
                throw new BusinessException("只有草稿状态的活动才能发布");
            }

            activity.setStatus(ActivityStatus.PUBLISHED);
            activityRepository.save(activity);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("发布活动成功 - ID={}, 标题: {}, 耗时: {}ms", id, activity.getTitle(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("发布活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void cancelActivity(Long id) {
        logger.info("开始取消活动 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Activity activity = activityRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            if (activity.getStatus() != ActivityStatus.PUBLISHED) {
                throw new BusinessException("只有已发布状态的活动才能取消");
            }

            activity.setStatus(ActivityStatus.CANCELLED);
            activityRepository.save(activity);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("取消活动成功 - ID={}, 标题: {}, 耗时: {}ms", id, activity.getTitle(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("取消活动失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivitySignupResponse> getSignupList(Long activityId, String status, PageRequest pageRequest) {
        logger.info("开始获取活动报名列表 - activityId={}, status={}, page={}, size={}", 
                activityId, status, pageRequest.getPage(), pageRequest.getSize());
        long startTime = System.currentTimeMillis();

        try {
            // 验证活动是否存在
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            Pageable pageable = buildPageable(pageRequest);
            Page<ActivitySignup> signupPage;

            if (StringUtils.hasText(status)) {
                SignupStatus signupStatus = SignupStatus.valueOf(status.toUpperCase());
                signupPage = activitySignupRepository.findByActivityIdAndStatus(activityId, signupStatus, pageable);
            } else {
                signupPage = activitySignupRepository.findByActivityId(activityId, pageable);
            }

            List<ActivitySignupResponse> content = signupPage.getContent().stream()
                    .map(this::convertToSignupResponse)
                    .collect(Collectors.toList());

            PageResponse<ActivitySignupResponse> response = new PageResponse<>();
            response.setContent(content);
            response.setTotalElements(signupPage.getTotalElements());
            response.setTotalPages(signupPage.getTotalPages());
            response.setNumber(signupPage.getNumber());
            response.setSize(signupPage.getSize());
            response.setFirst(signupPage.isFirst());
            response.setLast(signupPage.isLast());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取活动报名列表成功 - 返回 {} 条记录, 耗时: {}ms", content.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取活动报名列表失败 - activityId={}, 耗时: {}ms, 错误: {}", activityId, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void auditSignup(Long activityId, Long userId, ActivitySignupAuditRequest request) {
        logger.info("开始审核活动报名 - activityId={}, userId={}, status={}", activityId, userId, request.getStatus());
        long startTime = System.currentTimeMillis();

        try {
            // 验证活动是否存在
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            // 查询报名记录
            ActivitySignup signup = activitySignupRepository.findByActivityIdAndMemberId(activityId, userId)
                    .orElseThrow(() -> new BusinessException("报名记录不存在"));

            SignupStatus newStatus = SignupStatus.valueOf(request.getStatus().toUpperCase());
            
            // 验证状态转换
            if (signup.getStatus() != SignupStatus.PENDING) {
                throw new BusinessException("只有待审核状态的报名才能审核");
            }

            signup.setStatus(newStatus);
            signup.setAuditTime(LocalDateTime.now());
            signup.setAuditRemark(request.getRemark());

            activitySignupRepository.save(signup);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("审核活动报名成功 - activityId={}, userId={}, status={}, 耗时: {}ms", 
                    activityId, userId, newStatus, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("审核活动报名失败 - activityId={}, userId={}, 耗时: {}ms, 错误: {}", 
                    activityId, userId, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityCheckinResponse> getCheckinList(Long activityId) {
        logger.info("开始获取活动签到列表 - activityId={}", activityId);
        long startTime = System.currentTimeMillis();

        try {
            // 验证活动是否存在
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            List<ActivityCheckin> checkinList = activityCheckinRepository.findByActivityId(activityId);

            List<ActivityCheckinResponse> response = checkinList.stream()
                    .map(this::convertToCheckinResponse)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取活动签到列表成功 - 返回 {} 条记录, 耗时: {}ms", response.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取活动签到列表失败 - activityId={}, 耗时: {}ms, 错误: {}", activityId, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void checkin(Long activityId, String checkinCode) {
        logger.info("开始活动签到 - activityId={}, checkinCode={}", activityId, checkinCode);
        long startTime = System.currentTimeMillis();

        try {
            // 验证活动是否存在
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new BusinessException("活动不存在"));

            if (Boolean.TRUE.equals(activity.getIsDeleted())) {
                throw new BusinessException("活动已被删除");
            }

            if (activity.getStatus() != ActivityStatus.PUBLISHED) {
                throw new BusinessException("活动未在发布状态，无法签到");
            }

            // 根据签到码查找报名记录
            // 这里假设签到码是报名ID的某种编码，实际实现可能需要调整
            ActivitySignup signup = activitySignupRepository.findById(Long.valueOf(checkinCode))
                    .orElseThrow(() -> new BusinessException("无效的签到码"));

            if (!signup.getActivityId().equals(activityId)) {
                throw new BusinessException("签到码与活动不匹配");
            }

            if (signup.getStatus() != SignupStatus.APPROVED) {
                throw new BusinessException("报名未通过审核，无法签到");
            }

            // 检查是否已经签到
            if (activityCheckinRepository.findByActivityIdAndSignupId(activityId, signup.getId()).isPresent()) {
                throw new BusinessException("已经签到过了");
            }

            // 创建签到记录
            ActivityCheckin checkin = new ActivityCheckin();
            checkin.setActivityId(activityId);
            checkin.setSignupId(signup.getId());
            checkin.setMemberId(signup.getMemberId());
            checkin.setCheckinCode(checkinCode);
            checkin.setCheckinType("ONSITE");

            activityCheckinRepository.save(checkin);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("活动签到成功 - activityId={}, signupId={}, 耗时: {}ms", activityId, signup.getId(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("活动签到失败 - activityId={}, 耗时: {}ms, 错误: {}", activityId, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 构建分页参数
     */
    private Pageable buildPageable(PageRequest pageRequest) {
        int page = pageRequest.getPage() != null ? pageRequest.getPage() : 0;
        int size = pageRequest.getSize() != null ? pageRequest.getSize() : 20;
        
        if (StringUtils.hasText(pageRequest.getSortBy())) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(pageRequest.getSortDirection()) 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return org.springframework.data.domain.PageRequest.of(page, size, direction, pageRequest.getSortBy());
        }
        
        return org.springframework.data.domain.PageRequest.of(page, size);
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        String currentUsername = authenticationFacade.getCurrentUsername();
        if (currentUsername == null) {
            return null;
        }
        // 根据实际需求实现获取当前用户ID的逻辑
        return null;
    }

    /**
     * 转换为ActivityResponse
     */
    private ActivityResponse convertToActivityResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setTitle(activity.getTitle());
        response.setCoverImage(activity.getCoverImage());
        response.setDescription(activity.getDescription());
        response.setAgenda(activity.getAgenda());
        response.setLocation(activity.getLocation());
        response.setActivityType(activity.getActivityType());
        response.setTargetAudience(activity.getTargetAudience());
        response.setMaxParticipants(activity.getMaxParticipants());
        response.setFee(activity.getFee());
        response.setStatus(activity.getStatus() != null ? activity.getStatus().getCode() : null);
        response.setStartTime(activity.getStartTime());
        response.setEndTime(activity.getEndTime());
        response.setRegistrationStart(activity.getRegistrationStart());
        response.setRegistrationEnd(activity.getRegistrationEnd());
        response.setNeedAudit(activity.getNeedAudit());
        response.setCreatedAt(activity.getCreatedAt());
        
        // 统计报名数
        long signupCount = activitySignupRepository.countByActivityId(activity.getId());
        long approvedCount = activitySignupRepository.countByActivityIdAndStatus(activity.getId(), SignupStatus.APPROVED);
        response.setSignupCount((int) signupCount);
        response.setApprovedCount((int) approvedCount);
        
        return response;
    }

    /**
     * 转换为ActivitySignupResponse
     */
    private ActivitySignupResponse convertToSignupResponse(ActivitySignup signup) {
        ActivitySignupResponse response = new ActivitySignupResponse();
        response.setId(signup.getId());
        response.setActivityId(signup.getActivityId());
        response.setMemberId(signup.getMemberId());
        response.setContactName(signup.getContactName());
        response.setContactMobile(signup.getContactMobile());
        response.setContactEmail(signup.getContactEmail());
        response.setCompanyName(signup.getCompanyName());
        response.setStatus(signup.getStatus() != null ? signup.getStatus().getCode() : null);
        response.setSignupTime(signup.getSignupTime());
        response.setAuditTime(signup.getAuditTime());
        response.setAuditRemark(signup.getAuditRemark());
        return response;
    }

    /**
     * 转换为ActivityCheckinResponse
     */
    private ActivityCheckinResponse convertToCheckinResponse(ActivityCheckin checkin) {
        ActivityCheckinResponse response = new ActivityCheckinResponse();
        response.setId(checkin.getId());
        response.setActivityId(checkin.getActivityId());
        response.setSignupId(checkin.getSignupId());
        response.setMemberId(checkin.getMemberId());
        response.setCheckinTime(checkin.getCheckinTime());
        response.setCheckinType(checkin.getCheckinType());
        
        // 获取联系人姓名
        if (checkin.getSignupId() != null) {
            activitySignupRepository.findById(checkin.getSignupId()).ifPresent(signup -> {
                response.setContactName(signup.getContactName());
            });
        }
        
        return response;
    }
}
