ALTER TABLE products
    ADD COLUMN IF NOT EXISTS product_type VARCHAR(50) NOT NULL DEFAULT 'REGULAR';

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS war_start_time TIMESTAMP;

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS war_end_time TIMESTAMP;

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS max_quantity_per_checkout INTEGER NOT NULL DEFAULT 0;

ALTER TABLE products
DROP CONSTRAINT IF EXISTS chk_products_product_type;

ALTER TABLE products
    ADD CONSTRAINT chk_products_product_type
        CHECK (product_type IN ('REGULAR', 'LIMITED'));

ALTER TABLE products
DROP CONSTRAINT IF EXISTS chk_products_max_quantity_per_checkout;

ALTER TABLE products
    ADD CONSTRAINT chk_products_max_quantity_per_checkout
        CHECK (max_quantity_per_checkout >= 0);