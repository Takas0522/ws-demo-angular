package com.example.authservice.service;

import com.example.authservice.entity.RefreshToken;
import com.example.authservice.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing refresh tokens
 */
@Slf4j
@Service
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private long refreshTokenExpiration;
    
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    
    /**
     * Create a new refresh token for a user
     *
     * @param userId the user ID
     * @return the created refresh token
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(expiresAt)
                .build();
        
        log.debug("Creating refresh token for user: {}", userId);
        return refreshTokenRepository.save(refreshToken);
    }
    
    /**
     * Find refresh token by token string
     *
     * @param token the token string
     * @return Optional containing the refresh token if found
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    /**
     * Verify and get refresh token
     *
     * @param token the token string
     * @return the refresh token if valid
     * @throws IllegalArgumentException if token is invalid or expired
     */
    public RefreshToken verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired");
        }
        
        return refreshToken;
    }
    
    /**
     * Delete refresh token
     *
     * @param token the token string
     */
    @Transactional
    public void deleteToken(String token) {
        log.debug("Deleting refresh token");
        refreshTokenRepository.deleteByToken(token);
    }
    
    /**
     * Delete all refresh tokens for a user
     *
     * @param userId the user ID
     */
    @Transactional
    public void deleteTokensByUserId(Long userId) {
        log.debug("Deleting all refresh tokens for user: {}", userId);
        refreshTokenRepository.deleteByUserId(userId);
    }
    
    /**
     * Clean up expired tokens
     */
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
