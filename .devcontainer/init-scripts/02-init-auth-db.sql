-- Initialize Auth Service Database
-- This script connects to auth_db and executes the schema and data files
-- from auth-service resources directory (DRY approach)

\c auth_db;

-- Execute schema from auth-service resources
\i /backend-src/auth-service/src/main/resources/schema.sql

-- Execute seed data from auth-service resources
\i /backend-src/auth-service/src/main/resources/data.sql
