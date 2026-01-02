package com.example.sharedlib.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JWT token provider and validator
 */
class JwtTokenTest {

    @TempDir
    Path tempDir;

    private JwtTokenProvider tokenProvider;
    private JwtValidator tokenValidator;
    private Path privateKeyPath;
    private Path publicKeyPath;

    @BeforeEach
    void setUp() throws Exception {
        // Generate test RSA key pair
        privateKeyPath = tempDir.resolve("private_key.pem");
        publicKeyPath = tempDir.resolve("public_key.pem");
        generateTestKeys();

        // Initialize JWT components
        tokenProvider = new JwtTokenProvider();
        tokenValidator = new JwtValidator();

        // Set key paths using reflection
        ReflectionTestUtils.setField(tokenProvider, "privateKeyPath", privateKeyPath.toString());
        ReflectionTestUtils.setField(tokenValidator, "publicKeyPath", publicKeyPath.toString());
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 3600000L); // 1 hour
    }

    @Test
    void testGenerateAndValidateToken() {
        // Given
        String subject = "user123";
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("USER", "ADMIN"));
        claims.put("email", "user@example.com");

        // When
        String token = tokenProvider.generateToken(subject, claims);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Validate token
        Claims validatedClaims = tokenValidator.validateToken(token);
        assertEquals(subject, validatedClaims.getSubject());
        assertEquals("user@example.com", validatedClaims.get("email"));
        assertNotNull(validatedClaims.get("roles"));
    }

    @Test
    void testGenerateTokenWithoutClaims() {
        // Given
        String subject = "user456";

        // When
        String token = tokenProvider.generateToken(subject);

        // Then
        assertNotNull(token);
        String extractedSubject = tokenValidator.getSubject(token);
        assertEquals(subject, extractedSubject);
    }

    @Test
    void testGetSubject() {
        // Given
        String subject = "testUser";
        String token = tokenProvider.generateToken(subject);

        // When
        String extractedSubject = tokenValidator.getSubject(token);

        // Then
        assertEquals(subject, extractedSubject);
    }

    @Test
    void testGetClaim() {
        // Given
        String subject = "user789";
        Map<String, Object> claims = new HashMap<>();
        claims.put("department", "Engineering");
        String token = tokenProvider.generateToken(subject, claims);

        // When
        Object department = tokenValidator.getClaim(token, "department");

        // Then
        assertEquals("Engineering", department);
    }

    @Test
    void testIsTokenExpired() {
        // Given
        String subject = "user999";
        String token = tokenProvider.generateToken(subject);

        // When
        boolean isExpired = tokenValidator.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void testInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(RuntimeException.class, () -> tokenValidator.validateToken(invalidToken));
    }

    /**
     * Generate test RSA key pair
     */
    private void generateTestKeys() throws IOException, InterruptedException {
        // Generate private key
        ProcessBuilder privateKeyBuilder = new ProcessBuilder(
                "openssl", "genrsa", "-out", privateKeyPath.toString(), "2048"
        );
        Process privateKeyProcess = privateKeyBuilder.start();
        privateKeyProcess.waitFor();

        // Generate public key
        ProcessBuilder publicKeyBuilder = new ProcessBuilder(
                "openssl", "rsa", "-in", privateKeyPath.toString(),
                "-pubout", "-out", publicKeyPath.toString()
        );
        Process publicKeyProcess = publicKeyBuilder.start();
        publicKeyProcess.waitFor();
    }
}
