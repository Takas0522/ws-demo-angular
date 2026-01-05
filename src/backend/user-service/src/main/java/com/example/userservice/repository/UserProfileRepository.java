package com.example.userservice.repository;

import com.example.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserProfile entity
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Find user profile by user ID
     * @param userId the user ID
     * @return Optional of UserProfile
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * Check if a user profile exists by user ID
     * @param userId the user ID
     * @return true if exists, false otherwise
     */
    boolean existsByUserId(Long userId);
}
