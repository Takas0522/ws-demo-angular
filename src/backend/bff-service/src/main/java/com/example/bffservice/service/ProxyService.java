package com.example.bffservice.service;

import com.example.bffservice.config.BackendServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Service for proxying requests to backend services
 */
@Slf4j
@Service
public class ProxyService {

    private static final Set<HttpMethod> METHODS_WITH_BODY = Set.of(
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.PATCH
    );

    private final WebClient authServiceClient;
    private final WebClient userServiceClient;
    private final WebClient permissionServiceClient;

    public ProxyService(WebClient.Builder webClientBuilder, BackendServiceConfig backendServiceConfig) {
        // Create and cache WebClient instances for each backend service
        this.authServiceClient = webClientBuilder.baseUrl(backendServiceConfig.getAuth().getUrl()).build();
        this.userServiceClient = webClientBuilder.baseUrl(backendServiceConfig.getUser().getUrl()).build();
        this.permissionServiceClient = webClientBuilder.baseUrl(backendServiceConfig.getPermission().getUrl()).build();
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
        return forwardRequest(authServiceClient, path, method, body, headers, responseType);
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
        return forwardRequest(userServiceClient, path, method, body, headers, responseType);
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
        return forwardRequest(permissionServiceClient, path, method, body, headers, responseType);
    }

    /**
     * Generic method to forward requests to backend services
     */
    private <T> ResponseEntity<T> forwardRequest(
            WebClient webClient,
            String path,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            Class<T> responseType) {

        log.debug("Forwarding {} request to {}", method, path);

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
        if (body != null && METHODS_WITH_BODY.contains(method)) {
            responseMono = requestSpec
                    .bodyValue(body)
                    .exchangeToMono(response -> response.toEntity(responseType));
        } else {
            responseMono = requestSpec
                    .exchangeToMono(response -> response.toEntity(responseType));
        }

        // Note: Using block() for simplicity. For production use, consider making this reactive.
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
