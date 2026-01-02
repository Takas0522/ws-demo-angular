package com.example.permissionservice.controller;

import com.example.permissionservice.dto.CreatePermissionRequest;
import com.example.permissionservice.dto.UpdatePermissionRequest;
import com.example.permissionservice.dto.UserPermissionDto;
import com.example.permissionservice.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for permission-related API endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class PermissionController {
    
    private final PermissionService permissionService;
    
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    /**
     * Get all permissions for a user
     * GET /api/users/{userId}/permissions
     *
     * @param userId the user ID
     * @return list of user permissions
     */
    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<List<UserPermissionDto>> getUserPermissions(@PathVariable Long userId) {
        log.info("GET /api/users/{}/permissions - Fetching user permissions", userId);
        
        List<UserPermissionDto> permissions = permissionService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }
    
    /**
     * Create a new permission
     * POST /api/permissions
     *
     * @param request the create request
     * @return created permission
     */
    @PostMapping("/permissions")
    public ResponseEntity<UserPermissionDto> createPermission(
            @Valid @RequestBody CreatePermissionRequest request) {
        log.info("POST /api/permissions - Creating permission for userId: {}, applicationId: {}", 
                request.getUserId(), request.getApplicationId());
        
        String performedBy = getAuthenticatedUsername();
        UserPermissionDto permission = permissionService.createPermission(request, performedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }
    
    /**
     * Update an existing permission
     * PUT /api/permissions/{permissionId}
     *
     * @param permissionId the permission ID
     * @param request the update request
     * @return updated permission
     */
    @PutMapping("/permissions/{permissionId}")
    public ResponseEntity<UserPermissionDto> updatePermission(
            @PathVariable Long permissionId,
            @Valid @RequestBody UpdatePermissionRequest request) {
        log.info("PUT /api/permissions/{} - Updating permission", permissionId);
        
        String performedBy = getAuthenticatedUsername();
        UserPermissionDto permission = permissionService.updatePermission(permissionId, request, performedBy);
        return ResponseEntity.ok(permission);
    }
    
    /**
     * Delete a permission
     * DELETE /api/permissions/{permissionId}
     *
     * @param permissionId the permission ID
     * @return no content
     */
    @DeleteMapping("/permissions/{permissionId}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long permissionId) {
        log.info("DELETE /api/permissions/{} - Deleting permission", permissionId);
        
        String performedBy = getAuthenticatedUsername();
        permissionService.deletePermission(permissionId, performedBy);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get authenticated username from security context
     *
     * @return username or "system" if not authenticated
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return authentication.getPrincipal().toString();
        }
        return "system";
    }
}
