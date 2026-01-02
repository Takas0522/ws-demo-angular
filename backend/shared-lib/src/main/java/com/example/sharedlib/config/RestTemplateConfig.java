package com.example.sharedlib.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for RestTemplate with timeout and retry settings
 */
@Configuration
@EnableRetry
public class RestTemplateConfig {

    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Create RestTemplate with configured timeout
     *
     * @param builder RestTemplateBuilder
     * @param requestFactory ClientHttpRequestFactory with timeout settings
     * @return Configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory) {
        return builder
                .requestFactory(() -> requestFactory)
                .setConnectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .setReadTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    /**
     * Create RetryTemplate for retry logic
     *
     * @return Configured RetryTemplate
     */
    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(MAX_RETRY_ATTEMPTS)
                .fixedBackoff(1000) // 1 second between retries
                .retryOn(Exception.class)
                .build();
    }

    /**
     * Create ClientHttpRequestFactory with timeout settings
     *
     * @return Configured ClientHttpRequestFactory
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_SECONDS * 1000);
        factory.setReadTimeout(TIMEOUT_SECONDS * 1000);
        return factory;
    }
}
