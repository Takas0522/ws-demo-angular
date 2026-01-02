package com.example.authservice.service;

import com.example.authservice.dto.SagaStatusResponse;
import com.example.authservice.entity.SagaState;
import com.example.authservice.repository.SagaStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for saga operations
 */
@Slf4j
@Service
public class SagaService {
    
    private final SagaStateRepository sagaStateRepository;
    
    public SagaService(SagaStateRepository sagaStateRepository) {
        this.sagaStateRepository = sagaStateRepository;
    }
    
    /**
     * Get saga status by saga ID
     *
     * @param sagaId the saga ID
     * @return the saga status response
     */
    @Transactional(readOnly = true)
    public Optional<SagaStatusResponse> getSagaStatus(String sagaId) {
        log.debug("Getting saga status: sagaId={}", sagaId);
        
        return sagaStateRepository.findBySagaId(sagaId)
                .map(this::toResponse);
    }
    
    /**
     * Convert SagaState entity to SagaStatusResponse DTO
     *
     * @param sagaState the saga state entity
     * @return the saga status response DTO
     */
    private SagaStatusResponse toResponse(SagaState sagaState) {
        return SagaStatusResponse.builder()
                .sagaId(sagaState.getSagaId())
                .sagaType(sagaState.getSagaType())
                .currentStep(sagaState.getCurrentStep())
                .status(sagaState.getStatus())
                .payload(sagaState.getPayload())
                .errorMessage(sagaState.getErrorMessage())
                .createdAt(sagaState.getCreatedAt())
                .updatedAt(sagaState.getUpdatedAt())
                .build();
    }
}
