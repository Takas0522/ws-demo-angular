package com.example.bffservice.controller;

import com.example.bffservice.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * BFF Controller for Auth Service endpoints
 * Routes all /api/auth/** requests to the Auth Service
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ProxyService proxyService;

    public AuthController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Object request, HttpServletRequest httpRequest) {
        log.info("BFF: POST /api/auth/login");
        return proxyService.forwardToAuthService(
                "/api/auth/login",
                HttpMethod.POST,
                request,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody Object request, HttpServletRequest httpRequest) {
        log.info("BFF: POST /api/auth/refresh");
        return proxyService.forwardToAuthService(
                "/api/auth/refresh",
                HttpMethod.POST,
                request,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestBody Object request, HttpServletRequest httpRequest) {
        log.info("BFF: POST /api/auth/logout");
        return proxyService.forwardToAuthService(
                "/api/auth/logout",
                HttpMethod.POST,
                request,
                extractHeaders(httpRequest),
                Object.class
        );
    }

    /**
     * Register endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody Object request, HttpServletRequest httpRequest) {
        log.info("BFF: POST /api/auth/register");
        return proxyService.forwardToAuthService(
                "/api/auth/register",
                HttpMethod.POST,
                request,
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
