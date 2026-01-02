package com.example.bffservice.controller;

import com.example.bffservice.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * BFF Controller for Permission Service endpoints
 * Routes all /api/applications/** and /api/permissions/** requests to the Permission Service
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class PermissionController {

    private final ProxyService proxyService;

    public PermissionController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Get all applications
     */
    @GetMapping("/applications")
    public ResponseEntity<Object> getAllApplications(HttpServletRequest httpRequest) {
        log.info("BFF: GET /api/applications");
        return proxyService.forwardToPermissionService(
                "/api/applications",
                HttpMethod.GET,
                null,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Get user permissions
     */
    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<Object> getUserPermissions(@PathVariable Long userId, HttpServletRequest httpRequest) {
        log.info("BFF: GET /api/users/{}/permissions", userId);
        return proxyService.forwardToPermissionService(
                "/api/users/" + userId + "/permissions",
                HttpMethod.GET,
                null,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Create permission
     */
    @PostMapping("/permissions")
    public ResponseEntity<Object> createPermission(@RequestBody Object request, HttpServletRequest httpRequest) {
        log.info("BFF: POST /api/permissions");
        return proxyService.forwardToPermissionService(
                "/api/permissions",
                HttpMethod.POST,
                request,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Update permission
     */
    @PutMapping("/permissions/{permissionId}")
    public ResponseEntity<Object> updatePermission(
            @PathVariable Long permissionId,
            @RequestBody Object request,
            HttpServletRequest httpRequest) {
        log.info("BFF: PUT /api/permissions/{}", permissionId);
        return proxyService.forwardToPermissionService(
                "/api/permissions/" + permissionId,
                HttpMethod.PUT,
                request,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Delete permission
     */
    @DeleteMapping("/permissions/{permissionId}")
    public ResponseEntity<Object> deletePermission(@PathVariable Long permissionId, HttpServletRequest httpRequest) {
        log.info("BFF: DELETE /api/permissions/{}", permissionId);
        return proxyService.forwardToPermissionService(
                "/api/permissions/" + permissionId,
                HttpMethod.DELETE,
                null,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Extract headers from the incoming request
     */
    private HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }
        return headers;
    }
}
