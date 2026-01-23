package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.dto.request.LoginRequest;
import org.example.asianpetssystem.dto.request.RegisterRequest;
import org.example.asianpetssystem.dto.response.LoginResponse;
import org.example.asianpetssystem.entity.AdminUser;
import org.example.asianpetssystem.repository.AdminUserRepository;
import org.example.asianpetssystem.security.JwtTokenProvider;
import org.example.asianpetssystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 修复：使用正确的用户名生成token
        String jwt = jwtTokenProvider.generateToken(username);
        AdminUser user = (AdminUser) authentication.getPrincipal();

        return new LoginResponse(jwt, null, user.getRole(), user.getId());
    }

    @Override
    public void register(RegisterRequest request) {
        if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        AdminUser user = new AdminUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(1); // 启用状态

        adminUserRepository.save(user);
    }
}
