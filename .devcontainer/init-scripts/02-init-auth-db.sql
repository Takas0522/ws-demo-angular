-- Initialize Auth Service Database
-- This script initializes the auth_db database with schema and seed data
-- from auth-service resources

\c auth_db;

-- Auth Service Database Schema
-- This schema defines tables for authentication and authorization

-- Drop tables if they exist (for development/testing)
DROP TABLE IF EXISTS saga_state CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username for faster lookups
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index on user_id and token for faster lookups
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- Saga state table for distributed transaction management
CREATE TABLE saga_state (
    id BIGSERIAL PRIMARY KEY,
    saga_id VARCHAR(100) NOT NULL UNIQUE,
    saga_type VARCHAR(50) NOT NULL,
    current_step VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payload TEXT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on saga_id and status for faster lookups
CREATE INDEX idx_saga_state_saga_id ON saga_state(saga_id);
CREATE INDEX idx_saga_state_status ON saga_state(status);
CREATE INDEX idx_saga_state_saga_type ON saga_state(saga_type);

-- Auth Service Seed Data
-- Insert 10 test users
-- Default password for all users: "password123"
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- User 1: Admin user
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com', true, true, true, true);

-- User 2: Regular user
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user1@example.com', true, true, true, true);

-- User 3: Regular user
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user2@example.com', true, true, true, true);

-- User 4: Regular user
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('user3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user3@example.com', true, true, true, true);

-- User 5: Regular user
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('user4', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user4@example.com', true, true, true, true);

-- User 6: Regular user
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('user5', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user5@example.com', true, true, true, true);

-- User 7: Regular user
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('user6', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user6@example.com', true, true, true, true);

-- User 8: Disabled user for testing
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('disabled_user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'disabled@example.com', false, true, true, true);

-- User 9: Locked user for testing
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('locked_user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'locked@example.com', true, true, false, true);

-- User 10: Expired credentials user for testing
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('expired_creds_user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'expired@example.com', true, true, true, false);
