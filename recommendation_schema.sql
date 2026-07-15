-- Recommendation Engine Database Schema
-- Tables for enterprise recommendation engine with multiple algorithms

-- Table to track frequently bought together items
CREATE TABLE IF NOT EXISTS frequently_bought_together (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    related_product_id BIGINT NOT NULL,
    purchase_count INT DEFAULT 1,
    confidence_score DECIMAL(5, 4) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (related_product_id) REFERENCES products(id),
    UNIQUE KEY unique_product_pair (product_id, related_product_id),
    INDEX idx_product (product_id),
    INDEX idx_confidence (confidence_score DESC)
);

-- Table to track customer browsing history
CREATE TABLE IF NOT EXISTS browsing_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    time_spent_seconds INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_user_timestamp (user_id, viewed_at DESC),
    INDEX idx_product_timestamp (product_id, viewed_at DESC)
);

-- Table to track product sales frequency for trending/popular products
CREATE TABLE IF NOT EXISTS product_sales_frequency (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL UNIQUE,
    sales_count_24h INT DEFAULT 0,
    sales_count_7d INT DEFAULT 0,
    sales_count_30d INT DEFAULT 0,
    last_sale_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_sales_24h (sales_count_24h DESC),
    INDEX idx_sales_7d (sales_count_7d DESC),
    INDEX idx_sales_30d (sales_count_30d DESC)
);

-- Table to track product ratings (enhanced from existing if any)
CREATE TABLE IF NOT EXISTS product_ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_user_product_rating (user_id, product_id),
    INDEX idx_product_rating (product_id, rating DESC),
    INDEX idx_user_rating (user_id, created_at DESC)
);

-- Table for product recommendation cache (stores computed recommendations)
CREATE TABLE IF NOT EXISTS product_recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    recommendation_type VARCHAR(50) NOT NULL,
    recommended_product_id BIGINT NOT NULL,
    score DECIMAL(10, 6) NOT NULL,
    rank_position INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (recommended_product_id) REFERENCES products(id),
    UNIQUE KEY unique_recommendation (product_id, recommendation_type, recommended_product_id),
    INDEX idx_product_type (product_id, recommendation_type),
    INDEX idx_expires (expires_at)
);

-- Table for personalized recommendations per user
CREATE TABLE IF NOT EXISTS user_recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recommendation_type VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    score DECIMAL(10, 6) NOT NULL,
    rank_position INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_user_type (user_id, recommendation_type),
    INDEX idx_user_expires (user_id, expires_at)
);

-- Table for category similarity scores
CREATE TABLE IF NOT EXISTS category_similarity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id_1 BIGINT NOT NULL,
    category_id_2 BIGINT NOT NULL,
    similarity_score DECIMAL(5, 4) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id_1) REFERENCES categories(id),
    FOREIGN KEY (category_id_2) REFERENCES categories(id),
    UNIQUE KEY unique_category_pair (category_id_1, category_id_2),
    INDEX idx_similarity (similarity_score DESC)
);

-- Table to track product view counts for analytics
CREATE TABLE IF NOT EXISTS product_view_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL UNIQUE,
    view_count_24h INT DEFAULT 0,
    view_count_7d INT DEFAULT 0,
    view_count_30d INT DEFAULT 0,
    avg_rating DECIMAL(3, 2),
    review_count INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_views_24h (view_count_24h DESC),
    INDEX idx_rating (avg_rating DESC)
);

-- Add columns to existing products table if they don't exist
ALTER TABLE products ADD COLUMN IF NOT EXISTS category_id BIGINT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS average_rating DECIMAL(3, 2) DEFAULT 0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS total_reviews INT DEFAULT 0;

-- Add column to orders table to track order items for frequently bought together
ALTER TABLE orders ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
