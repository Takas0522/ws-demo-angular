package com.example.authservice.service;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.example.sharedlib.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for authentication operations
 */
@Slf4j
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${jwt.expiration:3600000}") // 1 hour default
    private long jwtExpiration;
    
    public AuthService(
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    /**
     * Authenticate user and generate tokens
     *
     * @param request the login request
     * @return the authentication response with tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        // Find user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        
        // Check account status
        if (!user.getEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }
        if (!user.getAccountNonLocked()) {
            throw new IllegalArgumentException("Account is locked");
        }
        if (!user.getAccountNonExpired()) {
            throw new IllegalArgumentException("Account has expired");
        }
        if (!user.getCredentialsNonExpired()) {
            throw new IllegalArgumentException("Credentials have expired");
        }
        
        // Generate tokens
        String accessToken = generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000) // Convert to seconds
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
    
    /**
     * Refresh access token using refresh token
     *
     * @param refreshTokenStr the refresh token string
     * @return the authentication response with new tokens
     */
    @Transactional
    public AuthResponse refresh(String refreshTokenStr) {
        log.info("Refreshing access token");
        
        // Verify refresh token
        RefreshToken refreshToken = refreshTokenService.verifyToken(refreshTokenStr);
        
        // Get user
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check account status
        if (!user.getEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }
        
        // Generate new access token
        String accessToken = generateAccessToken(user);
        
        // Optionally rotate refresh token (create new one and delete old)
        refreshTokenService.deleteToken(refreshTokenStr);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        log.info("Access token refreshed for user: {}", user.getUsername());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
    
    /**
     * Logout user by invalidating refresh token
     *
     * @param refreshToken the refresh token to invalidate
     */
    @Transactional
    public void logout(String refreshToken) {
        log.info("Logging out user");
        refreshTokenService.deleteToken(refreshToken);
    }
    
    /**
     * Generate access token for user
     *
     * @param user the user
     * @return the JWT access token
     */
    private String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        
        return jwtTokenProvider.generateToken(String.valueOf(user.getId()), claims);
    }
}
