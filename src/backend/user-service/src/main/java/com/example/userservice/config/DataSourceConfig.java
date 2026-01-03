package com.example.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * DataSource configuration with retry logic for database connection
 */
@Configuration
@EnableRetry
@EnableConfigurationProperties(DataSourceProperties.class)
@Slf4j
public class DataSourceConfig {

    /**
     * Configure retry template for database operations
     * @return RetryTemplate configured with retry policy
     */
    @Bean(name = "dataSourceRetryTemplate")
    public RetryTemplate dataSourceRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Retry up to 5 times
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);
        retryTemplate.setRetryPolicy(retryPolicy);

        // Wait 2 seconds between retries
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    /**
     * Create DataSource with retry logic
     * Retries connection up to 5 times with 2 second intervals
     * 
     * @param properties DataSource properties from application.yml
     * @param retryTemplate RetryTemplate for retrying connection
     * @return DataSource with retry capability
     */
    @Bean
    public DataSource dataSource(DataSourceProperties properties, RetryTemplate dataSourceRetryTemplate) {
        log.info("Configuring DataSource with retry capability");
        
        try {
            return dataSourceRetryTemplate.execute(context -> {
                log.info("Attempting to connect to database (attempt {})", context.getRetryCount() + 1);
                try {
                    DataSource dataSource = properties.initializeDataSourceBuilder().build();
                    // Test the connection
                    dataSource.getConnection().close();
                    log.info("Successfully connected to database");
                    return dataSource;
                } catch (SQLException e) {
                    log.warn("Failed to connect to database (attempt {}): {}", 
                        context.getRetryCount() + 1, e.getMessage());
                    throw new RuntimeException("Failed to connect to database", e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to configure DataSource after retries", e);
            throw new RuntimeException("Failed to configure DataSource", e);
        }
    }
}
