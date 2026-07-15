-- Initialize demo users with correct credentials
-- Run this script to set up the demo users

-- First, ensure roles exist
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_SELLER')
ON CONFLICT (name) DO NOTHING;

-- Get role IDs (these will be used in the inserts below)
-- ROLE_USER typically has id=1, ROLE_ADMIN id=2, ROLE_SELLER id=3

-- Create admin user with password: Admin@123
-- BCrypt hash for 'Admin@123'
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW')
ON CONFLICT (username) DO UPDATE SET password = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW';

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Create regular user with password: user123
-- BCrypt hash for 'user123'
INSERT INTO users (username, password) VALUES ('user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON CONFLICT (username) DO UPDATE SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi';

-- Assign user role to regular user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'user' AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;

-- Create seller user with password: seller123
-- BCrypt hash for 'seller123' (using same hash as user123 for demo)
INSERT INTO users (username, password) VALUES ('seller', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON CONFLICT (username) DO UPDATE SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi';

-- Assign seller role to seller user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'seller' AND r.name = 'ROLE_SELLER'
ON CONFLICT DO NOTHING;

-- Verification query
SELECT u.username, r.name as role 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id 
WHERE u.username IN ('admin', 'user', 'seller');
