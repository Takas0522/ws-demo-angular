package com.example.permissionservice.service;

import com.example.permissionservice.dto.ApplicationDto;
import com.example.permissionservice.entity.Application;
import com.example.permissionservice.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ApplicationService
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Application testApplication;

    @BeforeEach
    void setUp() {
        testApplication = Application.builder()
                .id(1L)
                .appCode("TEST_APP")
                .appName("Test Application")
                .description("Test description")
                .enabled(true)
                .build();
    }

    @Test
    void getAllApplications_ShouldReturnListOfApplications() {
        // Arrange
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findAll()).thenReturn(applications);

        // Act
        List<ApplicationDto> result = applicationService.getAllApplications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST_APP", result.get(0).getAppCode());
        assertEquals("Test Application", result.get(0).getAppName());
        verify(applicationRepository, times(1)).findAll();
    }
}
