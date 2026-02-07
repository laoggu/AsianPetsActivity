// src/main/java/org/example/asianpetssystem/service/impl/RoleServiceImpl.java
package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.dto.request.RoleCreateRequest;
import org.example.asianpetssystem.dto.request.RoleUpdateRequest;
import org.example.asianpetssystem.dto.response.PermissionResponse;
import org.example.asianpetssystem.dto.response.RolePermissionResponse;
import org.example.asianpetssystem.dto.response.RoleResponse;
import org.example.asianpetssystem.entity.Permission;
import org.example.asianpetssystem.entity.Role;
import org.example.asianpetssystem.entity.RolePermission;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.PermissionRepository;
import org.example.asianpetssystem.repository.RolePermissionRepository;
import org.example.asianpetssystem.repository.RoleRepository;
import org.example.asianpetssystem.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public PageResponse<RoleResponse> getRoleList(PageRequest pageRequest) {
        logger.info("开始获取角色列表 - page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        long startTime = System.currentTimeMillis();

        try {
            Pageable pageable = createPageable(pageRequest);
            Page<Role> roles = roleRepository.findAll(pageable);

            List<RoleResponse> content = roles.getContent().stream()
                    .map(this::convertToRoleResponse)
                    .collect(Collectors.toList());

            PageResponse<RoleResponse> response = new PageResponse<>();
            response.setContent(content);
            response.setTotalElements(roles.getTotalElements());
            response.setTotalPages(roles.getTotalPages());
            response.setNumber(roles.getNumber());
            response.setSize(roles.getSize());
            response.setFirst(roles.isFirst());
            response.setLast(roles.isLast());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取角色列表成功 - 返回 {} 条记录, 耗时: {}ms", content.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取角色列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public RoleResponse createRole(RoleCreateRequest request) {
        logger.info("开始创建角色 - name={}, code={}", request.getName(), request.getCode());
        long startTime = System.currentTimeMillis();

        try {
            // 检查编码是否已存在
            if (roleRepository.existsByCode(request.getCode())) {
                throw new BusinessException(BusinessErrorEnum.ROLE_CODE_EXISTS);
            }

            Role role = new Role();
            role.setName(request.getName());
            role.setCode(request.getCode());
            role.setDescription(request.getDescription());
            role.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

            Role savedRole = roleRepository.save(role);
            RoleResponse response = convertToRoleResponse(savedRole);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("创建角色成功 - ID={}, 名称: {}, 耗时: {}ms", savedRole.getId(), savedRole.getName(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("创建角色失败 - 名称: {}, 耗时: {}ms, 错误: {}", request.getName(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public RoleResponse updateRole(Long id, RoleUpdateRequest request) {
        logger.info("开始更新角色 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ROLE_NOT_FOUND));

            role.setName(request.getName());
            role.setDescription(request.getDescription());
            if (request.getIsActive() != null) {
                role.setIsActive(request.getIsActive());
            }

            Role updatedRole = roleRepository.save(role);
            RoleResponse response = convertToRoleResponse(updatedRole);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新角色成功 - ID={}, 名称: {}, 耗时: {}ms", id, updatedRole.getName(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新角色失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteRole(Long id) {
        logger.info("开始删除角色 - ID={}", id);
        long startTime = System.currentTimeMillis();

        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ROLE_NOT_FOUND));

            // 删除角色关联的权限
            rolePermissionRepository.deleteByRoleId(id);

            // 删除角色
            roleRepository.delete(role);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("删除角色成功 - ID={}, 名称: {}, 耗时: {}ms", id, role.getName(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("删除角色失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public RolePermissionResponse getRolePermissions(Long roleId) {
        logger.info("开始获取角色权限 - roleId={}", roleId);
        long startTime = System.currentTimeMillis();

        try {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ROLE_NOT_FOUND));

            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(roleId);
            List<Long> permissionIds = rolePermissions.stream()
                    .map(RolePermission::getPermissionId)
                    .collect(Collectors.toList());

            List<Permission> permissions = permissionRepository.findAllById(permissionIds);
            List<PermissionResponse> permissionResponses = permissions.stream()
                    .map(this::convertToPermissionResponse)
                    .collect(Collectors.toList());

            RolePermissionResponse response = new RolePermissionResponse();
            response.setRoleId(role.getId());
            response.setRoleName(role.getName());
            response.setRoleCode(role.getCode());
            response.setPermissions(permissionResponses);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取角色权限成功 - roleId={}, 权限数: {}, 耗时: {}ms", roleId, permissionResponses.size(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取角色权限失败 - roleId={}, 耗时: {}ms, 错误: {}", roleId, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        logger.info("开始更新角色权限 - roleId={}, permissionIds={}", roleId, permissionIds);
        long startTime = System.currentTimeMillis();

        try {
            // 验证角色是否存在
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.ROLE_NOT_FOUND));

            // 验证所有权限ID是否有效
            if (permissionIds != null && !permissionIds.isEmpty()) {
                List<Permission> permissions = permissionRepository.findAllById(permissionIds);
                if (permissions.size() != permissionIds.size()) {
                    throw new BusinessException(BusinessErrorEnum.PERMISSION_NOT_FOUND);
                }
            }

            // 删除原有权限关联
            rolePermissionRepository.deleteByRoleId(roleId);

            // 创建新的权限关联
            if (permissionIds != null && !permissionIds.isEmpty()) {
                List<RolePermission> newRolePermissions = new ArrayList<>();
                for (Long permissionId : permissionIds) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setPermissionId(permissionId);
                    newRolePermissions.add(rolePermission);
                }
                rolePermissionRepository.saveAll(newRolePermissions);
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.info("更新角色权限成功 - roleId={}, 权限数: {}, 耗时: {}ms", roleId,
                    permissionIds != null ? permissionIds.size() : 0, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("更新角色权限失败 - roleId={}, 耗时: {}ms, 错误: {}", roleId, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PermissionResponse> getPermissionList() {
        logger.info("开始获取权限列表");
        long startTime = System.currentTimeMillis();

        try {
            List<Permission> permissions = permissionRepository.findAllByOrderByModuleAscCodeAsc();
            List<PermissionResponse> result = permissions.stream()
                    .map(this::convertToPermissionResponse)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取权限列表成功 - 返回 {} 条记录, 耗时: {}ms", result.size(), duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取权限列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
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
     * 转换为角色响应对象
     */
    private RoleResponse convertToRoleResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setCode(role.getCode());
        response.setDescription(role.getDescription());
        response.setIsActive(role.getIsActive());
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdatedAt());
        return response;
    }

    /**
     * 转换为权限响应对象
     */
    private PermissionResponse convertToPermissionResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setCode(permission.getCode());
        response.setDescription(permission.getDescription());
        response.setModule(permission.getModule());
        return response;
    }
}
