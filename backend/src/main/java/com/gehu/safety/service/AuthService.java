package com.gehu.safety.service;

import com.gehu.safety.dto.request.LoginRequest;
import com.gehu.safety.dto.request.RegisterRequest;
import com.gehu.safety.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshAccessToken(String refreshToken);
    void logout(String refreshToken);
}
