-- Permission Service Database Schema
-- This schema defines tables for application permissions and user access

-- Drop tables if they exist (for development/testing)
DROP TABLE IF EXISTS user_app_permissions CASCADE;
DROP TABLE IF EXISTS permission_levels CASCADE;
DROP TABLE IF EXISTS applications CASCADE;

-- Applications table
-- Stores information about applications in the system
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    app_code VARCHAR(50) NOT NULL UNIQUE,
    app_name VARCHAR(100) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on app_code for faster lookups
CREATE INDEX idx_applications_app_code ON applications(app_code);
CREATE INDEX idx_applications_enabled ON applications(enabled);

-- Permission Levels table
-- Stores the different levels of permissions
CREATE TABLE permission_levels (
    id BIGSERIAL PRIMARY KEY,
    level_code VARCHAR(20) NOT NULL UNIQUE,
    level_name VARCHAR(50) NOT NULL,
    description TEXT,
    level_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on level_code for faster lookups
CREATE INDEX idx_permission_levels_level_code ON permission_levels(level_code);
CREATE INDEX idx_permission_levels_level_order ON permission_levels(level_order);

-- User App Permissions table
-- Maps users to applications with specific permission levels
CREATE TABLE user_app_permissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    permission_level_id BIGINT NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    granted_by VARCHAR(50),
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_app_permissions_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_app_permissions_permission_level FOREIGN KEY (permission_level_id) REFERENCES permission_levels(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_app_permissions UNIQUE (user_id, application_id)
);

-- Create indexes for faster lookups
CREATE INDEX idx_user_app_permissions_user_id ON user_app_permissions(user_id);
CREATE INDEX idx_user_app_permissions_application_id ON user_app_permissions(application_id);
CREATE INDEX idx_user_app_permissions_permission_level_id ON user_app_permissions(permission_level_id);
CREATE INDEX idx_user_app_permissions_expires_at ON user_app_permissions(expires_at);
