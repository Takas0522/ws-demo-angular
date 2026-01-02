package com.example.sharedlib.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * JWT Token Validator for validating JWT tokens
 * Uses RSA public key for verification
 */
@Component
public class JwtValidator {

    @Value("${jwt.public-key-path:/keys/public_key.pem}")
    private String publicKeyPath;

    private PublicKey publicKey;

    /**
     * Validate JWT token and return claims
     *
     * @param token JWT token string
     * @return Claims if valid
     * @throws RuntimeException if token is invalid
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getPublicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException ex) {
            throw new RuntimeException("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            throw new RuntimeException("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            throw new RuntimeException("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("JWT claims string is empty", ex);
        }
    }

    /**
     * Extract subject from token
     *
     * @param token JWT token string
     * @return Subject from token
     */
    public String getSubject(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    /**
     * Check if token is expired
     *
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return false; // If validation succeeds without exception, token is not expired
        } catch (RuntimeException ex) {
            // If the exception message contains "Expired", the token is expired
            return ex.getMessage() != null && ex.getMessage().contains("Expired");
        }
    }

    /**
     * Extract specific claim from token
     *
     * @param token JWT token string
     * @param claimKey The key of the claim to extract
     * @return Claim value
     */
    public Object getClaim(String token, String claimKey) {
        Claims claims = validateToken(token);
        return claims.get(claimKey);
    }

    /**
     * Load and cache the public key
     *
     * @return PublicKey instance
     */
    private PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                publicKey = loadPublicKey(publicKeyPath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load public key from: " + publicKeyPath, e);
            }
        }
        return publicKey;
    }

    /**
     * Load public key from PEM file
     *
     * @param path Path to the public key file
     * @return PublicKey instance
     */
    private PublicKey loadPublicKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(path)));
        
        // Remove PEM headers and newlines
        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }
}
