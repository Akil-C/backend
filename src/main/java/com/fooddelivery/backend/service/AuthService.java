package com.fooddelivery.backend.service;

import com.fooddelivery.backend.dto.JwtAuthenticationResponse;
import com.fooddelivery.backend.dto.LoginRequest;
import com.fooddelivery.backend.dto.SignUpRequest;
import com.fooddelivery.backend.dto.TokenRefreshRequest;
import com.fooddelivery.backend.dto.TokenRefreshResponse;

public interface AuthService {
    JwtAuthenticationResponse login(LoginRequest loginRequest, String ipAddress);
    void register(SignUpRequest signUpRequest);
    TokenRefreshResponse refresh(TokenRefreshRequest tokenRefreshRequest);
    void logout(String email);
}
