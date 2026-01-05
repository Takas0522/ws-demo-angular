package com.example.authservice.controller;

import com.example.authservice.dto.*;
import com.example.authservice.entity.SagaState;
import com.example.authservice.saga.UserRegistrationSaga;
import com.example.authservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthService authService;
    
    @MockBean
    private UserRegistrationSaga userRegistrationSaga;
    
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    
    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
        
        authResponse = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .userId(1L)
                .username("testuser")
                .build();
    }
    
    @Test
    @WithMockUser
    void testLoginSuccess() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
    
    @Test
    @WithMockUser
    void testLoginInvalidCredentials() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid username or password"));
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser
    void testRefreshSuccess() throws Exception {
        // Arrange
        RefreshTokenRequest refreshRequest = RefreshTokenRequest.builder()
                .refreshToken("refresh-token")
                .build();
        
        when(authService.refresh("refresh-token")).thenReturn(authResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }
    
    @Test
    @WithMockUser
    void testLogoutSuccess() throws Exception {
        // Arrange
        LogoutRequest logoutRequest = LogoutRequest.builder()
                .refreshToken("refresh-token")
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser
    void testRegisterSuccess() throws Exception {
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("newuser")
                .password("password123")
                .email("newuser@example.com")
                .build();
        
        SagaState sagaState = SagaState.builder()
                .sagaId("saga-123")
                .status(SagaState.SagaStatus.COMPLETED)
                .build();
        
        when(userRegistrationSaga.execute(any(RegisterRequest.class))).thenReturn(sagaState);
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sagaId").value("saga-123"))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }
}
