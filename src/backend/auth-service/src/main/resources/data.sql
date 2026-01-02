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
