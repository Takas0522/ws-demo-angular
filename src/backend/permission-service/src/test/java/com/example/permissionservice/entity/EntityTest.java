package com.example.permissionservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Permission Service entities
 */
class EntityTest {

    @Test
    void testApplicationEntityBuilder() {
        LocalDateTime now = LocalDateTime.now();
        
        Application app = Application.builder()
                .id(1L)
                .appCode("TEST_APP")
                .appName("Test Application")
                .description("Test description")
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(app);
        assertEquals(1L, app.getId());
        assertEquals("TEST_APP", app.getAppCode());
        assertEquals("Test Application", app.getAppName());
        assertEquals("Test description", app.getDescription());
        assertTrue(app.getEnabled());
        assertEquals(now, app.getCreatedAt());
        assertEquals(now, app.getUpdatedAt());
    }

    @Test
    void testPermissionLevelEntityBuilder() {
        LocalDateTime now = LocalDateTime.now();
        
        PermissionLevel level = PermissionLevel.builder()
                .id(1L)
                .levelCode("ADMIN")
                .levelName("Administrator")
                .description("Full administrative access")
                .levelOrder(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(level);
        assertEquals(1L, level.getId());
        assertEquals("ADMIN", level.getLevelCode());
        assertEquals("Administrator", level.getLevelName());
        assertEquals("Full administrative access", level.getDescription());
        assertEquals(3, level.getLevelOrder());
        assertEquals(now, level.getCreatedAt());
        assertEquals(now, level.getUpdatedAt());
    }

    @Test
    void testUserAppPermissionEntityBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Application app = Application.builder()
                .id(1L)
                .appCode("TEST_APP")
                .appName("Test Application")
                .build();
        
        PermissionLevel level = PermissionLevel.builder()
                .id(1L)
                .levelCode("READ")
                .levelName("Read")
                .build();
        
        UserAppPermission permission = UserAppPermission.builder()
                .id(1L)
                .userId(100L)
                .application(app)
                .permissionLevel(level)
                .grantedAt(now)
                .grantedBy("admin")
                .expiresAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(permission);
        assertEquals(1L, permission.getId());
        assertEquals(100L, permission.getUserId());
        assertEquals(app, permission.getApplication());
        assertEquals(level, permission.getPermissionLevel());
        assertEquals(now, permission.getGrantedAt());
        assertEquals("admin", permission.getGrantedBy());
        assertNull(permission.getExpiresAt());
        assertEquals(now, permission.getCreatedAt());
        assertEquals(now, permission.getUpdatedAt());
    }

    @Test
    void testApplicationDefaultValues() {
        Application app = Application.builder()
                .appCode("TEST")
                .appName("Test")
                .build();
        
        assertTrue(app.getEnabled(), "Default enabled should be true");
    }

    @Test
    void testUserAppPermissionDefaultGrantedAt() {
        UserAppPermission permission = UserAppPermission.builder()
                .userId(1L)
                .build();
        
        assertNotNull(permission.getGrantedAt(), "Default grantedAt should be set");
    }
}
