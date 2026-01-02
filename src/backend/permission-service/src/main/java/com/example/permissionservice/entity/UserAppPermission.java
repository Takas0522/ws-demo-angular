package com.example.permissionservice.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * UserAppPermission entity representing a user's permission for an application
 */
@Entity
@Table(name = "user_app_permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAppPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_level_id", nullable = false)
    private PermissionLevel permissionLevel;

    @Column(name = "granted_at", nullable = false)
    @Builder.Default
    private LocalDateTime grantedAt = LocalDateTime.now();

    @Column(name = "granted_by", length = 50)
    private String grantedBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
