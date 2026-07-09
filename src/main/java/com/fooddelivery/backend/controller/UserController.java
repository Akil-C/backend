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

    @PutMapping("/admin/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }
}
