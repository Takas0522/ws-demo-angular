package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserProfileRequest;
import com.example.userservice.dto.UpdateUserProfileRequest;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for public user profile API endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserProfileService userProfileService;
    
    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    /**
     * Get all user profiles
     * GET /api/users
     *
     * @return list of user profiles
     */
    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getAllUserProfiles() {
        log.info("GET /api/users - Fetching all user profiles");
        
        List<UserProfileDto> profiles = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(profiles);
    }
    
    /**
     * Get user profile by userId
     * GET /api/users/{userId}
     *
     * @param userId the user ID
     * @return user profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        log.info("GET /api/users/{} - Fetching user profile", userId);
        
        UserProfileDto profile = userProfileService.getUserProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }
    
    /**
     * Create user profile
     * POST /api/users
     *
     * @param request the create request
     * @return created user profile
     */
    @PostMapping
    public ResponseEntity<UserProfileDto> createUserProfile(
            @Valid @RequestBody CreateUserProfileRequest request) {
        log.info("POST /api/users - Creating user profile for userId: {}", request.getUserId());
        
        UserProfileDto profile = userProfileService.createOrUpdateUserProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }
    
    /**
     * Update user profile
     * PUT /api/users/{userId}
     *
     * @param userId the user ID
     * @param request the update request
     * @return updated user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("PUT /api/users/{} - Updating user profile", userId);
        
        UserProfileDto profile = userProfileService.updateUserProfile(userId, request);
        return ResponseEntity.ok(profile);
    }
    
    /**
     * Delete user profile
     * DELETE /api/users/{userId}
     *
     * @param userId the user ID
     * @return no content
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long userId) {
        log.info("DELETE /api/users/{} - Deleting user profile", userId);
        
        userProfileService.deleteUserProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
