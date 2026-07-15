-- ============================================
-- SAMPLE DATA FOR COMMERCECORE E-COMMERCE
-- ============================================
-- This script adds realistic sample data for:
-- - Categories
-- - Products
-- - Users (Admin, Customer, Seller)
-- - Orders
-- - Reviews
-- - Coupons
-- ============================================

INSERT IGNORE INTO categories (name, description) VALUES
('Electronics', 'Smartphones, laptops, audio and more'),
('Apparel', 'Clothing and footwear'),
('Home & Kitchen', 'Appliances and housewares'),
('Books', 'Fiction and non-fiction books'),
('Sports', 'Fitness and outdoor gear'),
('Beauty', 'Skincare and cosmetics'),
('Toys & Games', 'Toys and games for all ages'),
('Automotive', 'Car accessories and electronics');
-- ============================================
-- PRODUCTS (cleaned, parent-first)
-- ============================================
INSERT IGNORE INTO products (name, description, price, original_price, image_url, stock_quantity, brand, warranty, return_policy, rating, review_count, category_id) VALUES
('iPhone 15 Pro', 'Apple flagship smartphone with A17 chip', 1199.00, 1299.00, 'https://example.com/images/iphone15pro.jpg', 25, 'Apple', '1 year', '30-day return', 4.8, 124, (SELECT id FROM categories WHERE name='Electronics' LIMIT 1)),
('Galaxy S24 Ultra', 'Samsung flagship with pro camera', 999.00, 1099.00, 'https://example.com/images/galaxys24.jpg', 18, 'Samsung', '1 year', '30-day return', 4.7, 98, (SELECT id FROM categories WHERE name='Electronics' LIMIT 1)),
('MacBook Air M3', 'Lightweight laptop with M3 chip', 1299.00, 1399.00, 'https://example.com/images/macbookairm3.jpg', 12, 'Apple', '1 year', '14-day return', 4.9, 76, (SELECT id FROM categories WHERE name='Electronics' LIMIT 1)),
('Bose QC45 Headphones', 'Noise cancelling over-ear headphones', 329.00, 349.00, 'https://example.com/images/boseqc45.jpg', 30, 'Bose', '1 year', '30-day return', 4.6, 210, (SELECT id FROM categories WHERE name='Electronics' LIMIT 1)),
('Instant Pot Duo 7-in-1', 'Multi-use pressure cooker', 89.00, 129.00, 'https://example.com/images/instantpot.jpg', 40, 'Instant Pot', '2 years', '30-day return', 4.5, 412, (SELECT id FROM categories WHERE name='Home & Kitchen' LIMIT 1)),
('Nike Air Max 270', 'Comfortable lifestyle sneakers', 149.00, 169.00, 'https://example.com/images/airmax270.jpg', 50, 'Nike', '6 months', '30-day return', 4.4, 65, (SELECT id FROM categories WHERE name='Apparel' LIMIT 1)),
('Levi''s 501 Original Jeans', 'Classic straight fit denim', 59.00, 79.00, 'https://example.com/images/levis501.jpg', 80, 'Levi''s', '6 months', '30-day return', 4.3, 44, (SELECT id FROM categories WHERE name='Apparel' LIMIT 1)),
('Harry Potter Complete Box Set', 'All 7 books in paperback', 89.00, 99.00, 'https://example.com/images/harrypotterbox.jpg', 60, 'Bloomsbury', 'N/A', '30-day return', 4.9, 1020, (SELECT id FROM categories WHERE name='Books' LIMIT 1)),
('Atomic Habits', 'Tiny changes, remarkable results', 16.99, 24.99, 'https://example.com/images/atomichabits.jpg', 120, 'Penguin', 'N/A', '30-day return', 4.8, 860, (SELECT id FROM categories WHERE name='Books' LIMIT 1)),
('Fitbit Charge 6', 'Advanced health & fitness tracker', 149.95, 179.95, 'https://example.com/images/fitbitcharge6.jpg', 35, 'Fitbit', '1 year', '30-day return', 4.2, 53, (SELECT id FROM categories WHERE name='Sports' LIMIT 1)),
('LEGO Star Wars Millennium Falcon', 'Large-scale Lego set', 159.99, 199.99, 'https://example.com/images/legomf.jpg', 22, 'LEGO', 'N/A', '30-day return', 4.9, 310, (SELECT id FROM categories WHERE name='Toys & Games' LIMIT 1)),
('Car Bluetooth FM Transmitter', 'Handy car accessory for audio', 29.99, 39.99, 'https://example.com/images/carbt.jpg', 75, 'Anker', '1 year', '30-day return', 4.0, 18, (SELECT id FROM categories WHERE name='Automotive' LIMIT 1));

-- PRODUCT SPECS
INSERT IGNORE INTO product_specs (product_id, spec_key, spec_value)
SELECT p.id, 'Screen', '6.1 inch OLED' FROM products p WHERE p.name='iPhone 15 Pro'
UNION ALL SELECT p.id, 'Processor', 'A17 Bionic' FROM products p WHERE p.name='iPhone 15 Pro'
UNION ALL SELECT p.id, 'Storage', '256GB' FROM products p WHERE p.name='iPhone 15 Pro'
UNION ALL SELECT p.id, 'Screen', '6.8 inch AMOLED' FROM products p WHERE p.name='Galaxy S24 Ultra'
UNION ALL SELECT p.id, 'Processor', 'Snapdragon 8 Gen 3' FROM products p WHERE p.name='Galaxy S24 Ultra'
UNION ALL SELECT p.id, 'Storage', '256GB' FROM products p WHERE p.name='Galaxy S24 Ultra'
UNION ALL SELECT p.id, 'CPU', 'Apple M3' FROM products p WHERE p.name='MacBook Air M3'
UNION ALL SELECT p.id, 'RAM', '16GB' FROM products p WHERE p.name='MacBook Air M3'
UNION ALL SELECT p.id, 'Storage', '512GB SSD' FROM products p WHERE p.name='MacBook Air M3'
UNION ALL SELECT p.id, 'Type', 'Over-ear' FROM products p WHERE p.name='Bose QC45 Headphones'
UNION ALL SELECT p.id, 'ANC', 'Active' FROM products p WHERE p.name='Bose QC45 Headphones'
UNION ALL SELECT p.id, 'Battery', '24 hours' FROM products p WHERE p.name='Bose QC45 Headphones'
UNION ALL SELECT p.id, 'Capacity', '6 quarts' FROM products p WHERE p.name='Instant Pot Duo 7-in-1'
UNION ALL SELECT p.id, 'Material', 'Stainless Steel' FROM products p WHERE p.name='Instant Pot Duo 7-in-1'
UNION ALL SELECT p.id, 'Programs', '7' FROM products p WHERE p.name='Instant Pot Duo 7-in-1'
UNION ALL SELECT p.id, 'Upper Material', 'Mesh' FROM products p WHERE p.name='Nike Air Max 270'
UNION ALL SELECT p.id, 'Sole', 'Air Unit' FROM products p WHERE p.name='Nike Air Max 270'
UNION ALL SELECT p.id, 'Sizes', '7-13' FROM products p WHERE p.name='Nike Air Max 270'
UNION ALL SELECT p.id, 'Fit', 'Straight' FROM products p WHERE p.name='Levi''s 501 Original Jeans'
UNION ALL SELECT p.id, 'Material', '100% Cotton' FROM products p WHERE p.name='Levi''s 501 Original Jeans'
UNION ALL SELECT p.id, 'Waist Sizes', '28-40' FROM products p WHERE p.name='Levi''s 501 Original Jeans'
UNION ALL SELECT p.id, 'Volumes', '7 books' FROM products p WHERE p.name='Harry Potter Complete Box Set'
UNION ALL SELECT p.id, 'Pages', 'Approx 4100' FROM products p WHERE p.name='Harry Potter Complete Box Set'
UNION ALL SELECT p.id, 'Format', 'Paperback' FROM products p WHERE p.name='Harry Potter Complete Box Set'
UNION ALL SELECT p.id, 'Pages', '320' FROM products p WHERE p.name='Atomic Habits'
UNION ALL SELECT p.id, 'Author', 'James Clear' FROM products p WHERE p.name='Atomic Habits'
UNION ALL SELECT p.id, 'Format', 'Paperback' FROM products p WHERE p.name='Atomic Habits'
UNION ALL SELECT p.id, 'Sensors', 'Heart rate, SpO2' FROM products p WHERE p.name='Fitbit Charge 6'
UNION ALL SELECT p.id, 'Battery', '7 days' FROM products p WHERE p.name='Fitbit Charge 6'
UNION ALL SELECT p.id, 'Water Resistant', 'Yes' FROM products p WHERE p.name='Fitbit Charge 6'
UNION ALL SELECT p.id, 'Pieces', '754' FROM products p WHERE p.name='LEGO Star Wars Millennium Falcon'
UNION ALL SELECT p.id, 'Recommended Age', '16+' FROM products p WHERE p.name='LEGO Star Wars Millennium Falcon'
UNION ALL SELECT p.id, 'Dimensions', '33 x 22 x 12 in' FROM products p WHERE p.name='LEGO Star Wars Millennium Falcon'
UNION ALL SELECT p.id, 'Connectivity', 'Bluetooth' FROM products p WHERE p.name='Car Bluetooth FM Transmitter'
UNION ALL SELECT p.id, 'Power', '12V' FROM products p WHERE p.name='Car Bluetooth FM Transmitter'
UNION ALL SELECT p.id, 'Channels', 'FM' FROM products p WHERE p.name='Car Bluetooth FM Transmitter';

-- PRODUCT FEATURES
INSERT IGNORE INTO product_features (product_id, feature)
SELECT p.id, 'Premium camera' FROM products p WHERE p.name='iPhone 15 Pro'
UNION ALL SELECT p.id, 'Titanium frame' FROM products p WHERE p.name='iPhone 15 Pro'
UNION ALL SELECT p.id, 'Long battery life' FROM products p WHERE p.name='iPhone 15 Pro'
UNION ALL SELECT p.id, 'High zoom camera' FROM products p WHERE p.name='Galaxy S24 Ultra'
UNION ALL SELECT p.id, 'S Pen support' FROM products p WHERE p.name='Galaxy S24 Ultra'
UNION ALL SELECT p.id, '120Hz display' FROM products p WHERE p.name='Galaxy S24 Ultra'
UNION ALL SELECT p.id, 'Fanless design' FROM products p WHERE p.name='MacBook Air M3'
UNION ALL SELECT p.id, 'Retina display' FROM products p WHERE p.name='MacBook Air M3'
UNION ALL SELECT p.id, 'Lightweight' FROM products p WHERE p.name='MacBook Air M3'
UNION ALL SELECT p.id, 'Comfort fit' FROM products p WHERE p.name='Bose QC45 Headphones'
UNION ALL SELECT p.id, 'Superior ANC' FROM products p WHERE p.name='Bose QC45 Headphones'
UNION ALL SELECT p.id, 'Long battery' FROM products p WHERE p.name='Bose QC45 Headphones'
UNION ALL SELECT p.id, 'Multi-cooker' FROM products p WHERE p.name='Instant Pot Duo 7-in-1'
UNION ALL SELECT p.id, 'Programmable' FROM products p WHERE p.name='Instant Pot Duo 7-in-1'
UNION ALL SELECT p.id, 'Easy clean' FROM products p WHERE p.name='Instant Pot Duo 7-in-1'
UNION ALL SELECT p.id, 'Air cushioning' FROM products p WHERE p.name='Nike Air Max 270'
UNION ALL SELECT p.id, 'Stylish' FROM products p WHERE p.name='Nike Air Max 270'
UNION ALL SELECT p.id, 'Breathable' FROM products p WHERE p.name='Nike Air Max 270'
UNION ALL SELECT p.id, 'Durable denim' FROM products p WHERE p.name='Levi''s 501 Original Jeans'
UNION ALL SELECT p.id, 'Classic style' FROM products p WHERE p.name='Levi''s 501 Original Jeans'
UNION ALL SELECT p.id, 'Machine wash' FROM products p WHERE p.name='Levi''s 501 Original Jeans'
UNION ALL SELECT p.id, 'Collector''s edition' FROM products p WHERE p.name='Harry Potter Complete Box Set'
UNION ALL SELECT p.id, 'Great gift' FROM products p WHERE p.name='Harry Potter Complete Box Set'
UNION ALL SELECT p.id, 'Complete series' FROM products p WHERE p.name='Harry Potter Complete Box Set'
UNION ALL SELECT p.id, 'Practical advice' FROM products p WHERE p.name='Atomic Habits'
UNION ALL SELECT p.id, 'Actionable tips' FROM products p WHERE p.name='Atomic Habits'
UNION ALL SELECT p.id, 'Evidence-based' FROM products p WHERE p.name='Atomic Habits'
UNION ALL SELECT p.id, 'Health tracking' FROM products p WHERE p.name='Fitbit Charge 6'
UNION ALL SELECT p.id, 'Sleep monitoring' FROM products p WHERE p.name='Fitbit Charge 6'
UNION ALL SELECT p.id, 'Waterproof' FROM products p WHERE p.name='Fitbit Charge 6'
UNION ALL SELECT p.id, 'Licensed set' FROM products p WHERE p.name='LEGO Star Wars Millennium Falcon'
UNION ALL SELECT p.id, 'Display worthy' FROM products p WHERE p.name='LEGO Star Wars Millennium Falcon'
UNION ALL SELECT p.id, 'High detail' FROM products p WHERE p.name='LEGO Star Wars Millennium Falcon'
UNION ALL SELECT p.id, 'Plug and play' FROM products p WHERE p.name='Car Bluetooth FM Transmitter'
UNION ALL SELECT p.id, 'Hands-free calls' FROM products p WHERE p.name='Car Bluetooth FM Transmitter'
UNION ALL SELECT p.id, 'USB charging' FROM products p WHERE p.name='Car Bluetooth FM Transmitter';

-- PRODUCTS (temporarily commented out due to malformed JSON/array literals in this dump)
-- USERS

-- Admin User
-- ROLES
INSERT IGNORE INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_ADMIN');

-- USERS (only username/password columns exist in the JPA entity)
INSERT IGNORE INTO users (username, password) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Customer Users
INSERT IGNORE INTO users (username, password) VALUES
('john_doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('jane_smith', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('mike_wilson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('sarah_jones', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('david_brown', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Seller Users
INSERT IGNORE INTO users (username, password) VALUES
('tech_store', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('fashion_hub', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('home_essentials', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- ============================================
-- ADDRESSES
-- ============================================

-- ADDRESSES (match fields in Address.java)
INSERT IGNORE INTO addresses (user_id, street, city, zip_code, phone, is_default) VALUES
(2, '123 Main Street, Apt 4B', 'New York', '10001', '+11234567891', TRUE),
(2, '456 Business Ave, Suite 100', 'New York', '10002', '+11234567892', FALSE),
(3, '789 Oak Lane', 'Los Angeles', '90001', '+11234567893', TRUE),
(4, '321 Pine Road', 'Chicago', '60601', '+11234567894', TRUE),
(5, '654 Elm Street', 'Houston', '77001', '+11234567895', TRUE),
(6, '987 Cedar Drive', 'Phoenix', '85001', '+11234567896', TRUE);

-- ============================================
-- ORDERS
-- ============================================

-- ORDERS (matches Order.java fields)
INSERT IGNORE INTO orders (user_id, total_amount, status, stripe_payment_intent_id, created_at) VALUES
(2, 1199.00, 'SHIPPED', 'pi_1234567890', DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2, 349.00, 'SHIPPED', 'pi_1234567891', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, 89.00, 'PENDING', 'pi_1234567892', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 159.00, 'SHIPPED', 'pi_1234567893', DATE_SUB(NOW(), INTERVAL 45 DAY)),
(5, 599.00, 'PENDING', 'pi_1234567894', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 169.00, 'SHIPPED', 'pi_1234567895', DATE_SUB(NOW(), INTERVAL 60 DAY));

-- ============================================
-- ORDER ITEMS
-- ============================================
-- ORDER ITEMS (re-enabled, mapped by stripe id + product name)
INSERT IGNORE INTO order_items (order_id, product_id, quantity, price_at_purchase)
SELECT o.id, p.id, 1, p.price FROM orders o JOIN products p ON p.name='iPhone 15 Pro' WHERE o.stripe_payment_intent_id='pi_1234567890'
UNION ALL
SELECT o.id, p.id, 1, p.price FROM orders o JOIN products p ON p.name='Galaxy S24 Ultra' WHERE o.stripe_payment_intent_id='pi_1234567891'
UNION ALL
SELECT o.id, p.id, 1, p.price FROM orders o JOIN products p ON p.name='Atomic Habits' WHERE o.stripe_payment_intent_id='pi_1234567892'
UNION ALL
SELECT o.id, p.id, 1, p.price FROM orders o JOIN products p ON p.name='Bose QC45 Headphones' WHERE o.stripe_payment_intent_id='pi_1234567893'
UNION ALL
SELECT o.id, p.id, 1, p.price FROM orders o JOIN products p ON p.name='Harry Potter Complete Box Set' WHERE o.stripe_payment_intent_id='pi_1234567894'
UNION ALL
SELECT o.id, p.id, 1, p.price FROM orders o JOIN products p ON p.name='Fitbit Charge 6' WHERE o.stripe_payment_intent_id='pi_1234567895';

-- ============================================
-- REVIEWS
-- ============================================
-- REVIEWS (re-enabled)
INSERT IGNORE INTO reviews (user_id, product_id, rating, comment, created_at)
SELECT u.id, p.id, 5, 'Amazing product, highly recommended!', DATE_SUB(NOW(), INTERVAL 25 DAY) FROM users u JOIN products p ON p.name='iPhone 15 Pro' WHERE u.username='john_doe'
UNION ALL
SELECT u.id, p.id, 4, 'Great value for money.', DATE_SUB(NOW(), INTERVAL 20 DAY) FROM users u JOIN products p ON p.name='Galaxy S24 Ultra' WHERE u.username='jane_smith'
UNION ALL
SELECT u.id, p.id, 5, 'Exactly as described.', DATE_SUB(NOW(), INTERVAL 40 DAY) FROM users u JOIN products p ON p.name='MacBook Air M3' WHERE u.username='mike_wilson'
UNION ALL
SELECT u.id, p.id, 4, 'Good quality and fast delivery.', DATE_SUB(NOW(), INTERVAL 10 DAY) FROM users u JOIN products p ON p.name='Bose QC45 Headphones' WHERE u.username='sarah_jones'
UNION ALL
SELECT u.id, p.id, 5, 'A must-read!', DATE_SUB(NOW(), INTERVAL 60 DAY) FROM users u JOIN products p ON p.name='Atomic Habits' WHERE u.username='david_brown';

-- ============================================
-- COUPONS
-- ============================================

INSERT IGNORE INTO coupons (code, discount_percentage, expiry_date, is_active, usage_limit, times_used, created_at) VALUES
('WELCOME10', 10, DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE, 1000, 250, NOW()),
('SAVE20', 20, DATE_ADD(NOW(), INTERVAL 60 DAY), TRUE, 500, 120, NOW()),
('FLAT50', 50, DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE, 200, 45, NOW()),
('FLASH25', 25, DATE_ADD(NOW(), INTERVAL 15 DAY), TRUE, 100, 30, NOW()),
('NEWUSER', 15, DATE_ADD(NOW(), INTERVAL 180 DAY), TRUE, 5000, 890, NOW());

-- ============================================
-- WISHLIST (Legacy - will be migrated)
-- ============================================
-- WISHLIST (re-enabled, uses composite key)
INSERT IGNORE INTO wishlist (user_id, product_id, added_at)
SELECT u.id, p.id, DATE_SUB(NOW(), INTERVAL 10 DAY) FROM users u JOIN products p ON p.name='iPhone 15 Pro' WHERE u.username='john_doe'
UNION ALL
SELECT u.id, p.id, DATE_SUB(NOW(), INTERVAL 8 DAY) FROM users u JOIN products p ON p.name='Galaxy S24 Ultra' WHERE u.username='john_doe'
UNION ALL
SELECT u.id, p.id, DATE_SUB(NOW(), INTERVAL 5 DAY) FROM users u JOIN products p ON p.name='MacBook Air M3' WHERE u.username='jane_smith'
UNION ALL
SELECT u.id, p.id, DATE_SUB(NOW(), INTERVAL 15 DAY) FROM users u JOIN products p ON p.name='Bose QC45 Headphones' WHERE u.username='mike_wilson'
UNION ALL
SELECT u.id, p.id, DATE_SUB(NOW(), INTERVAL 12 DAY) FROM users u JOIN products p ON p.name='Harry Potter Complete Box Set' WHERE u.username='mike_wilson';

-- ============================================
-- END OF SAMPLE DATA
-- ============================================
