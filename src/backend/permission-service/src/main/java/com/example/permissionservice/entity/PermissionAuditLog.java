package com.example.permissionservice.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PermissionAuditLog entity for tracking permission changes
 */
@Entity
@Table(name = "permission_audit_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "permission_level_id")
    private Long permissionLevelId;

    @Column(nullable = false, length = 20)
    private String action;

    @Column(name = "performed_by", nullable = false, length = 50)
    private String performedBy;

    @Column(name = "performed_at", nullable = false)
    @Builder.Default
    private LocalDateTime performedAt = LocalDateTime.now();

    @Column(name = "old_permission_level_id")
    private Long oldPermissionLevelId;

    @Column(name = "new_permission_level_id")
    private Long newPermissionLevelId;

    @Column(columnDefinition = "TEXT")
    private String details;
}
