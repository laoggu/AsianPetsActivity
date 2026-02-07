package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.AdminUserCreateRequest;
import org.example.asianpetssystem.dto.request.AdminUserUpdateRequest;
import org.example.asianpetssystem.dto.response.AdminUserResponse;

public interface AdminUserManageService {

    /**
     * 获取用户列表
     *
     * @param username    用户名（模糊查询）
     * @param status      状态
     * @param pageRequest 分页请求
     * @return 分页响应
     */
    PageResponse<AdminUserResponse> getUserList(String username, String status, PageRequest pageRequest);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情响应
     */
    AdminUserResponse getUserById(Long id);

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 创建的用户响应
     */
    AdminUserResponse createUser(AdminUserCreateRequest request);

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 更新请求
     * @return 更新后的用户响应
     */
    AdminUserResponse updateUser(Long id, AdminUserUpdateRequest request);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态（ACTIVE/INACTIVE）
     */
    void updateUserStatus(Long id, String status);

    /**
     * 重置密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);
}
