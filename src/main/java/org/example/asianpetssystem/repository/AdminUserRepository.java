package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    Optional<AdminUser> findByUsername(String username);

    /**
     * 根据用户名模糊查询和状态查询用户列表（分页）
     *
     * @param username 用户名（模糊查询）
     * @param status   状态
     * @param pageable 分页对象
     * @return 用户分页列表
     */
    Page<AdminUser> findByUsernameContainingAndStatus(String username, Integer status, Pageable pageable);

    /**
     * 根据用户名模糊查询用户列表（分页），排除已删除用户
     *
     * @param username 用户名（模糊查询）
     * @param pageable 分页对象
     * @return 用户分页列表
     */
    @Query("SELECT u FROM AdminUser u WHERE u.username LIKE %:username% AND u.status <> -1")
    Page<AdminUser> findByUsernameContainingAndStatusNotDeleted(@Param("username") String username, Pageable pageable);

    /**
     * 根据状态查询用户列表（分页），排除已删除用户
     *
     * @param status   状态
     * @param pageable 分页对象
     * @return 用户分页列表
     */
    Page<AdminUser> findByStatus(Integer status, Pageable pageable);

    /**
     * 查询所有未删除的用户列表（分页）
     *
     * @param pageable 分页对象
     * @return 用户分页列表
     */
    @Query("SELECT u FROM AdminUser u WHERE u.status <> -1")
    Page<AdminUser> findAllActive(Pageable pageable);

    /**
     * 根据用户名和状态查询用户
     *
     * @param username 用户名
     * @param status   状态
     * @return 用户对象
     */
    Optional<AdminUser> findByUsernameAndStatus(String username, Integer status);

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
