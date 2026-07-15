-- ============================================
-- USER MANAGEMENT & PROFILE SYSTEM MIGRATION
-- ============================================

-- 1. Update users table with profile, verification, and preference fields
ALTER TABLE users 
    ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20) UNIQUE,
    ADD COLUMN IF NOT EXISTS full_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS profile_picture_url VARCHAR(512),
    ADD COLUMN IF NOT EXISTS language VARCHAR(10) NOT NULL DEFAULT 'en',
    ADD COLUMN IF NOT EXISTS currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    ADD COLUMN IF NOT EXISTS email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS sms_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS push_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS mfa_secret VARCHAR(128),
    ADD COLUMN IF NOT EXISTS mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS reset_password_token VARCHAR(128),
    ADD COLUMN IF NOT EXISTS reset_password_token_expiry DATETIME,
    ADD COLUMN IF NOT EXISTS verification_token VARCHAR(128);

-- 2. Create refresh_tokens table for JWT session management
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Update addresses table to support billing/shipping and recipient name
ALTER TABLE addresses
    ADD COLUMN IF NOT EXISTS recipient_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS type VARCHAR(50) NOT NULL DEFAULT 'SHIPPING';

-- 4. Seed new predefined roles
INSERT INTO roles (name) 
SELECT 'ROLE_SUPPORT_AGENT' FROM DUAL 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_SUPPORT_AGENT');

INSERT INTO roles (name) 
SELECT 'ROLE_DELIVERY_PARTNER' FROM DUAL 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_DELIVERY_PARTNER');
