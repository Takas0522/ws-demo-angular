package com.example.userservice.repository;

import com.example.userservice.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for IdempotencyKey entity
 */
@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    
    /**
     * Find idempotency key by key value
     * @param idempotencyKey the key value
     * @return Optional of IdempotencyKey
     */
    Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * Delete all expired idempotency keys
     * @param now the current timestamp
     */
    void deleteByExpiresAtBefore(LocalDateTime now);
}
