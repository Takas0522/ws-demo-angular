-- PostgreSQL Database Initialization Script
-- This script creates three databases for the microservices architecture:
-- 1. auth_db: Authentication service database
-- 2. user_db: User management service database
-- 3. permission_db: Permission management service database

-- Create auth_db
CREATE DATABASE auth_db WITH OWNER = postgres;

-- Create user_db
CREATE DATABASE user_db WITH OWNER = postgres;

-- Create permission_db
CREATE DATABASE permission_db WITH OWNER = postgres;
