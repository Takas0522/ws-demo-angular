package com.example.userservice.config;

import com.example.sharedlib.filter.InternalApiAuthFilter;
import com.example.sharedlib.jwt.JwtValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the user service
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final JwtValidator jwtValidator;
    
    public SecurityConfig(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    // Internal API endpoints are open (authentication is optional)
                    .antMatchers("/internal/**").permitAll()
                    // Public API endpoints require JWT authentication
                    .antMatchers("/api/**").authenticated()
                    // All other endpoints are denied
                    .anyRequest().denyAll()
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    
    /**
     * Create JWT authentication filter bean
     *
     * @return InternalApiAuthFilter instance
     */
    @Bean
    public InternalApiAuthFilter jwtAuthenticationFilter() {
        return new InternalApiAuthFilter(jwtValidator);
    }
}
