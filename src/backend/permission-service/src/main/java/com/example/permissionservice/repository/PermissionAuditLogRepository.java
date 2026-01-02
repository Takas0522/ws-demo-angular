package com.example.permissionservice.repository;

import com.example.permissionservice.entity.PermissionAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for PermissionAuditLog entity
 */
@Repository
public interface PermissionAuditLogRepository extends JpaRepository<PermissionAuditLog, Long> {
    
    /**
     * Find all audit logs for a user
     * @param userId the user ID
     * @return List of PermissionAuditLog
     */
    List<PermissionAuditLog> findByUserId(Long userId);
    
    /**
     * Find all audit logs for an application
     * @param applicationId the application ID
     * @return List of PermissionAuditLog
     */
    List<PermissionAuditLog> findByApplicationId(Long applicationId);
}
