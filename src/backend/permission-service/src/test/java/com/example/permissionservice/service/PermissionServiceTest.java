package com.example.permissionservice.service;

import com.example.permissionservice.dto.CreatePermissionRequest;
import com.example.permissionservice.dto.UserPermissionDto;
import com.example.permissionservice.entity.Application;
import com.example.permissionservice.entity.PermissionLevel;
import com.example.permissionservice.entity.UserAppPermission;
import com.example.permissionservice.repository.ApplicationRepository;
import com.example.permissionservice.repository.PermissionLevelRepository;
import com.example.permissionservice.repository.UserAppPermissionRepository;
import com.example.sharedlib.exception.BadRequestException;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PermissionService
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private UserAppPermissionRepository permissionRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PermissionLevelRepository permissionLevelRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PermissionService permissionService;

    private Application testApplication;
    private PermissionLevel testPermissionLevel;
    private UserAppPermission testPermission;

    @BeforeEach
    void setUp() {
        testApplication = Application.builder()
                .id(1L)
                .appCode("TEST_APP")
                .appName("Test Application")
                .enabled(true)
                .build();

        testPermissionLevel = PermissionLevel.builder()
                .id(1L)
                .levelCode("READ")
                .levelName("Read")
                .levelOrder(1)
                .build();

        testPermission = UserAppPermission.builder()
                .id(1L)
                .userId(1L)
                .application(testApplication)
                .permissionLevel(testPermissionLevel)
                .grantedBy("admin")
                .build();
    }

    @Test
    void getUserPermissions_ShouldReturnListOfPermissions() {
        // Arrange
        List<UserAppPermission> permissions = Arrays.asList(testPermission);
        when(permissionRepository.findByUserId(1L)).thenReturn(permissions);

        // Act
        List<UserPermissionDto> result = permissionService.getUserPermissions(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        verify(permissionRepository, times(1)).findByUserId(1L);
    }

    @Test
    void createPermission_ShouldCreatePermissionSuccessfully() {
        // Arrange
        CreatePermissionRequest request = CreatePermissionRequest.builder()
                .userId(1L)
                .applicationId(1L)
                .permissionLevelId(1L)
                .grantedBy("admin")
                .build();

        when(permissionRepository.existsByUserIdAndApplicationId(1L, 1L)).thenReturn(false);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(permissionLevelRepository.findById(1L)).thenReturn(Optional.of(testPermissionLevel));
        when(permissionRepository.save(any(UserAppPermission.class))).thenReturn(testPermission);

        // Act
        UserPermissionDto result = permissionService.createPermission(request, "admin");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(auditLogService, times(1)).logPermissionCreate(1L, 1L, 1L, "admin");
    }

    @Test
    void createPermission_ShouldThrowException_WhenPermissionAlreadyExists() {
        // Arrange
        CreatePermissionRequest request = CreatePermissionRequest.builder()
                .userId(1L)
                .applicationId(1L)
                .permissionLevelId(1L)
                .build();

        when(permissionRepository.existsByUserIdAndApplicationId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            permissionService.createPermission(request, "admin");
        });
    }

    @Test
    void deletePermission_ShouldDeletePermissionSuccessfully() {
        // Arrange
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(testPermission));

        // Act
        permissionService.deletePermission(1L, "admin");

        // Assert
        verify(permissionRepository, times(1)).delete(testPermission);
        verify(auditLogService, times(1)).logPermissionDelete(1L, 1L, 1L, "admin");
    }

    @Test
    void deletePermission_ShouldThrowException_WhenPermissionNotFound() {
        // Arrange
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            permissionService.deletePermission(1L, "admin");
        });
    }
}
