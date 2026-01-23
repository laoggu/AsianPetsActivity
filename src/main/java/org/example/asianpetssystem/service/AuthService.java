package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.request.LoginRequest;
import org.example.asianpetssystem.dto.request.RegisterRequest;
import org.example.asianpetssystem.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(String username, String password);
    void register(RegisterRequest request);
}
