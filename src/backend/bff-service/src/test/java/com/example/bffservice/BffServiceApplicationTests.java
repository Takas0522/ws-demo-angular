package com.example.bffservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic integration test for BFF Service
 */
@SpringBootTest
@TestPropertySource(properties = {
    "backend.services.auth.url=http://localhost:8081",
    "backend.services.user.url=http://localhost:8082",
    "backend.services.permission.url=http://localhost:8083"
})
class BffServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verify that the Spring context loads successfully
    }
}
