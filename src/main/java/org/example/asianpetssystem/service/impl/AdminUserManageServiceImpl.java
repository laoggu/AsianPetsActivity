package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.dto.request.AdminUserCreateRequest;
import org.example.asianpetssystem.dto.request.AdminUserUpdateRequest;
import org.example.asianpetssystem.dto.response.AdminUserResponse;
import org.example.asianpetssystem.entity.AdminUser;
import org.example.asianpetssystem.entity.Role;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.AdminUserRepository;
import org.example.asianpetssystem.repository.RoleRepository;
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.AdminUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserManageServiceImpl implements AdminUserManageService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserManageServiceImpl.class);

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    public PageResponse<AdminUserResponse> getUserList(String username, String status, PageRequest pageRequest) {
        logger.info("开始获取用户列表 - username={}, status={}, page={}, size={}", 
                username, status, pageRequest.getPage(), pageRequest.getSize());
        long startTime = System.currentTimeMillis();

        try {
            Pageable pageable = createPageable(pageRequest);
            Integer statusInt = convertStatusToInt(status);

            Page<AdminUser> users;
            if (StringUtils.hasText(username) && statusInt != null) {
                users = adminUserRepository.findByUsernameContainingAndStatus(username, statusInt, pageable);
            } else if (StringUtils.hasText(username)) {
                users = adminUserRepository.findByUsernameContainingAndStatusNotDeleted(username, pageable);
            } else if (statusInt != null) {
                users = adminUserRepository.findByStatus(statusInt, pageable);
            } else {
                users = adminUserRepository.findAllActive(pageable);
            }

            List<AdminUserResponse> content = users.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            PageResponse<AdminUserResponse> response = new PageResponse<>();
            response.setContent(content);
            response.setTotalElements(users.getTotalElements());
            response.setTotalPages(users.getTotalPages());
            response.setNumber(users.getNumber());
            response.setSize(users.getSize());
            response.setFirst(users.isFirst());
            response.setLast(users.isLast());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取用户列表成功 - 返回 {} 条记录, 耗时: {}ms", content.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取用户列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AdminUserResponse getUserById(Long id) {
        logger.info("开始获取用户详情 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AdminUser user = adminUserRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND));

            if (user.getStatus() != null && user.getStatus() == -1) {
                throw new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND);
            }

            AdminUserResponse response = convertToResponse(user);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取用户详情成功 - ID={}, 耗时: {}ms", id, duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取用户详情失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AdminUserResponse createUser(AdminUserCreateRequest request) {
        logger.info("开始创建用户 - username={}", request.getUsername());
        long startTime = System.currentTimeMillis();

        try {
            // 检查用户名是否已存在
            if (adminUserRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException(BusinessErrorEnum.ADMIN_USER_EXISTS);
            }

            // 验证角色是否存在
            if (request.getRoleId() != null) {
                Role role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ROLE_NOT_FOUND));
            }

            AdminUser user = new AdminUser();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRealName(request.getRealName());
            user.setMobile(request.getMobile());
            user.setEmail(request.getEmail());
            user.setRoleId(request.getRoleId());
            user.setStatus(1); // 默认启用
            user.setCreatedAt(LocalDateTime.now());

            AdminUser savedUser = adminUserRepository.save(user);
            AdminUserResponse response = convertToResponse(savedUser);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建用户成功 - ID={}, 用户名: {}, 耗时: {}ms", savedUser.getId(), savedUser.getUsername(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建用户失败 - 用户名: {}, 耗时: {}ms, 错误: {}", request.getUsername(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AdminUserResponse updateUser(Long id, AdminUserUpdateRequest request) {
        logger.info("开始更新用户 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AdminUser user = adminUserRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND));

            if (user.getStatus() != null && user.getStatus() == -1) {
                throw new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND);
            }

            // 验证角色是否存在
            if (request.getRoleId() != null) {
                Role role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ROLE_NOT_FOUND));
            }

            user.setRealName(request.getRealName());
            user.setMobile(request.getMobile());
            user.setEmail(request.getEmail());
            user.setRoleId(request.getRoleId());

            AdminUser updatedUser = adminUserRepository.save(user);
            AdminUserResponse response = convertToResponse(updatedUser);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新用户成功 - ID={}, 耗时: {}ms", id, duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新用户失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("开始删除用户 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AdminUser user = adminUserRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND));

            if (user.getStatus() != null && user.getStatus() == -1) {
                throw new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND);
            }

            // 检查是否为当前登录用户
            Long currentUserId = authenticationFacade.getCurrentUserId();
            if (currentUserId != null && currentUserId.equals(id)) {
                throw new BusinessException(BusinessErrorEnum.CANNOT_DELETE_SELF);
            }

            // 逻辑删除，将状态设置为-1
            user.setStatus(-1);
            adminUserRepository.save(user);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除用户成功 - ID={}, 耗时: {}ms", id, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除用户失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateUserStatus(Long id, String status) {
        logger.info("开始更新用户状态 - ID={}, status={}", id, status);
        long startTime = System.currentTimeMillis();

        try {
            AdminUser user = adminUserRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND));

            if (user.getStatus() != null && user.getStatus() == -1) {
                throw new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND);
            }

            Integer statusInt = "ACTIVE".equals(status) ? 1 : 0;
            user.setStatus(statusInt);
            adminUserRepository.save(user);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新用户状态成功 - ID={}, status={}, 耗时: {}ms", id, status, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新用户状态失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        logger.info("开始重置密码 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            AdminUser user = adminUserRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND));

            if (user.getStatus() != null && user.getStatus() == -1) {
                throw new BusinessException(BusinessErrorEnum.ADMIN_USER_NOT_FOUND);
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            adminUserRepository.save(user);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("重置密码成功 - ID={}, 耗时: {}ms", id, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("重置密码失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 创建分页对象
     */
    private Pageable createPageable(PageRequest pageRequest) {
        int page = pageRequest.getPage() != null ? pageRequest.getPage() : 0;
        int size = pageRequest.getSize() != null ? pageRequest.getSize() : 20;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if (StringUtils.hasText(pageRequest.getSortBy())) {
            Sort.Direction direction = "asc".equalsIgnoreCase(pageRequest.getSortDirection())
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, pageRequest.getSortBy());
        }

        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }

    /**
     * 将状态字符串转换为整数
     */
    private Integer convertStatusToInt(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        return "ACTIVE".equals(status) ? 1 : 0;
    }

    /**
     * 将状态整数转换为字符串
     */
    private String convertStatusToString(Integer status) {
        if (status == null) {
            return "INACTIVE";
        }
        return status == 1 ? "ACTIVE" : "INACTIVE";
    }

    /**
     * 转换为响应对象
     */
    private AdminUserResponse convertToResponse(AdminUser user) {
        AdminUserResponse response = new AdminUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setMobile(user.getMobile());
        response.setEmail(user.getEmail());
        response.setRoleId(user.getRoleId());
        
        // 查询角色名称
        if (user.getRoleId() != null) {
            roleRepository.findById(user.getRoleId()).ifPresent(role -> {
                response.setRoleName(role.getName());
            });
        }
        
        response.setStatus(convertStatusToString(user.getStatus()));
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLoginTime(user.getLastLoginAt());
        return response;
    }
}
