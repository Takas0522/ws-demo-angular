package com.example.permissionservice.service;

import com.example.permissionservice.dto.*;
import com.example.permissionservice.entity.Application;
import com.example.permissionservice.entity.PermissionLevel;
import com.example.permissionservice.entity.UserAppPermission;
import com.example.permissionservice.repository.ApplicationRepository;
import com.example.permissionservice.repository.PermissionLevelRepository;
import com.example.permissionservice.repository.UserAppPermissionRepository;
import com.example.sharedlib.exception.BadRequestException;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user permissions
 */
@Slf4j
@Service
public class PermissionService {
    
    private final UserAppPermissionRepository permissionRepository;
    private final ApplicationRepository applicationRepository;
    private final PermissionLevelRepository permissionLevelRepository;
    private final AuditLogService auditLogService;
    
    public PermissionService(UserAppPermissionRepository permissionRepository,
                           ApplicationRepository applicationRepository,
                           PermissionLevelRepository permissionLevelRepository,
                           AuditLogService auditLogService) {
        this.permissionRepository = permissionRepository;
        this.applicationRepository = applicationRepository;
        this.permissionLevelRepository = permissionLevelRepository;
        this.auditLogService = auditLogService;
    }
    
    /**
     * Get all permissions for a user
     *
     * @param userId the user ID
     * @return List of UserPermissionDto
     */
    @Transactional(readOnly = true)
    public List<UserPermissionDto> getUserPermissions(Long userId) {
        log.info("Fetching permissions for userId: {}", userId);
        
        List<UserAppPermission> permissions = permissionRepository.findByUserId(userId);
        return permissions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Create a new user permission
     *
     * @param request the create request
     * @param performedBy who performed the action
     * @return UserPermissionDto
     */
    @Transactional
    public UserPermissionDto createPermission(CreatePermissionRequest request, String performedBy) {
        log.info("Creating permission for userId: {}, applicationId: {}", 
                request.getUserId(), request.getApplicationId());
        
        // Check if permission already exists
        if (permissionRepository.existsByUserIdAndApplicationId(
                request.getUserId(), request.getApplicationId())) {
            throw new BadRequestException("Permission already exists for this user and application");
        }
        
        // Fetch application
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        
        // Fetch permission level
        PermissionLevel permissionLevel = permissionLevelRepository.findById(request.getPermissionLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Permission level not found"));
        
        // Create permission
        UserAppPermission permission = UserAppPermission.builder()
                .userId(request.getUserId())
                .application(application)
                .permissionLevel(permissionLevel)
                .grantedAt(LocalDateTime.now())
                .grantedBy(performedBy != null ? performedBy : request.getGrantedBy())
                .expiresAt(request.getExpiresAt())
                .build();
        
        permission = permissionRepository.save(permission);
        
        // Log the action
        auditLogService.logPermissionCreate(
                request.getUserId(),
                request.getApplicationId(),
                request.getPermissionLevelId(),
                performedBy != null ? performedBy : request.getGrantedBy()
        );
        
        return mapToDto(permission);
    }
    
    /**
     * Update an existing user permission
     *
     * @param permissionId the permission ID
     * @param request the update request
     * @param performedBy who performed the action
     * @return UserPermissionDto
     */
    @Transactional
    public UserPermissionDto updatePermission(Long permissionId, UpdatePermissionRequest request, String performedBy) {
        log.info("Updating permission with id: {}", permissionId);
        
        // Fetch existing permission
        UserAppPermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        
        Long oldPermissionLevelId = permission.getPermissionLevel().getId();
        
        // Fetch new permission level
        PermissionLevel newPermissionLevel = permissionLevelRepository.findById(request.getPermissionLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Permission level not found"));
        
        // Update permission
        permission.setPermissionLevel(newPermissionLevel);
        permission.setExpiresAt(request.getExpiresAt());
        if (request.getGrantedBy() != null) {
            permission.setGrantedBy(request.getGrantedBy());
        }
        
        permission = permissionRepository.save(permission);
        
        // Log the action
        auditLogService.logPermissionUpdate(
                permission.getUserId(),
                permission.getApplication().getId(),
                oldPermissionLevelId,
                request.getPermissionLevelId(),
                performedBy != null ? performedBy : request.getGrantedBy()
        );
        
        return mapToDto(permission);
    }
    
    /**
     * Delete a user permission
     *
     * @param permissionId the permission ID
     * @param performedBy who performed the action
     */
    @Transactional
    public void deletePermission(Long permissionId, String performedBy) {
        log.info("Deleting permission with id: {}", permissionId);
        
        // Fetch existing permission
        UserAppPermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        
        Long userId = permission.getUserId();
        Long applicationId = permission.getApplication().getId();
        Long permissionLevelId = permission.getPermissionLevel().getId();
        
        // Delete permission
        permissionRepository.delete(permission);
        
        // Log the action
        auditLogService.logPermissionDelete(
                userId,
                applicationId,
                permissionLevelId,
                performedBy
        );
    }
    
    /**
     * Map UserAppPermission entity to UserPermissionDto
     *
     * @param permission the UserAppPermission entity
     * @return UserPermissionDto
     */
    private UserPermissionDto mapToDto(UserAppPermission permission) {
        return UserPermissionDto.builder()
                .id(permission.getId())
                .userId(permission.getUserId())
                .application(mapApplicationToDto(permission.getApplication()))
                .permissionLevel(mapPermissionLevelToDto(permission.getPermissionLevel()))
                .grantedAt(permission.getGrantedAt())
                .grantedBy(permission.getGrantedBy())
                .expiresAt(permission.getExpiresAt())
                .build();
    }
    
    /**
     * Map Application entity to ApplicationDto
     *
     * @param application the Application entity
     * @return ApplicationDto
     */
    private ApplicationDto mapApplicationToDto(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
                .appCode(application.getAppCode())
                .appName(application.getAppName())
                .description(application.getDescription())
                .enabled(application.getEnabled())
                .build();
    }
    
    /**
     * Map PermissionLevel entity to PermissionLevelDto
     *
     * @param permissionLevel the PermissionLevel entity
     * @return PermissionLevelDto
     */
    private PermissionLevelDto mapPermissionLevelToDto(PermissionLevel permissionLevel) {
        return PermissionLevelDto.builder()
                .id(permissionLevel.getId())
                .levelCode(permissionLevel.getLevelCode())
                .levelName(permissionLevel.getLevelName())
                .description(permissionLevel.getDescription())
                .levelOrder(permissionLevel.getLevelOrder())
                .build();
    }
}
