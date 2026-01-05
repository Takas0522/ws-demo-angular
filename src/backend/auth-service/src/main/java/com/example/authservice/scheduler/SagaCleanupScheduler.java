package com.example.authservice.scheduler;

import com.example.authservice.entity.SagaState;
import com.example.authservice.repository.SagaStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduler for cleaning up old saga records
 * Deletes completed sagas older than 30 days
 */
@Slf4j
@Component
public class SagaCleanupScheduler {
    
    private static final int RETENTION_DAYS = 30;
    
    private final SagaStateRepository sagaStateRepository;
    
    public SagaCleanupScheduler(SagaStateRepository sagaStateRepository) {
        this.sagaStateRepository = sagaStateRepository;
    }
    
    /**
     * Clean up old completed sagas
     * Runs daily at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldSagas() {
        log.info("Starting saga cleanup job");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RETENTION_DAYS);
            
            int deletedCount = sagaStateRepository.deleteByStatusAndCreatedAtBefore(
                    SagaState.SagaStatus.COMPLETED, 
                    cutoffDate
            );
            
            log.info("Saga cleanup completed. Deleted {} completed sagas older than {} days", 
                    deletedCount, RETENTION_DAYS);
        } catch (Exception e) {
            log.error("Error during saga cleanup: {}", e.getMessage(), e);
        }
    }
}
