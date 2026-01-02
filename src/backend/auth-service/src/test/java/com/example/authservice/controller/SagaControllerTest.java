package com.example.authservice.controller;

import com.example.authservice.dto.SagaStatusResponse;
import com.example.authservice.entity.SagaState;
import com.example.authservice.service.SagaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SagaController
 */
@WebMvcTest(SagaController.class)
class SagaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private SagaService sagaService;
    
    private SagaStatusResponse sagaStatusResponse;
    
    @BeforeEach
    void setUp() {
        sagaStatusResponse = SagaStatusResponse.builder()
                .sagaId("saga-123")
                .sagaType("USER_REGISTRATION")
                .currentStep("CREATE_USER")
                .status(SagaState.SagaStatus.IN_PROGRESS)
                .payload("{\"username\":\"testuser\"}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    @WithMockUser
    void testGetSagaStatusSuccess() throws Exception {
        // Arrange
        when(sagaService.getSagaStatus("saga-123")).thenReturn(Optional.of(sagaStatusResponse));
        
        // Act & Assert
        mockMvc.perform(get("/api/saga/saga-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sagaId").value("saga-123"))
                .andExpect(jsonPath("$.sagaType").value("USER_REGISTRATION"))
                .andExpect(jsonPath("$.currentStep").value("CREATE_USER"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }
    
    @Test
    @WithMockUser
    void testGetSagaStatusNotFound() throws Exception {
        // Arrange
        when(sagaService.getSagaStatus("saga-999")).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/saga/saga-999"))
                .andExpect(status().isNotFound());
    }
}
