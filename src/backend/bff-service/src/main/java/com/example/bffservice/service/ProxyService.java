package com.example.bffservice.service;

import com.example.bffservice.config.BackendServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service for proxying requests to backend services
 */
@Slf4j
@Service
public class ProxyService {

    private final WebClient.Builder webClientBuilder;
    private final BackendServiceConfig backendServiceConfig;

    public ProxyService(WebClient.Builder webClientBuilder, BackendServiceConfig backendServiceConfig) {
        this.webClientBuilder = webClientBuilder;
        this.backendServiceConfig = backendServiceConfig;
    }

    /**
     * Forward request to auth service
     */
    public <T> ResponseEntity<T> forwardToAuthService(
            String path,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            Class<T> responseType) {
        return forwardRequest(backendServiceConfig.getAuth().getUrl(), path, method, body, headers, responseType);
    }

    /**
     * Forward request to user service
     */
    public <T> ResponseEntity<T> forwardToUserService(
            String path,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            Class<T> responseType) {
        return forwardRequest(backendServiceConfig.getUser().getUrl(), path, method, body, headers, responseType);
    }

    /**
     * Forward request to permission service
     */
    public <T> ResponseEntity<T> forwardToPermissionService(
            String path,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            Class<T> responseType) {
        return forwardRequest(backendServiceConfig.getPermission().getUrl(), path, method, body, headers, responseType);
    }

    /**
     * Generic method to forward requests to backend services
     */
    private <T> ResponseEntity<T> forwardRequest(
            String baseUrl,
            String path,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            Class<T> responseType) {

        log.debug("Forwarding {} request to {}{}", method, baseUrl, path);

        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();

        WebClient.RequestBodySpec requestSpec = webClient
                .method(method)
                .uri(path)
                .headers(h -> {
                    if (headers != null) {
                        headers.forEach((key, values) -> {
                            // Forward important headers, skip hop-by-hop headers
                            if (!isHopByHopHeader(key)) {
                                h.addAll(key, values);
                            }
                        });
                    }
                });

        Mono<ResponseEntity<T>> responseMono;
        if (body != null && (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH)) {
            responseMono = requestSpec
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(responseType);
        } else {
            responseMono = requestSpec
                    .retrieve()
                    .toEntity(responseType);
        }

        return responseMono.block();
    }

    /**
     * Check if header is a hop-by-hop header that should not be forwarded
     */
    private boolean isHopByHopHeader(String headerName) {
        String lowerCase = headerName.toLowerCase();
        return lowerCase.equals("connection") ||
               lowerCase.equals("keep-alive") ||
               lowerCase.equals("proxy-authenticate") ||
               lowerCase.equals("proxy-authorization") ||
               lowerCase.equals("te") ||
               lowerCase.equals("trailers") ||
               lowerCase.equals("transfer-encoding") ||
               lowerCase.equals("upgrade") ||
               lowerCase.equals("host");
    }
}
