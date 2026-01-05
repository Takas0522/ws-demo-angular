package com.example.bffservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for backend service URLs
 */
@Configuration
@ConfigurationProperties(prefix = "backend.services")
@Getter
@Setter
public class BackendServiceConfig {

    private ServiceEndpoint auth;
    private ServiceEndpoint user;
    private ServiceEndpoint permission;

    @Getter
    @Setter
    public static class ServiceEndpoint {
        private String url;
    }
}
