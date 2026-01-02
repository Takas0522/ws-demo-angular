package com.example.authservice.controller;

import com.example.authservice.dto.SagaStatusResponse;
import com.example.authservice.service.SagaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for saga endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/saga")
public class SagaController {
    
    private final SagaService sagaService;
    
    public SagaController(SagaService sagaService) {
        this.sagaService = sagaService;
    }
    
    /**
     * Get saga status endpoint
     * GET /api/saga/{sagaId}
     *
     * @param sagaId the saga ID
     * @return the saga status response
     */
    @GetMapping("/{sagaId}")
    public ResponseEntity<SagaStatusResponse> getSagaStatus(@PathVariable String sagaId) {
        log.info("GET /api/saga/{} - Getting saga status", sagaId);
        
        return sagaService.getSagaStatus(sagaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
