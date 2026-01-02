package com.example.authservice.saga;

import com.example.authservice.entity.SagaState;
import com.example.authservice.repository.SagaStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Saga orchestrator for managing saga lifecycle
 */
@Slf4j
@Component
public class SagaOrchestrator {
    
    private final SagaStateRepository sagaStateRepository;
    
    public SagaOrchestrator(SagaStateRepository sagaStateRepository) {
        this.sagaStateRepository = sagaStateRepository;
    }
    
    /**
     * Create a new saga
     *
     * @param sagaType the type of saga
     * @param payload the payload data
     * @return the created saga state
     */
    @Transactional
    public SagaState createSaga(String sagaType, String payload) {
        String sagaId = UUID.randomUUID().toString();
        
        SagaState sagaState = SagaState.builder()
                .sagaId(sagaId)
                .sagaType(sagaType)
                .currentStep("INIT")
                .status(SagaState.SagaStatus.PENDING)
                .payload(payload)
                .build();
        
        log.info("Creating saga: sagaId={}, type={}", sagaId, sagaType);
        return sagaStateRepository.save(sagaState);
    }
    
    /**
     * Update saga state
     *
     * @param sagaState the saga state to update
     * @return the updated saga state
     */
    @Transactional
    public SagaState updateSaga(SagaState sagaState) {
        log.debug("Updating saga: sagaId={}, step={}, status={}", 
                sagaState.getSagaId(), sagaState.getCurrentStep(), sagaState.getStatus());
        return sagaStateRepository.save(sagaState);
    }
    
    /**
     * Mark saga as in progress
     *
     * @param sagaState the saga state
     * @param step the current step
     * @return the updated saga state
     */
    @Transactional
    public SagaState markInProgress(SagaState sagaState, String step) {
        sagaState.setCurrentStep(step);
        sagaState.setStatus(SagaState.SagaStatus.IN_PROGRESS);
        return updateSaga(sagaState);
    }
    
    /**
     * Mark saga as completed
     *
     * @param sagaState the saga state
     * @return the updated saga state
     */
    @Transactional
    public SagaState markCompleted(SagaState sagaState) {
        sagaState.setStatus(SagaState.SagaStatus.COMPLETED);
        log.info("Saga completed: sagaId={}", sagaState.getSagaId());
        return updateSaga(sagaState);
    }
    
    /**
     * Mark saga as failed
     *
     * @param sagaState the saga state
     * @param errorMessage the error message
     * @return the updated saga state
     */
    @Transactional
    public SagaState markFailed(SagaState sagaState, String errorMessage) {
        sagaState.setStatus(SagaState.SagaStatus.FAILED);
        sagaState.setErrorMessage(errorMessage);
        log.error("Saga failed: sagaId={}, error={}", sagaState.getSagaId(), errorMessage);
        return updateSaga(sagaState);
    }
    
    /**
     * Mark saga as compensating
     *
     * @param sagaState the saga state
     * @return the updated saga state
     */
    @Transactional
    public SagaState markCompensating(SagaState sagaState) {
        sagaState.setStatus(SagaState.SagaStatus.COMPENSATING);
        log.info("Saga compensating: sagaId={}", sagaState.getSagaId());
        return updateSaga(sagaState);
    }
    
    /**
     * Mark saga as compensated
     *
     * @param sagaState the saga state
     * @return the updated saga state
     */
    @Transactional
    public SagaState markCompensated(SagaState sagaState) {
        sagaState.setStatus(SagaState.SagaStatus.COMPENSATED);
        log.info("Saga compensated: sagaId={}", sagaState.getSagaId());
        return updateSaga(sagaState);
    }
}
