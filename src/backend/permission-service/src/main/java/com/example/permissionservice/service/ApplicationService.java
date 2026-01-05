package com.example.permissionservice.service;

import com.example.permissionservice.dto.ApplicationDto;
import com.example.permissionservice.entity.Application;
import com.example.permissionservice.repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing applications
 */
@Slf4j
@Service
public class ApplicationService {
    
    private final ApplicationRepository applicationRepository;
    
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    
    /**
     * Get all applications
     *
     * @return List of ApplicationDto
     */
    @Transactional(readOnly = true)
    public List<ApplicationDto> getAllApplications() {
        log.info("Fetching all applications");
        
        List<Application> applications = applicationRepository.findAll();
        return applications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Map Application entity to ApplicationDto
     *
     * @param application the Application entity
     * @return ApplicationDto
     */
    private ApplicationDto mapToDto(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
                .appCode(application.getAppCode())
                .appName(application.getAppName())
                .description(application.getDescription())
                .enabled(application.getEnabled())
                .build();
    }
}
