-- Data Population Script for Recommendation Engine
-- This script populates sample data to initialize the recommendation engine

-- 1. Update existing products with category information
UPDATE products SET category_id = 1, average_rating = 4.5, total_reviews = 120 WHERE id BETWEEN 1 AND 10;
UPDATE products SET category_id = 2, average_rating = 4.3, total_reviews = 95 WHERE id BETWEEN 11 AND 20;
UPDATE products SET category_id = 3, average_rating = 4.7, total_reviews = 180 WHERE id BETWEEN 21 AND 30;

-- 2. Initialize ProductSalesFrequency for all products
INSERT INTO product_sales_frequency (product_id, sales_count_24h, sales_count_7d, sales_count_30d, last_sale_at, updated_at)
SELECT id, FLOOR(RAND() * 100), FLOOR(RAND() * 500), FLOOR(RAND() * 2000), NOW(), NOW()
FROM products
ON DUPLICATE KEY UPDATE
  sales_count_24h = VALUES(sales_count_24h),
  sales_count_7d = VALUES(sales_count_7d),
  sales_count_30d = VALUES(sales_count_30d),
  last_sale_at = VALUES(last_sale_at);

-- 3. Initialize ProductViewStats for all products
INSERT INTO product_view_stats (product_id, view_count_24h, view_count_7d, view_count_30d, avg_rating, review_count, updated_at)
SELECT id, FLOOR(RAND() * 500), FLOOR(RAND() * 2000), FLOOR(RAND() * 8000), 
       ROUND(3.5 + RAND() * 1.5, 1), FLOOR(RAND() * 300), NOW()
FROM products
ON DUPLICATE KEY UPDATE
  view_count_24h = VALUES(view_count_24h),
  view_count_7d = VALUES(view_count_7d),
  view_count_30d = VALUES(view_count_30d),
  avg_rating = VALUES(avg_rating),
  review_count = VALUES(review_count);

-- 4. Populate sample ProductRatings
INSERT INTO product_ratings (product_id, user_id, rating, review, created_at, updated_at)
SELECT 
  p.id,
  u.id,
  FLOOR(3 + RAND() * 3) as rating,
  CONCAT('Great product! Rating: ', FLOOR(3 + RAND() * 3)) as review,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
  NOW()
FROM products p
CROSS JOIN users u
WHERE RAND() < 0.1
ON DUPLICATE KEY UPDATE
  rating = VALUES(rating),
  review = VALUES(review);

-- 5. Populate FrequentlyBoughtTogether relationships
-- This assumes products 1-3 are often bought together, 4-6 are often bought together, etc.
INSERT INTO frequently_bought_together (product_id, related_product_id, purchase_count, confidence_score, created_at, updated_at)
VALUES
  (1, 2, 45, 0.85, NOW(), NOW()),
  (1, 3, 38, 0.75, NOW(), NOW()),
  (2, 1, 45, 0.85, NOW(), NOW()),
  (2, 3, 32, 0.65, NOW(), NOW()),
  (3, 1, 38, 0.75, NOW(), NOW()),
  (3, 2, 32, 0.65, NOW(), NOW()),
  (4, 5, 52, 0.90, NOW(), NOW()),
  (4, 6, 28, 0.60, NOW(), NOW()),
  (5, 4, 52, 0.90, NOW(), NOW()),
  (5, 6, 35, 0.70, NOW(), NOW()),
  (6, 4, 28, 0.60, NOW(), NOW()),
  (6, 5, 35, 0.70, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  purchase_count = VALUES(purchase_count),
  confidence_score = VALUES(confidence_score);

-- 6. Populate CategorySimilarity
INSERT INTO category_similarity (category_id_1, category_id_2, similarity_score, updated_at)
VALUES
  (1, 2, 0.75, NOW()),
  (1, 3, 0.60, NOW()),
  (2, 3, 0.85, NOW()),
  (1, 4, 0.45, NOW()),
  (2, 4, 0.55, NOW()),
  (3, 4, 0.50, NOW())
ON DUPLICATE KEY UPDATE
  similarity_score = VALUES(similarity_score);

-- 7. Sample BrowsingHistory (simulate user browsing)
INSERT INTO browsing_history (user_id, product_id, viewed_at, time_spent_seconds)
SELECT 
  u.id,
  p.id,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 7) DAY),
  FLOOR(20 + RAND() * 300)
FROM users u
CROSS JOIN (SELECT DISTINCT id FROM products LIMIT 10) p
WHERE RAND() < 0.2
LIMIT 100;

-- Verification queries
SELECT 'Products with sales frequency' as check_type, COUNT(*) as count FROM product_sales_frequency;
SELECT 'Products with view stats' as check_type, COUNT(*) as count FROM product_view_stats;
SELECT 'Product ratings' as check_type, COUNT(*) as count FROM product_ratings;
SELECT 'Frequently bought together' as check_type, COUNT(*) as count FROM frequently_bought_together;
SELECT 'Category similarities' as check_type, COUNT(*) as count FROM category_similarity;
SELECT 'Browsing history' as check_type, COUNT(*) as count FROM browsing_history;
