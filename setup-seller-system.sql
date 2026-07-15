-- =====================================================
-- B2B2C Multi-Vendor Marketplace Setup Script
-- Complete Seller System Database Initialization
-- =====================================================

-- Step 1: Ensure roles exist
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_SELLER')
ON CONFLICT (name) DO NOTHING;

-- Step 2: Create/Update system users
-- Admin user
INSERT INTO users (username, password) 
VALUES ('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW')
ON CONFLICT (username) DO NOTHING;

-- Regular user
INSERT INTO users (username, password) 
VALUES ('user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON CONFLICT (username) DO NOTHING;

-- Seller user
INSERT INTO users (username, password) 
VALUES ('seller', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON CONFLICT (username) DO NOTHING;

-- Additional test sellers
INSERT INTO users (username, password) 
VALUES 
  ('seller2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
  ('seller3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON CONFLICT (username) DO NOTHING;

-- Step 3: Assign roles to users
-- Admin role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- User role for regular user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'user' AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;

-- Seller roles
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username IN ('seller', 'seller2', 'seller3') AND r.name = 'ROLE_SELLER'
ON CONFLICT DO NOTHING;

-- Step 4: Create categories if they don't exist
INSERT INTO categories (name, description) VALUES
  ('Electronics', 'Electronic devices and gadgets'),
  ('Clothing', 'Apparel and fashion items'),
  ('Books', 'Physical books and literature'),
  ('Home & Garden', 'Home and garden products'),
  ('Sports & Outdoors', 'Sports equipment and outdoor gear')
ON CONFLICT DO NOTHING;

-- Step 5: Update existing products to have seller associations
-- Assign existing products (without seller) to the admin user
UPDATE products 
SET seller_id = (SELECT id FROM users WHERE username = 'admin')
WHERE seller_id IS NULL;

-- Step 6: Create sample seller products
-- Get seller IDs for reference
-- You can verify these IDs match in your database

-- Add sample products for seller1
INSERT INTO products (name, description, price, original_price, image_url, stock_quantity, brand, warranty, return_policy, category_id, seller_id)
SELECT 
  'Wireless Bluetooth Headphones',
  'Premium wireless headphones with noise cancellation',
  79.99,
  129.99,
  'https://via.placeholder.com/400x400?text=Headphones',
  50,
  'AudioTech',
  '1 year',
  '30 days',
  (SELECT id FROM categories WHERE name = 'Electronics' LIMIT 1),
  (SELECT id FROM users WHERE username = 'seller' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM products WHERE name = 'Wireless Bluetooth Headphones' 
  AND seller_id = (SELECT id FROM users WHERE username = 'seller')
);

INSERT INTO products (name, description, price, original_price, image_url, stock_quantity, brand, warranty, return_policy, category_id, seller_id)
SELECT 
  'USB-C Fast Charging Cable',
  '2-meter USB-C charging cable with fast charging support',
  19.99,
  39.99,
  'https://via.placeholder.com/400x400?text=Charging+Cable',
  200,
  'QuickCharge',
  '6 months',
  '30 days',
  (SELECT id FROM categories WHERE name = 'Electronics' LIMIT 1),
  (SELECT id FROM users WHERE username = 'seller' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM products WHERE name = 'USB-C Fast Charging Cable'
  AND seller_id = (SELECT id FROM users WHERE username = 'seller')
);

-- Add sample products for seller2
INSERT INTO products (name, description, price, original_price, image_url, stock_quantity, brand, warranty, return_policy, category_id, seller_id)
SELECT 
  'Cotton T-Shirt',
  '100% organic cotton t-shirt in multiple colors',
  29.99,
  49.99,
  'https://via.placeholder.com/400x400?text=T-Shirt',
  100,
  'FashionBrand',
  '6 months',
  '30 days',
  (SELECT id FROM categories WHERE name = 'Clothing' LIMIT 1),
  (SELECT id FROM users WHERE username = 'seller2' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM products WHERE name = 'Cotton T-Shirt'
  AND seller_id = (SELECT id FROM users WHERE username = 'seller2')
);

INSERT INTO products (name, description, price, original_price, image_url, stock_quantity, brand, warranty, return_policy, category_id, seller_id)
SELECT 
  'Running Shoes',
  'Professional running shoes with gel cushioning',
  99.99,
  149.99,
  'https://via.placeholder.com/400x400?text=Running+Shoes',
  75,
  'SportFit',
  '1 year',
  '30 days',
  (SELECT id FROM categories WHERE name = 'Sports & Outdoors' LIMIT 1),
  (SELECT id FROM users WHERE username = 'seller2' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM products WHERE name = 'Running Shoes'
  AND seller_id = (SELECT id FROM users WHERE username = 'seller2')
);

-- Add sample products for seller3
INSERT INTO products (name, description, price, original_price, image_url, stock_quantity, brand, warranty, return_policy, category_id, seller_id)
SELECT 
  'The Art of Programming',
  'Comprehensive guide to modern programming practices',
  49.99,
  79.99,
  'https://via.placeholder.com/400x400?text=Programming+Book',
  60,
  'TechBooks',
  'None',
  '30 days',
  (SELECT id FROM categories WHERE name = 'Books' LIMIT 1),
  (SELECT id FROM users WHERE username = 'seller3' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM products WHERE name = 'The Art of Programming'
  AND seller_id = (SELECT id FROM users WHERE username = 'seller3')
);

-- Step 7: Verification queries
SELECT '=== USER ROLE ASSIGNMENTS ===' as info;
SELECT u.username, GROUP_CONCAT(r.name) as roles 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username;

SELECT '=== SELLER PRODUCTS ===' as info;
SELECT p.id, p.name, u.username as seller, p.price, p.stock_quantity, p.created_at
FROM products p
JOIN users u ON p.seller_id = u.id
WHERE u.username IN ('seller', 'seller2', 'seller3')
ORDER BY u.username, p.name;

SELECT '=== PRODUCT COUNT BY SELLER ===' as info;
SELECT u.username, COUNT(p.id) as product_count
FROM users u
LEFT JOIN products p ON u.id = p.seller_id
WHERE u.username IN ('admin', 'seller', 'seller2', 'seller3')
GROUP BY u.id, u.username;
