package com.example.bffservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for BFF Service
 * BFF acts as a proxy, so we allow all requests to pass through
 * Authentication is handled by the backend services
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors() // Enable CORS
            .and()
            .csrf().disable() // Disable CSRF for REST API
            .authorizeRequests()
                .anyRequest().permitAll(); // Allow all requests (auth handled by backend services)
        
        return http.build();
    }
}
