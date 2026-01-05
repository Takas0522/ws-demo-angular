package com.example.authservice.controller;

import com.example.authservice.dto.*;
import com.example.authservice.entity.SagaState;
import com.example.authservice.saga.UserRegistrationSaga;
import com.example.authservice.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for authentication endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    private final UserRegistrationSaga userRegistrationSaga;
    
    public AuthController(AuthService authService, UserRegistrationSaga userRegistrationSaga) {
        this.authService = authService;
        this.userRegistrationSaga = userRegistrationSaga;
    }
    
    /**
     * Login endpoint
     * POST /api/auth/login
     *
     * @param request the login request
     * @return the authentication response with tokens
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - username: {}", request.getUsername());
        
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Refresh token endpoint
     * POST /api/auth/refresh
     *
     * @param request the refresh token request
     * @return the authentication response with new tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /api/auth/refresh");
        
        try {
            AuthResponse response = authService.refresh(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Logout endpoint
     * POST /api/auth/logout
     *
     * @param request the logout request
     * @return success response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("POST /api/auth/logout");
        
        try {
            authService.logout(request.getRefreshToken());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * User registration endpoint (async via saga)
     * POST /api/auth/register
     *
     * @param request the registration request
     * @return the registration response with saga ID
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - username: {}", request.getUsername());
        
        try {
            SagaState sagaState = userRegistrationSaga.execute(request);
            
            RegisterResponse response = RegisterResponse.builder()
                    .sagaId(sagaState.getSagaId())
                    .message(sagaState.getStatus() == SagaState.SagaStatus.COMPLETED 
                            ? "User registered successfully" 
                            : "User registration in progress. Check saga status with sagaId: " + sagaState.getSagaId())
                    .build();
            
            HttpStatus status = sagaState.getStatus() == SagaState.SagaStatus.COMPLETED 
                    ? HttpStatus.CREATED 
                    : HttpStatus.ACCEPTED;
            
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
