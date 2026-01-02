package com.example.authservice.saga;

import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.entity.SagaState;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserRegistrationSaga
 */
@ExtendWith(MockitoExtension.class)
class UserRegistrationSagaTest {
    
    @Mock
    private SagaOrchestrator sagaOrchestrator;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserRegistrationSaga userRegistrationSaga;
    
    private ObjectMapper objectMapper;
    private RegisterRequest registerRequest;
    private SagaState sagaState;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userRegistrationSaga = new UserRegistrationSaga(
                sagaOrchestrator, userRepository, passwordEncoder, objectMapper);
        
        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .password("password123")
                .email("newuser@example.com")
                .build();
        
        sagaState = SagaState.builder()
                .id(1L)
                .sagaId("saga-123")
                .sagaType("USER_REGISTRATION")
                .currentStep("INIT")
                .status(SagaState.SagaStatus.PENDING)
                .build();
    }
    
    @Test
    void testExecuteSagaSuccess() throws Exception {
        // Arrange
        String payload = objectMapper.writeValueAsString(registerRequest);
        when(sagaOrchestrator.createSaga(anyString(), eq(payload))).thenReturn(sagaState);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword");
        
        User createdUser = User.builder()
                .id(1L)
                .username("newuser")
                .password("$2a$10$hashedpassword")
                .email("newuser@example.com")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(createdUser);
        
        SagaState inProgressState = SagaState.builder()
                .sagaId("saga-123")
                .status(SagaState.SagaStatus.IN_PROGRESS)
                .currentStep("VALIDATE_USER")
                .build();
        when(sagaOrchestrator.markInProgress(any(), anyString())).thenReturn(inProgressState);
        
        SagaState completedState = SagaState.builder()
                .sagaId("saga-123")
                .status(SagaState.SagaStatus.COMPLETED)
                .build();
        when(sagaOrchestrator.markCompleted(any())).thenReturn(completedState);
        
        // Act
        SagaState result = userRegistrationSaga.execute(registerRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(SagaState.SagaStatus.COMPLETED, result.getStatus());
        
        verify(sagaOrchestrator).createSaga(eq("USER_REGISTRATION"), anyString());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(sagaOrchestrator, atLeastOnce()).markInProgress(any(), anyString());
        verify(sagaOrchestrator).markCompleted(any());
    }
    
    @Test
    void testExecuteSagaUserAlreadyExists() throws Exception {
        // Arrange
        String payload = objectMapper.writeValueAsString(registerRequest);
        when(sagaOrchestrator.createSaga(anyString(), eq(payload))).thenReturn(sagaState);
        when(userRepository.existsByUsername("newuser")).thenReturn(true);
        
        SagaState inProgressState = SagaState.builder()
                .sagaId("saga-123")
                .status(SagaState.SagaStatus.IN_PROGRESS)
                .currentStep("VALIDATE_USER")
                .build();
        when(sagaOrchestrator.markInProgress(any(), anyString())).thenReturn(inProgressState);
        
        SagaState compensatingState = SagaState.builder()
                .sagaId("saga-123")
                .status(SagaState.SagaStatus.COMPENSATING)
                .build();
        when(sagaOrchestrator.markCompensating(any())).thenReturn(compensatingState);
        
        SagaState compensatedState = SagaState.builder()
                .sagaId("saga-123")
                .status(SagaState.SagaStatus.COMPENSATED)
                .build();
        when(sagaOrchestrator.markCompensated(any())).thenReturn(compensatedState);
        
        // Act
        SagaState result = userRegistrationSaga.execute(registerRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(SagaState.SagaStatus.COMPENSATED, result.getStatus());
        
        verify(sagaOrchestrator).createSaga(eq("USER_REGISTRATION"), anyString());
        verify(userRepository).existsByUsername("newuser");
    }
}
