-- Create an admin user for testing
-- First, find the admin role ID
SELECT id FROM roles WHERE name = 'ROLE_ADMIN';

-- Insert admin user (password: admin123)
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');

-- Link admin user with admin role (replace IDs with actual values from your database)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- Create a test category
INSERT INTO categories (name, description) VALUES ('Electronics', 'Electronic devices and gadgets');

-- Create some test products
INSERT INTO products (name, description, price, image_url, stock_quantity, category_id) VALUES 
('Laptop', 'High-performance laptop', 999.99, 'https://example.com/laptop.jpg', 10, 1),
('Mouse', 'Wireless mouse', 29.99, 'https://example.com/mouse.jpg', 50, 1),
('Keyboard', 'Mechanical keyboard', 79.99, 'https://example.com/keyboard.jpg', 25, 1);
