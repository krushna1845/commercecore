-- Sample Data for Amazon-Grade E-commerce Project

-- Insert sample addresses for admin user
INSERT INTO addresses (user_id, street, city, zip_code, phone, is_default) VALUES
(1, '123 Main St', 'New York', '10001', '+1234567890', true),
(1, '456 Oak Ave', 'Brooklyn', '11201', '+1234567891', false);

-- Insert sample coupons
INSERT INTO coupons (code, discount_percentage, expiry_date, is_active, usage_limit, times_used, created_at) VALUES
('WELCOME10', 10, DATE_ADD(NOW(), INTERVAL 30 DAY), true, 100, 0, NOW()),
('SUMMER20', 20, DATE_ADD(NOW(), INTERVAL 60 DAY), true, 50, 0, NOW()),
('FLASH30', 30, DATE_ADD(NOW(), INTERVAL 7 DAY), true, 25, 0, NOW()),
('LOYALTY15', 15, DATE_ADD(NOW(), INTERVAL 90 DAY), true, 0, 0, NOW()); -- unlimited usage

-- Insert sample reviews for products
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(1, 1, 5, 'Excellent laptop! Fast performance and great battery life.', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 2, 4, 'Good mouse, comfortable to use for long hours.', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 3, 5, 'Mechanical keyboard with amazing tactile feedback!', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Insert sample wishlist items
INSERT INTO wishlist (user_id, product_id, added_at) VALUES
(1, 1, NOW()),
(1, 3, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Create a regular user for testing
INSERT INTO users (username, password) VALUES ('testuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');

-- Assign user role to test user
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1); -- ROLE_USER

-- Insert sample addresses for test user
INSERT INTO addresses (user_id, street, city, zip_code, phone, is_default) VALUES
(2, '789 Pine Rd', 'Los Angeles', '90210', '+1987654321', true);

-- Insert sample reviews by test user
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 1, 4, 'Great laptop but a bit expensive.', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 2, 5, 'Perfect mouse for gaming!', DATE_SUB(NOW(), INTERVAL 4 DAY));

-- Insert sample wishlist items for test user
INSERT INTO wishlist (user_id, product_id, added_at) VALUES
(2, 2, NOW()),
(2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Additional sample products with different categories
INSERT INTO categories (name, description) VALUES 
('Books', 'Physical and digital books'),
('Clothing', 'Fashion and apparel');

INSERT INTO products (name, description, price, image_url, stock_quantity, category_id) VALUES 
('Clean Code', 'A handbook of agile software craftsmanship', 45.99, 'https://example.com/cleancode.jpg', 100, 2),
('T-Shirt', 'Premium cotton t-shirt', 29.99, 'https://example.com/tshirt.jpg', 75, 3);

-- More sample reviews
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(1, 4, 5, 'Must-read for every developer!', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 5, 3, 'Good quality but sizing runs small.', DATE_SUB(NOW(), INTERVAL 6 DAY));

-- More wishlist items
INSERT INTO wishlist (user_id, product_id, added_at) VALUES
(1, 4, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, 5, DATE_SUB(NOW(), INTERVAL 8 DAY));

-- Verify data insertion
SELECT 'Reviews created:' as info, COUNT(*) as count FROM reviews;
SELECT 'Wishlist items created:' as info, COUNT(*) as count FROM wishlist;
SELECT 'Addresses created:' as info, COUNT(*) as count FROM addresses;
SELECT 'Coupons created:' as info, COUNT(*) as count FROM coupons;
