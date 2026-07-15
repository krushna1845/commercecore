-- ============================================
-- MILESTONE 6 & 7 MIGRATION
-- Shipping Tracking & Payouts Ledger
-- ============================================

-- 1. Add Tracking Information to OrderItems
ALTER TABLE order_items ADD COLUMN tracking_number VARCHAR(255) NULL;
ALTER TABLE order_items ADD COLUMN courier_name VARCHAR(255) NULL;

-- 2. Create the Payouts Ledger Table
CREATE TABLE payouts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reference_number VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    paid_at DATETIME NULL,
    CONSTRAINT fk_payout_seller FOREIGN KEY (seller_id) REFERENCES users(id)
);
