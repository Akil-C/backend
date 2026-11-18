package com.fooddelivery.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginHistoryDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDateTime loginTime;
    private String ipAddress;
    private String status;
}
