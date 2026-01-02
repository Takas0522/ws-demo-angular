package com.example.permissionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request DTO for updating an existing user permission
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionRequest {
    
    @NotNull(message = "Permission Level ID is required")
    private Long permissionLevelId;
    
    private LocalDateTime expiresAt;
    
    private String grantedBy;
}
