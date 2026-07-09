package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.dto.*;
import com.fooddelivery.backend.entity.LoginHistory;
import com.fooddelivery.backend.entity.RefreshToken;
import com.fooddelivery.backend.entity.Role;
import com.fooddelivery.backend.entity.User;
import com.fooddelivery.backend.exception.BadRequestException;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.LoginHistoryRepository;
import com.fooddelivery.backend.repository.RefreshTokenRepository;
import com.fooddelivery.backend.repository.RoleRepository;
import com.fooddelivery.backend.repository.UserRepository;
import com.fooddelivery.backend.security.UserPrincipal;
import com.fooddelivery.backend.security.jwt.JwtTokenProvider;
import com.fooddelivery.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    @Override
    @Transactional
    public JwtAuthenticationResponse login(LoginRequest loginRequest, String ipAddress) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new BadRequestException("This account has been deactivated");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Create refresh token
            RefreshToken refreshToken = createRefreshToken(user);

            // Log Login History
            LoginHistory history = LoginHistory.builder()
                    .user(user)
                    .ipAddress(ipAddress)
                    .status("SUCCESS")
                    .build();
            loginHistoryRepository.save(history);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Set<String> roles = userPrincipal.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toSet());

            UserDto userDto = UserDto.builder()
                    .id(userPrincipal.getId())
                    .name(userPrincipal.getName())
                    .email(userPrincipal.getEmail())
                    .phone(user.getPhone())
                    .roles(roles)
                    .build();

            return JwtAuthenticationResponse.builder()
                    .accessToken(jwt)
                    .refreshToken(refreshToken.getToken())
                    .user(userDto)
                    .build();

        } catch (Exception ex) {
            LoginHistory history = LoginHistory.builder()
                    .user(user)
                    .ipAddress(ipAddress)
                    .status("FAILED")
                    .build();
            loginHistoryRepository.save(history);
            throw new BadRequestException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public void register(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email Address already in use!");
        }

        // Create new user
        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .phone(signUpRequest.getPhone())
                .isActive(true)
                .build();

        // Assign default customer role
        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build()));

        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest tokenRefreshRequest) {
        String requestRefreshToken = tokenRefreshRequest.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = tokenProvider.generateTokenFromUserId(user.getId());
                    // Rotate refresh token
                    refreshTokenRepository.delete(
                            refreshTokenRepository.findByToken(requestRefreshToken).get()
                    );
                    RefreshToken newRefreshToken = createRefreshToken(user);
                    return new TokenRefreshResponse(token, newRefreshToken.getToken());
                })
                .orElseThrow(() -> new BadRequestException("Refresh token is not in database!"));
    }

    @Override
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.deleteByUserEmail(email);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .token(UUID.randomUUID().toString())
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0 || token.getRevoked()) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
