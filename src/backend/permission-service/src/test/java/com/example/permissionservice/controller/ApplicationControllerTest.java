package com.example.permissionservice.controller;

import com.example.permissionservice.dto.ApplicationDto;
import com.example.permissionservice.service.ApplicationService;
import com.example.sharedlib.jwt.JwtValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ApplicationController
 */
@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private JwtValidator jwtValidator;

    @Test
    @WithMockUser
    void getAllApplications_ShouldReturnListOfApplications() throws Exception {
        // Arrange
        ApplicationDto app1 = ApplicationDto.builder()
                .id(1L)
                .appCode("TEST_APP")
                .appName("Test Application")
                .enabled(true)
                .build();
        
        List<ApplicationDto> applications = Arrays.asList(app1);
        when(applicationService.getAllApplications()).thenReturn(applications);

        // Act & Assert
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appCode").value("TEST_APP"))
                .andExpect(jsonPath("$[0].appName").value("Test Application"));
    }
}
