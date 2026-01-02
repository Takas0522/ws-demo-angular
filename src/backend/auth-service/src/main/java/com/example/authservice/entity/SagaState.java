package com.example.authservice.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * SagaState entity for tracking distributed transaction state
 */
@Entity
@Table(name = "saga_state")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saga_id", nullable = false, unique = true, length = 100)
    private String sagaId;

    @Column(name = "saga_type", nullable = false, length = 50)
    private String sagaType;

    @Column(name = "current_step", nullable = false, length = 50)
    private String currentStep;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum representing saga status
     */
    public enum SagaStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        COMPENSATING,
        COMPENSATED
    }
}
