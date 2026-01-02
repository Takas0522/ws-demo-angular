package com.example.permissionservice.controller;

import com.example.permissionservice.dto.ApplicationDto;
import com.example.permissionservice.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for application-related API endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    /**
     * Get all applications
     * GET /api/applications
     *
     * @return list of applications
     */
    @GetMapping
    public ResponseEntity<List<ApplicationDto>> getAllApplications() {
        log.info("GET /api/applications - Fetching all applications");
        
        List<ApplicationDto> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }
}
