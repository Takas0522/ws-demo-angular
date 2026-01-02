-- Initialize User Service Database
-- This script connects to user_db and executes the schema and data files
-- from user-service resources directory (DRY approach)

\c user_db;

-- Execute schema from user-service resources
\i /backend-src/user-service/src/main/resources/schema.sql

-- Execute seed data from user-service resources
\i /backend-src/user-service/src/main/resources/data.sql
