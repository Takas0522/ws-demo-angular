package com.example.authservice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test to verify password hash matching
 */
public class PasswordHashTest {
    
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // Hash from database (NEW)
        String storedHash = "$2a$10$k7tRFdkBD1/fhIN4xVH1deOmF09L/2fQpNM62tpV2JhvHtVBHP4Gm";
        
        // Test passwords
        String[] testPasswords = {
            "password123",
            "admin123",
            "Password123",
            "admin",
            "password",
            "123456",
            "test123",
            "demo123",
            "secret",
            "changeme"
        };
        
        System.out.println("Testing password hash verification:");
        System.out.println("Stored hash: " + storedHash);
        System.out.println();
        
        for (String pwd : testPasswords) {
            boolean matches = passwordEncoder.matches(pwd, storedHash);
            System.out.println("Testing '" + pwd + "': " + matches);
            if (matches) {
                System.out.println("*** MATCH FOUND: " + pwd + " ***");
            }
        }
        System.out.println();
        
        // Generate correct hash for password123
        System.out.println("Generating correct hash for 'password123':");
        String correctHash = passwordEncoder.encode("password123");
        System.out.println("Correct hash: " + correctHash);
        System.out.println("Verification: " + passwordEncoder.matches("password123", correctHash));
    }
}
