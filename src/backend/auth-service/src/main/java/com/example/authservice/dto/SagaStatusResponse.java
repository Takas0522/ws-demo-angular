package com.example.authservice.dto;

import com.example.authservice.entity.SagaState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for saga status response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStatusResponse {
    
    private String sagaId;
    private String sagaType;
    private String currentStep;
    private SagaState.SagaStatus status;
    private String payload;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
