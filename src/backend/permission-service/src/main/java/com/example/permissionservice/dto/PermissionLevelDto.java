package com.example.permissionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Permission Level information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionLevelDto {
    private Long id;
    private String levelCode;
    private String levelName;
    private String description;
    private Integer levelOrder;
}
