package com.example.authservice.repository;

import com.example.authservice.entity.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for SagaState entity
 */
@Repository
public interface SagaStateRepository extends JpaRepository<SagaState, Long> {
    
    /**
     * Find saga by saga ID
     *
     * @param sagaId the saga ID
     * @return Optional containing the saga state if found
     */
    Optional<SagaState> findBySagaId(String sagaId);
    
    /**
     * Find sagas by status
     *
     * @param status the saga status
     * @return List of saga states with the given status
     */
    List<SagaState> findByStatus(SagaState.SagaStatus status);
    
    /**
     * Find sagas by type and status
     *
     * @param sagaType the saga type
     * @param status the saga status
     * @return List of saga states
     */
    List<SagaState> findBySagaTypeAndStatus(String sagaType, SagaState.SagaStatus status);
    
    /**
     * Delete completed sagas older than the specified date
     *
     * @param status the saga status
     * @param createdBefore the cutoff date
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM SagaState s WHERE s.status = :status AND s.createdAt < :createdBefore")
    int deleteByStatusAndCreatedAtBefore(SagaState.SagaStatus status, LocalDateTime createdBefore);
}
