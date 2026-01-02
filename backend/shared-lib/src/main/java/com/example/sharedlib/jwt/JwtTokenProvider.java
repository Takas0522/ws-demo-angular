package com.example.sharedlib.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT Token Provider for generating JWT tokens
 * Uses RSA private key for signing
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.private-key-path:/keys/private_key.pem}")
    private String privateKeyPath;

    @Value("${jwt.expiration:3600000}") // Default 1 hour
    private long jwtExpiration;

    private PrivateKey privateKey;

    /**
     * Generate JWT token with claims
     *
     * @param subject The subject (typically user ID)
     * @param claims  Additional claims to include in the token
     * @return JWT token string
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    /**
     * Generate JWT token with subject only
     *
     * @param subject The subject (typically user ID)
     * @return JWT token string
     */
    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    /**
     * Load and cache the private key
     *
     * @return PrivateKey instance
     */
    private PrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                privateKey = loadPrivateKey(privateKeyPath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load private key from: " + privateKeyPath, e);
            }
        }
        return privateKey;
    }

    /**
     * Load private key from PEM file
     *
     * @param path Path to the private key file
     * @return PrivateKey instance
     */
    private PrivateKey loadPrivateKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(path)));
        
        // Remove PEM headers and newlines
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }
}
