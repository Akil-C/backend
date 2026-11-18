package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.dto.UserDto;
import com.fooddelivery.backend.entity.User;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.UserRepository;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.fooddelivery.backend.repository.RoleRepository roleRepository;

    @Autowired
    private com.fooddelivery.backend.repository.LoginHistoryRepository loginHistoryRepository;

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", dto));
    }

    @PutMapping("/user/me")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserDto userDto) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        userRepository.save(user);

        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", dto));
    }

    // Admin Endpoints
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("All users retrieved", users));
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return ResponseEntity.ok(ApiResponse.success("User retrieved", user));
    }

    @PutMapping({"/admin/users/{id}/status", "/admin/users/{id}/toggle-status"})
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam(value = "active", required = false) Boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        if (active != null) {
            user.setIsActive(active);
        } else {
            user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        }
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
    }

    @PutMapping("/admin/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<User>> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", user));
    }

    @PutMapping("/admin/users/{id}/activate")
    public ResponseEntity<ApiResponse<User>> activateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        user.setIsActive(true);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", user));
    }

    @PutMapping("/admin/users/{id}/role")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable Long id,
            @RequestParam("role") String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        String cleanRole = roleName.trim().toUpperCase();
        if (!cleanRole.startsWith("ROLE_")) {
            cleanRole = "ROLE_" + cleanRole;
        }

        if ("ROLE_ADMIN".equals(cleanRole)) {
            com.fooddelivery.backend.entity.Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(com.fooddelivery.backend.entity.Role.builder().name("ROLE_ADMIN").build()));
            user.getRoles().add(adminRole);
        } else if ("ROLE_CUSTOMER".equals(cleanRole)) {
            user.getRoles().removeIf(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
            com.fooddelivery.backend.entity.Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseGet(() -> roleRepository.save(com.fooddelivery.backend.entity.Role.builder().name("ROLE_CUSTOMER").build()));
            user.getRoles().add(customerRole);
        } else {
            final String targetRole = cleanRole;
            com.fooddelivery.backend.entity.Role customRole = roleRepository.findByName(targetRole)
                    .orElseGet(() -> roleRepository.save(com.fooddelivery.backend.entity.Role.builder().name(targetRole).build()));
            user.getRoles().add(customRole);
        }

        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", user));
    }

    @GetMapping("/admin/users/tracking/logins")
    public ResponseEntity<ApiResponse<java.util.List<com.fooddelivery.backend.dto.LoginHistoryDto>>> getLoginTrackingAudits() {
        java.util.List<com.fooddelivery.backend.entity.LoginHistory> histories = loginHistoryRepository.findTop50ByOrderByLoginTimeDesc();
        java.util.List<com.fooddelivery.backend.dto.LoginHistoryDto> dtoList = histories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Recent login tracking audits retrieved", dtoList));
    }

    @GetMapping("/admin/users/{id}/logins")
    public ResponseEntity<ApiResponse<java.util.List<com.fooddelivery.backend.dto.LoginHistoryDto>>> getUserLoginHistory(@PathVariable Long id) {
        java.util.List<com.fooddelivery.backend.entity.LoginHistory> histories = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(id);
        java.util.List<com.fooddelivery.backend.dto.LoginHistoryDto> dtoList = histories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("User login history retrieved", dtoList));
    }

    private com.fooddelivery.backend.dto.LoginHistoryDto mapToDto(com.fooddelivery.backend.entity.LoginHistory lh) {
        return com.fooddelivery.backend.dto.LoginHistoryDto.builder()
                .id(lh.getId())
                .userId(lh.getUser() != null ? lh.getUser().getId() : null)
                .userName(lh.getUser() != null ? lh.getUser().getName() : "Unknown")
                .userEmail(lh.getUser() != null ? lh.getUser().getEmail() : "Unknown")
                .loginTime(lh.getLoginTime())
                .ipAddress(lh.getIpAddress())
                .status(lh.getStatus())
                .build();
    }
}
