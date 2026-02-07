// src/main/java/org/example/asianpetssystem/service/RoleService.java
package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.RoleCreateRequest;
import org.example.asianpetssystem.dto.request.RoleUpdateRequest;
import org.example.asianpetssystem.dto.response.PermissionResponse;
import org.example.asianpetssystem.dto.response.RolePermissionResponse;
import org.example.asianpetssystem.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    /**
     * 获取角色列表
     *
     * @param pageRequest 分页请求
     * @return 分页响应
     */
    PageResponse<RoleResponse> getRoleList(PageRequest pageRequest);

    /**
     * 创建角色
     *
     * @param request 创建请求
     * @return 创建的角色响应
     */
    RoleResponse createRole(RoleCreateRequest request);

    /**
     * 更新角色
     *
     * @param id      角色ID
     * @param request 更新请求
     * @return 更新后的角色响应
     */
    RoleResponse updateRole(Long id, RoleUpdateRequest request);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 角色权限响应
     */
    RolePermissionResponse getRolePermissions(Long roleId);

    /**
     * 更新角色的权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void updateRolePermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取权限列表
     *
     * @return 权限列表
     */
    List<PermissionResponse> getPermissionList();
}
