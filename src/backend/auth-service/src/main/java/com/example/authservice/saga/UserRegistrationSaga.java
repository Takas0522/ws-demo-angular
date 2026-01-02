package com.example.authservice.saga;

import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.entity.SagaState;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Saga for user registration with timeout and retry logic
 * - 30 second timeout
 * - 3 retry attempts
 */
@Slf4j
@Component
public class UserRegistrationSaga {
    
    private static final String SAGA_TYPE = "USER_REGISTRATION";
    private static final long TIMEOUT_SECONDS = 30;
    private static final int MAX_ATTEMPTS = 3;
    
    private final SagaOrchestrator sagaOrchestrator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    
    public UserRegistrationSaga(
            SagaOrchestrator sagaOrchestrator,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper) {
        this.sagaOrchestrator = sagaOrchestrator;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Execute user registration saga
     *
     * @param request the registration request
     * @return the saga state
     */
    public SagaState execute(RegisterRequest request) {
        SagaState sagaState = null;
        
        try {
            // Create saga
            String payload = objectMapper.writeValueAsString(request);
            sagaState = sagaOrchestrator.createSaga(SAGA_TYPE, payload);
            
            // Execute saga with timeout
            final SagaState finalSagaState = sagaState;
            CompletableFuture<SagaState> future = CompletableFuture.supplyAsync(() -> 
                executeSagaSteps(finalSagaState, request)
            );
            
            // Wait with timeout
            sagaState = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
        } catch (TimeoutException e) {
            log.error("Saga timed out: sagaId={}", sagaState != null ? sagaState.getSagaId() : "unknown");
            if (sagaState != null) {
                sagaState = sagaOrchestrator.markFailed(sagaState, "Saga execution timed out after " + TIMEOUT_SECONDS + " seconds");
            }
        } catch (Exception e) {
            log.error("Saga execution failed: sagaId={}, error={}", 
                    sagaState != null ? sagaState.getSagaId() : "unknown", e.getMessage(), e);
            if (sagaState != null) {
                sagaState = sagaOrchestrator.markFailed(sagaState, "Saga execution failed: " + e.getMessage());
            }
        }
        
        return sagaState;
    }
    
    /**
     * Execute saga steps
     *
     * @param sagaState the saga state
     * @param request the registration request
     * @return the updated saga state
     */
    private SagaState executeSagaSteps(SagaState sagaState, RegisterRequest request) {
        try {
            // Step 1: Validate user doesn't exist
            sagaState = sagaOrchestrator.markInProgress(sagaState, "VALIDATE_USER");
            validateUserDoesNotExist(request);
            
            // Step 2: Create user (with retry)
            sagaState = sagaOrchestrator.markInProgress(sagaState, "CREATE_USER");
            User user = createUserWithRetry(request);
            
            // Step 3: Send notification (simulated)
            sagaState = sagaOrchestrator.markInProgress(sagaState, "SEND_NOTIFICATION");
            sendWelcomeNotification(user);
            
            // Mark as completed
            sagaState = sagaOrchestrator.markCompleted(sagaState);
            
        } catch (Exception e) {
            log.error("Saga step failed: sagaId={}, step={}, error={}", 
                    sagaState.getSagaId(), sagaState.getCurrentStep(), e.getMessage());
            
            // Attempt compensation
            sagaState = compensate(sagaState, request);
        }
        
        return sagaState;
    }
    
    /**
     * Validate user doesn't exist
     *
     * @param request the registration request
     * @throws IllegalArgumentException if user already exists
     */
    private void validateUserDoesNotExist(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
    }
    
    /**
     * Create user with retry logic
     *
     * @param request the registration request
     * @return the created user
     */
    @Retryable(
        value = {Exception.class},
        maxAttempts = MAX_ATTEMPTS,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public User createUserWithRetry(RegisterRequest request) {
        log.info("Creating user: username={}", request.getUsername());
        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * Send welcome notification (simulated)
     *
     * @param user the created user
     */
    private void sendWelcomeNotification(User user) {
        // Simulate notification sending
        log.info("Sending welcome notification to: {}", user.getEmail());
        // In a real implementation, this would call a notification service
    }
    
    /**
     * Compensate for failed saga
     *
     * @param sagaState the saga state
     * @param request the registration request
     * @return the updated saga state
     */
    private SagaState compensate(SagaState sagaState, RegisterRequest request) {
        try {
            sagaState = sagaOrchestrator.markCompensating(sagaState);
            
            // Delete user if created
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(user -> {
                        log.info("Compensating: deleting user {}", user.getUsername());
                        userRepository.delete(user);
                    });
            
            sagaState = sagaOrchestrator.markCompensated(sagaState);
        } catch (Exception e) {
            log.error("Compensation failed: sagaId={}, error={}", sagaState.getSagaId(), e.getMessage());
            sagaState = sagaOrchestrator.markFailed(sagaState, "Compensation failed: " + e.getMessage());
        }
        
        return sagaState;
    }
}
