-- Initialize User Service Database
-- This script initializes the user_db database with schema and seed data
-- from user-service resources

\c user_db;

-- User Service Database Schema
-- This schema defines tables for user profiles and idempotency management

-- Drop tables if they exist (for development/testing)
DROP TABLE IF EXISTS idempotency_keys CASCADE;
DROP TABLE IF EXISTS user_profiles CASCADE;

-- User profiles table
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    display_name VARCHAR(100),
    bio TEXT,
    avatar_url VARCHAR(255),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes on user_profiles for faster lookups
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_user_profiles_display_name ON user_profiles(display_name);
CREATE INDEX idx_user_profiles_created_at ON user_profiles(created_at);

-- Idempotency keys table for handling duplicate requests
CREATE TABLE idempotency_keys (
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    request_path VARCHAR(255) NOT NULL,
    request_method VARCHAR(10) NOT NULL,
    response_status_code INT,
    response_body TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

-- Create indexes on idempotency_keys for faster lookups
CREATE INDEX idx_idempotency_keys_key ON idempotency_keys(idempotency_key);
CREATE INDEX idx_idempotency_keys_expires_at ON idempotency_keys(expires_at);

-- User Service Seed Data
-- Insert 10 test user profiles
-- These user_id values should correspond to users created in auth_db

-- Profile 1: Admin user
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (1, 'Admin', 'User', 'Administrator', 'System administrator with full access to all features', 'https://i.pravatar.cc/150?img=1', '+1-555-0101', '1985-01-15', '123 Admin Street', 'San Francisco', 'California', 'USA', '94102');

-- Profile 2: Regular user
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (2, 'John', 'Doe', 'JohnD', 'Software engineer passionate about web development', 'https://i.pravatar.cc/150?img=2', '+1-555-0102', '1990-05-20', '456 Main Street', 'New York', 'New York', 'USA', '10001');

-- Profile 3: Regular user
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (3, 'Jane', 'Smith', 'JaneS', 'UX designer who loves creating beautiful interfaces', 'https://i.pravatar.cc/150?img=3', '+1-555-0103', '1992-08-12', '789 Oak Avenue', 'Los Angeles', 'California', 'USA', '90001');

-- Profile 4: Regular user
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (4, 'Michael', 'Johnson', 'MikeJ', 'Full-stack developer specializing in microservices', 'https://i.pravatar.cc/150?img=4', '+1-555-0104', '1988-03-25', '321 Elm Street', 'Chicago', 'Illinois', 'USA', '60601');

-- Profile 5: Regular user
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (5, 'Emily', 'Davis', 'EmilyD', 'Data scientist exploring machine learning applications', 'https://i.pravatar.cc/150?img=5', '+1-555-0105', '1995-11-08', '654 Pine Road', 'Seattle', 'Washington', 'USA', '98101');

-- Profile 6: Regular user
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (6, 'David', 'Wilson', 'DaveW', 'DevOps engineer focused on cloud infrastructure', 'https://i.pravatar.cc/150?img=6', '+1-555-0106', '1987-07-14', '987 Maple Lane', 'Austin', 'Texas', 'USA', '73301');

-- Profile 7: Regular user
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (7, 'Sarah', 'Brown', 'SarahB', 'Product manager passionate about user experience', 'https://i.pravatar.cc/150?img=7', '+1-555-0107', '1991-12-30', '147 Cedar Drive', 'Boston', 'Massachusetts', 'USA', '02101');

-- Profile 8: Disabled user for testing
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (8, 'Disabled', 'Account', 'DisabledUser', 'This account is disabled for testing purposes', 'https://i.pravatar.cc/150?img=8', '+1-555-0108', '1989-04-18', '258 Birch Street', 'Denver', 'Colorado', 'USA', '80201');

-- Profile 9: Locked user for testing
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (9, 'Locked', 'Account', 'LockedUser', 'This account is locked for testing purposes', 'https://i.pravatar.cc/150?img=9', '+1-555-0109', '1993-09-22', '369 Spruce Avenue', 'Miami', 'Florida', 'USA', '33101');

-- Profile 10: Expired credentials user for testing
INSERT INTO user_profiles (user_id, first_name, last_name, display_name, bio, avatar_url, phone_number, date_of_birth, address, city, state, country, postal_code)
VALUES (10, 'Expired', 'Credentials', 'ExpiredUser', 'This account has expired credentials for testing', 'https://i.pravatar.cc/150?img=10', '+1-555-0110', '1986-06-05', '741 Willow Court', 'Phoenix', 'Arizona', 'USA', '85001');
