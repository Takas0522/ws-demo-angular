package com.example.permissionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for User Permission information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionDto {
    private Long id;
    private Long userId;
    private ApplicationDto application;
    private PermissionLevelDto permissionLevel;
    private LocalDateTime grantedAt;
    private String grantedBy;
    private LocalDateTime expiresAt;
}
