package com.example.authservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for entity classes
 */
class EntityTest {

    @Test
    void testUserEntity() {
        User user = User.builder()
                .username("testuser")
                .password("hashedpassword")
                .email("test@example.com")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertTrue(user.getEnabled());
    }

    @Test
    void testRefreshTokenEntity() {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("sample-token")
                .expiresAt(expiresAt)
                .build();

        assertNotNull(token);
        assertEquals(1L, token.getUserId());
        assertEquals("sample-token", token.getToken());
        assertFalse(token.isExpired());
    }

    @Test
    void testRefreshTokenExpired() {
        LocalDateTime expiresAt = LocalDateTime.now().minusDays(1);
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("expired-token")
                .expiresAt(expiresAt)
                .build();

        assertTrue(token.isExpired());
        
        // Test with explicit time
        LocalDateTime checkTime = expiresAt.plusHours(1);
        assertTrue(token.isExpired(checkTime));
        
        // Test not expired with earlier time
        LocalDateTime earlierTime = expiresAt.minusHours(1);
        assertFalse(token.isExpired(earlierTime));
    }

    @Test
    void testSagaStateEntity() {
        SagaState sagaState = SagaState.builder()
                .sagaId("saga-123")
                .sagaType("USER_REGISTRATION")
                .currentStep("CREATE_USER")
                .status(SagaState.SagaStatus.PENDING)
                .payload("{\"userId\": 123}")
                .build();

        assertNotNull(sagaState);
        assertEquals("saga-123", sagaState.getSagaId());
        assertEquals("USER_REGISTRATION", sagaState.getSagaType());
        assertEquals(SagaState.SagaStatus.PENDING, sagaState.getStatus());
    }

    @Test
    void testSagaStatusEnum() {
        assertEquals(6, SagaState.SagaStatus.values().length);
        assertNotNull(SagaState.SagaStatus.valueOf("PENDING"));
        assertNotNull(SagaState.SagaStatus.valueOf("IN_PROGRESS"));
        assertNotNull(SagaState.SagaStatus.valueOf("COMPLETED"));
        assertNotNull(SagaState.SagaStatus.valueOf("FAILED"));
        assertNotNull(SagaState.SagaStatus.valueOf("COMPENSATING"));
        assertNotNull(SagaState.SagaStatus.valueOf("COMPENSATED"));
    }
}
