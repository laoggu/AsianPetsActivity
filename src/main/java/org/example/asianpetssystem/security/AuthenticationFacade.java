package org.example.asianpetssystem.security;

import org.example.asianpetssystem.entity.AdminUser;
import org.example.asianpetssystem.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationFacade {

    @Autowired
    private AdminUserRepository adminUserRepository;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，如果未登录则返回null
     */
    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        Optional<AdminUser> user = adminUserRepository.findByUsername(username);
        return user.map(AdminUser::getId).orElse(null);
    }
}
