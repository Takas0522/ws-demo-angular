package com.example.bffservice.controller;

import com.example.bffservice.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * BFF Controller for User Service endpoints
 * Routes all /api/users/** requests to the User Service
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ProxyService proxyService;

    public UserController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Get all user profiles
     */
    @GetMapping
    public ResponseEntity<Object> getAllUserProfiles(HttpServletRequest httpRequest) {
        log.info("BFF: GET /api/users");
        return proxyService.forwardToUserService(
                "/api/users",
                HttpMethod.GET,
                null,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Get user profile by userId
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserProfile(@PathVariable Long userId, HttpServletRequest httpRequest) {
        log.info("BFF: GET /api/users/{}", userId);
        return proxyService.forwardToUserService(
                "/api/users/" + userId,
                HttpMethod.GET,
                null,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Create user profile
     */
    @PostMapping
    public ResponseEntity<Object> createUserProfile(@RequestBody Object request, HttpServletRequest httpRequest) {
        log.info("BFF: POST /api/users");
        return proxyService.forwardToUserService(
                "/api/users",
                HttpMethod.POST,
                request,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Update user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody Object request,
            HttpServletRequest httpRequest) {
        log.info("BFF: PUT /api/users/{}", userId);
        return proxyService.forwardToUserService(
                "/api/users/" + userId,
                HttpMethod.PUT,
                request,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Delete user profile
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserProfile(@PathVariable Long userId, HttpServletRequest httpRequest) {
        log.info("BFF: DELETE /api/users/{}", userId);
        return proxyService.forwardToUserService(
                "/api/users/" + userId,
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
