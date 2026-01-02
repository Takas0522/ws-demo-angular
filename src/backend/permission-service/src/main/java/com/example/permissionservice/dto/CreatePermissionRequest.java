package com.example.permissionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a new user permission
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Application ID is required")
    private Long applicationId;
    
    @NotNull(message = "Permission Level ID is required")
    private Long permissionLevelId;
    
    private String grantedBy;
    
    private LocalDateTime expiresAt;
}
