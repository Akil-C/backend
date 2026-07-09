package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.dto.*;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.register(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully. Please login."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        JwtAuthenticationResponse jwtResponse = authService.login(loginRequest, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest refreshRequest) {
        TokenRefreshResponse response = authService.refresh(refreshRequest);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.logout(email);
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
