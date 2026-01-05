package com.example.permissionservice.repository;

import com.example.permissionservice.entity.UserAppPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserAppPermission entity
 */
@Repository
public interface UserAppPermissionRepository extends JpaRepository<UserAppPermission, Long> {
    
    /**
     * Find all permissions for a user
     * @param userId the user ID
     * @return List of UserAppPermission
     */
    List<UserAppPermission> findByUserId(Long userId);
    
    /**
     * Find permission by user ID and application ID
     * @param userId the user ID
     * @param applicationId the application ID
     * @return Optional of UserAppPermission
     */
    Optional<UserAppPermission> findByUserIdAndApplicationId(Long userId, Long applicationId);
    
    /**
     * Find all permissions for an application
     * @param applicationId the application ID
     * @return List of UserAppPermission
     */
    List<UserAppPermission> findByApplicationId(Long applicationId);
    
    /**
     * Check if user has permission for application
     * @param userId the user ID
     * @param applicationId the application ID
     * @return true if exists, false otherwise
     */
    boolean existsByUserIdAndApplicationId(Long userId, Long applicationId);
}
