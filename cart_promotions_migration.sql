-- ============================================
-- CART & PROMOTIONS MIGRATION (Milestone 4)
-- ============================================

ALTER TABLE cart_items MODIFY user_id BIGINT NULL;
ALTER TABLE cart_items ADD COLUMN guest_id VARCHAR(255) NULL;
ALTER TABLE cart_items DROP INDEX UK_cart_user_product;

ALTER TABLE coupons ADD COLUMN discount_type VARCHAR(50) NOT NULL DEFAULT 'PERCENTAGE';
ALTER TABLE coupons ADD COLUMN discount_amount DOUBLE NOT NULL DEFAULT 0.0;
ALTER TABLE coupons ADD COLUMN min_purchase_amount DOUBLE NOT NULL DEFAULT 0.0;
