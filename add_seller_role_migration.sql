-- Migration script to add ROLE_SELLER and default existing products to admin seller
-- Run this script to upgrade your database for the multi-vendor marketplace feature

-- Step 1: Add ROLE_SELLER to the roles table
INSERT INTO roles (name) VALUES ('ROLE_SELLER')
ON CONFLICT (name) DO NOTHING;

-- Step 2: Add seller_id column to products table
ALTER TABLE products ADD COLUMN IF NOT EXISTS seller_id BIGINT;
ALTER TABLE products ADD CONSTRAINT fk_products_seller 
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE SET NULL;

-- Step 3: Find or create a system seller user (using admin as default seller)
-- First, check if admin user exists, if not create one
INSERT INTO users (username, password) 
VALUES ('system_seller', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON CONFLICT (username) DO NOTHING;

-- Step 4: Assign ROLE_SELLER to the system_seller user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'system_seller' AND r.name = 'ROLE_SELLER'
ON CONFLICT DO NOTHING;

-- Step 5: Update all existing products to belong to the system_seller (or admin if system_seller doesn't exist)
UPDATE products 
SET seller_id = (
    SELECT id FROM users 
    WHERE username = 'system_seller' 
    LIMIT 1
)
WHERE seller_id IS NULL;

-- If system_seller doesn't exist, use admin as fallback
UPDATE products 
SET seller_id = (
    SELECT id FROM users 
    WHERE username = 'admin' 
    LIMIT 1
)
WHERE seller_id IS NULL;

-- Step 6: Make seller_id NOT NULL after migration (optional, uncomment if you want to enforce this)
-- ALTER TABLE products ALTER COLUMN seller_id SET NOT NULL;

-- Verification queries (run these to verify the migration)
-- SELECT * FROM roles WHERE name = 'ROLE_SELLER';
-- SELECT COUNT(*) FROM products WHERE seller_id IS NOT NULL;
-- SELECT p.name, u.username as seller FROM products p JOIN users u ON p.seller_id = u.id LIMIT 10;
