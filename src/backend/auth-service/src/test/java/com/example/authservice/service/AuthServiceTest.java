package com.example.authservice.service;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.example.sharedlib.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RefreshTokenService refreshTokenService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpiration", 3600000L);
        
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("$2a$10$hashedpassword")
                .email("test@example.com")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }
    
    @Test
    void testLoginSuccess() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyMap())).thenReturn("access-token");
        
        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .userId(1L)
                .build();
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(refreshToken);
        
        // Act
        AuthResponse response = authService.login(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtTokenProvider).generateToken(anyString(), anyMap());
        verify(refreshTokenService).createRefreshToken(1L);
    }
    
    @Test
    void testLoginInvalidUsername() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    
    @Test
    void testLoginInvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(false);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtTokenProvider, never()).generateToken(anyString(), anyMap());
    }
    
    @Test
    void testLoginDisabledAccount() {
        // Arrange
        testUser.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        
        verify(userRepository).findByUsername("testuser");
        verify(jwtTokenProvider, never()).generateToken(anyString(), anyMap());
    }
    
    @Test
    void testLogout() {
        // Arrange
        String refreshToken = "refresh-token";
        doNothing().when(refreshTokenService).deleteToken(refreshToken);
        
        // Act
        authService.logout(refreshToken);
        
        // Assert
        verify(refreshTokenService).deleteToken(refreshToken);
    }
}
