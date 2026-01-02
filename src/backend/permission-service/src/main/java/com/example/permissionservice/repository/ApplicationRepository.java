package com.example.permissionservice.repository;

import com.example.permissionservice.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Application entity
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    /**
     * Find application by app code
     * @param appCode the application code
     * @return Optional of Application
     */
    Optional<Application> findByAppCode(String appCode);
    
    /**
     * Check if application exists by app code
     * @param appCode the application code
     * @return true if exists, false otherwise
     */
    boolean existsByAppCode(String appCode);
}
