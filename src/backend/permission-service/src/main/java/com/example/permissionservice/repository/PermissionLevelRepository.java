package com.example.permissionservice.repository;

import com.example.permissionservice.entity.PermissionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PermissionLevel entity
 */
@Repository
public interface PermissionLevelRepository extends JpaRepository<PermissionLevel, Long> {
    
    /**
     * Find permission level by level code
     * @param levelCode the permission level code
     * @return Optional of PermissionLevel
     */
    Optional<PermissionLevel> findByLevelCode(String levelCode);
    
    /**
     * Check if permission level exists by level code
     * @param levelCode the permission level code
     * @return true if exists, false otherwise
     */
    boolean existsByLevelCode(String levelCode);
}
