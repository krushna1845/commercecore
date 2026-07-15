-- Migration: Add approval_status column to products table
-- This enables the Admin-Seller Governance Bond system

ALTER TABLE products ADD COLUMN approval_status VARCHAR(20) DEFAULT 'PENDING';

-- Update existing products to APPROVED status (so existing products remain visible)
UPDATE products SET approval_status = 'APPROVED' WHERE approval_status IS NULL OR approval_status = 'PENDING';

-- Add check constraint to ensure only valid values
ALTER TABLE products ADD CONSTRAINT chk_approval_status CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED'));
