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
 * PermissionLevel entity representing a permission level in the system
 */
@Entity
@Table(name = "permission_levels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_code", nullable = false, unique = true, length = 20)
    private String levelCode;

    @Column(name = "level_name", nullable = false, length = 50)
    private String levelName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "level_order", nullable = false)
    private Integer levelOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
