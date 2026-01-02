package com.example.permissionservice.service;

import com.example.permissionservice.entity.PermissionAuditLog;
import com.example.permissionservice.repository.PermissionAuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for managing permission audit logs
 */
@Slf4j
@Service
public class AuditLogService {
    
    private final PermissionAuditLogRepository auditLogRepository;
    
    public AuditLogService(PermissionAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    /**
     * Log permission creation
     *
     * @param userId the user ID
     * @param applicationId the application ID
     * @param permissionLevelId the permission level ID
     * @param performedBy who performed the action
     */
    @Transactional
    public void logPermissionCreate(Long userId, Long applicationId, Long permissionLevelId, String performedBy) {
        log.info("Logging permission creation for userId: {}, applicationId: {}", userId, applicationId);
        
        PermissionAuditLog auditLog = PermissionAuditLog.builder()
                .userId(userId)
                .applicationId(applicationId)
                .permissionLevelId(permissionLevelId)
                .action("CREATE")
                .performedBy(performedBy)
                .performedAt(LocalDateTime.now())
                .newPermissionLevelId(permissionLevelId)
                .build();
        
        auditLogRepository.save(auditLog);
    }
    
    /**
     * Log permission update
     *
     * @param userId the user ID
     * @param applicationId the application ID
     * @param oldPermissionLevelId the old permission level ID
     * @param newPermissionLevelId the new permission level ID
     * @param performedBy who performed the action
     */
    @Transactional
    public void logPermissionUpdate(Long userId, Long applicationId, Long oldPermissionLevelId, 
                                   Long newPermissionLevelId, String performedBy) {
        log.info("Logging permission update for userId: {}, applicationId: {}", userId, applicationId);
        
        PermissionAuditLog auditLog = PermissionAuditLog.builder()
                .userId(userId)
                .applicationId(applicationId)
                .permissionLevelId(newPermissionLevelId)
                .action("UPDATE")
                .performedBy(performedBy)
                .performedAt(LocalDateTime.now())
                .oldPermissionLevelId(oldPermissionLevelId)
                .newPermissionLevelId(newPermissionLevelId)
                .build();
        
        auditLogRepository.save(auditLog);
    }
    
    /**
     * Log permission deletion
     *
     * @param userId the user ID
     * @param applicationId the application ID
     * @param permissionLevelId the permission level ID
     * @param performedBy who performed the action
     */
    @Transactional
    public void logPermissionDelete(Long userId, Long applicationId, Long permissionLevelId, String performedBy) {
        log.info("Logging permission deletion for userId: {}, applicationId: {}", userId, applicationId);
        
        PermissionAuditLog auditLog = PermissionAuditLog.builder()
                .userId(userId)
                .applicationId(applicationId)
                .permissionLevelId(permissionLevelId)
                .action("DELETE")
                .performedBy(performedBy)
                .performedAt(LocalDateTime.now())
                .oldPermissionLevelId(permissionLevelId)
                .build();
        
        auditLogRepository.save(auditLog);
    }
}
