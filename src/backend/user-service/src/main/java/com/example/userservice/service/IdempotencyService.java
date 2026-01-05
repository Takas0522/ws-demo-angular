package com.example.userservice.service;

import com.example.userservice.entity.IdempotencyKey;
import com.example.userservice.repository.IdempotencyKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for handling idempotency keys
 */
@Slf4j
@Service
public class IdempotencyService {
    
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    
    // Default expiration time: 24 hours
    private static final int DEFAULT_EXPIRATION_HOURS = 24;
    
    public IdempotencyService(IdempotencyKeyRepository idempotencyKeyRepository) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }
    
    /**
     * Check if idempotency key exists
     *
     * @param key the idempotency key
     * @return Optional of IdempotencyKey if exists
     */
    public Optional<IdempotencyKey> findByKey(String key) {
        return idempotencyKeyRepository.findByIdempotencyKey(key);
    }
    
    /**
     * Save idempotency key with response
     *
     * @param key the idempotency key
     * @param requestPath the request path
     * @param requestMethod the request method
     * @param statusCode the response status code
     * @param responseBody the response body
     * @return saved IdempotencyKey
     */
    @Transactional
    public IdempotencyKey saveKey(String key, String requestPath, String requestMethod, 
                                    Integer statusCode, String responseBody) {
        IdempotencyKey idempotencyKey = IdempotencyKey.builder()
                .idempotencyKey(key)
                .requestPath(requestPath)
                .requestMethod(requestMethod)
                .responseStatusCode(statusCode)
                .responseBody(responseBody)
                .expiresAt(LocalDateTime.now().plusHours(DEFAULT_EXPIRATION_HOURS))
                .build();
        
        return idempotencyKeyRepository.save(idempotencyKey);
    }
    
    /**
     * Clean up expired idempotency keys
     */
    @Transactional
    public void cleanupExpiredKeys() {
        log.info("Cleaning up expired idempotency keys");
        idempotencyKeyRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
