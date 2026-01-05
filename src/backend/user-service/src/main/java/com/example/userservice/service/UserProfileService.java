package com.example.userservice.service;

import com.example.sharedlib.exception.BadRequestException;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.userservice.dto.CreateUserProfileRequest;
import com.example.userservice.dto.UpdateUserProfileRequest;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for user profile operations
 */
@Slf4j
@Service
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }
    
    /**
     * Create user profile (internal API)
     *
     * @param request the create request
     * @return created UserProfile entity
     */
    @Transactional
    public UserProfile createUserProfile(CreateUserProfileRequest request) {
        log.info("Creating user profile for userId: {}", request.getUserId());
        
        // Check if profile already exists
        if (userProfileRepository.existsByUserId(request.getUserId())) {
            throw new BadRequestException("User profile already exists for userId: " + request.getUserId());
        }
        
        UserProfile userProfile = UserProfile.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .displayName(request.getDisplayName())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .build();
        
        UserProfile saved = userProfileRepository.save(userProfile);
        log.info("User profile created with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Delete user profile by userId (compensate operation)
     *
     * @param userId the user ID
     */
    @Transactional
    public void deleteUserProfileByUserId(Long userId) {
        log.info("Deleting user profile for userId: {}", userId);
        
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));
        
        userProfileRepository.delete(userProfile);
        log.info("User profile deleted for userId: {}", userId);
    }
    
    /**
     * Get all user profiles
     *
     * @return list of UserProfileDto
     */
    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllUserProfiles() {
        log.info("Fetching all user profiles");
        
        return userProfileRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user profile by userId
     *
     * @param userId the user ID
     * @return UserProfileDto
     */
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileByUserId(Long userId) {
        log.info("Fetching user profile for userId: {}", userId);
        
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));
        
        return convertToDto(userProfile);
    }
    
    /**
     * Create or update user profile (public API)
     *
     * @param request the create request
     * @return created/updated UserProfileDto
     */
    @Transactional
    public UserProfileDto createOrUpdateUserProfile(CreateUserProfileRequest request) {
        log.info("Creating/updating user profile for userId: {}", request.getUserId());
        
        UserProfile userProfile = userProfileRepository.findByUserId(request.getUserId())
                .orElse(UserProfile.builder().userId(request.getUserId()).build());
        
        // Update fields
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setDisplayName(request.getDisplayName());
        userProfile.setBio(request.getBio());
        userProfile.setAvatarUrl(request.getAvatarUrl());
        userProfile.setPhoneNumber(request.getPhoneNumber());
        userProfile.setDateOfBirth(request.getDateOfBirth());
        userProfile.setAddress(request.getAddress());
        userProfile.setCity(request.getCity());
        userProfile.setState(request.getState());
        userProfile.setCountry(request.getCountry());
        userProfile.setPostalCode(request.getPostalCode());
        
        UserProfile saved = userProfileRepository.save(userProfile);
        log.info("User profile saved with id: {}", saved.getId());
        
        return convertToDto(saved);
    }
    
    /**
     * Update user profile
     *
     * @param userId the user ID
     * @param request the update request
     * @return updated UserProfileDto
     */
    @Transactional
    public UserProfileDto updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        log.info("Updating user profile for userId: {}", userId);
        
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));
        
        // Update only non-null fields
        if (request.getFirstName() != null) {
            userProfile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            userProfile.setLastName(request.getLastName());
        }
        if (request.getDisplayName() != null) {
            userProfile.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            userProfile.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            userProfile.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getPhoneNumber() != null) {
            userProfile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            userProfile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAddress() != null) {
            userProfile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            userProfile.setCity(request.getCity());
        }
        if (request.getState() != null) {
            userProfile.setState(request.getState());
        }
        if (request.getCountry() != null) {
            userProfile.setCountry(request.getCountry());
        }
        if (request.getPostalCode() != null) {
            userProfile.setPostalCode(request.getPostalCode());
        }
        
        UserProfile saved = userProfileRepository.save(userProfile);
        log.info("User profile updated for userId: {}", userId);
        
        return convertToDto(saved);
    }
    
    /**
     * Delete user profile by userId
     *
     * @param userId the user ID
     */
    @Transactional
    public void deleteUserProfile(Long userId) {
        log.info("Deleting user profile for userId: {}", userId);
        
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));
        
        userProfileRepository.delete(userProfile);
        log.info("User profile deleted for userId: {}", userId);
    }
    
    /**
     * Convert UserProfile entity to DTO
     *
     * @param userProfile the entity
     * @return UserProfileDto
     */
    private UserProfileDto convertToDto(UserProfile userProfile) {
        return UserProfileDto.builder()
                .id(userProfile.getId())
                .userId(userProfile.getUserId())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .displayName(userProfile.getDisplayName())
                .bio(userProfile.getBio())
                .avatarUrl(userProfile.getAvatarUrl())
                .phoneNumber(userProfile.getPhoneNumber())
                .dateOfBirth(userProfile.getDateOfBirth())
                .address(userProfile.getAddress())
                .city(userProfile.getCity())
                .state(userProfile.getState())
                .country(userProfile.getCountry())
                .postalCode(userProfile.getPostalCode())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .build();
    }
}
