package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserProfileRequest;
import com.example.userservice.dto.CreateUserProfileResponse;
import com.example.userservice.entity.IdempotencyKey;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.service.IdempotencyService;
import com.example.userservice.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Controller for internal user profile API endpoints
 * Handles idempotency for saga operations
 */
@Slf4j
@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    
    private final UserProfileService userProfileService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;
    
    public InternalUserController(UserProfileService userProfileService,
                                   IdempotencyService idempotencyService,
                                   ObjectMapper objectMapper) {
        this.userProfileService = userProfileService;
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Create user profile with idempotency support
     * POST /internal/users
     *
     * @param idempotencyKey X-Idempotency-Key header
     * @param request the create request
     * @return created user profile response
     */
    @PostMapping
    public ResponseEntity<CreateUserProfileResponse> createUserProfile(
            @RequestHeader(value = "X-Idempotency-Key", required = true) String idempotencyKey,
            @Valid @RequestBody CreateUserProfileRequest request) {
        
        log.info("POST /internal/users - userId: {}, idempotencyKey: {}", 
                request.getUserId(), idempotencyKey);
        
        // Check if idempotency key already exists
        Optional<IdempotencyKey> existingKey = idempotencyService.findByKey(idempotencyKey);
        if (existingKey.isPresent()) {
            log.info("Idempotency key already exists, returning cached response");
            IdempotencyKey key = existingKey.get();
            
            try {
                CreateUserProfileResponse cachedResponse = objectMapper.readValue(
                        key.getResponseBody(), CreateUserProfileResponse.class);
                return ResponseEntity.status(key.getResponseStatusCode()).body(cachedResponse);
            } catch (Exception e) {
                log.error("Failed to deserialize cached response for idempotency key: {}", idempotencyKey, e);
                CreateUserProfileResponse errorResponse = CreateUserProfileResponse.builder()
                        .message("Failed to retrieve cached response")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
        
        try {
            // Create user profile
            UserProfile userProfile = userProfileService.createUserProfile(request);
            
            CreateUserProfileResponse response = CreateUserProfileResponse.builder()
                    .id(userProfile.getId())
                    .userId(userProfile.getUserId())
                    .message("User profile created successfully")
                    .build();
            
            // Save idempotency key with response
            String responseBody = objectMapper.writeValueAsString(response);
            idempotencyService.saveKey(idempotencyKey, "/internal/users", "POST", 
                    HttpStatus.CREATED.value(), responseBody);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to create user profile", e);
            
            CreateUserProfileResponse errorResponse = CreateUserProfileResponse.builder()
                    .message("Failed to create user profile: " + e.getMessage())
                    .build();
            
            try {
                String responseBody = objectMapper.writeValueAsString(errorResponse);
                idempotencyService.saveKey(idempotencyKey, "/internal/users", "POST", 
                        HttpStatus.BAD_REQUEST.value(), responseBody);
            } catch (Exception ex) {
                log.error("Failed to save error response for idempotency", ex);
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * Delete user profile (compensate operation)
     * DELETE /internal/users/{userId}/compensate
     *
     * @param userId the user ID
     * @return no content
     */
    @DeleteMapping("/{userId}/compensate")
    public ResponseEntity<Void> compensateUserProfile(@PathVariable Long userId) {
        log.info("DELETE /internal/users/{}/compensate - Compensating user profile", userId);
        
        try {
            userProfileService.deleteUserProfileByUserId(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to compensate user profile for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
