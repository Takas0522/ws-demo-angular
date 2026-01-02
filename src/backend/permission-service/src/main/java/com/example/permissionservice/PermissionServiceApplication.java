package com.example.permissionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Permission Service
 */
@SpringBootApplication(scanBasePackages = {
    "com.example.permissionservice",
    "com.example.sharedlib"
})
public class PermissionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionServiceApplication.class, args);
    }
}
