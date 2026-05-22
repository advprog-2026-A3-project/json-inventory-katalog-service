ALTER TABLE products
    ADD COLUMN IF NOT EXISTS rating DOUBLE PRECISION NOT NULL DEFAULT 0.0;

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS rating_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE products
    ADD CONSTRAINT chk_products_rating_range
        CHECK (rating >= 0.0 AND rating <= 5.0);

ALTER TABLE products
    ADD CONSTRAINT chk_products_rating_count_non_negative
        CHECK (rating_count >= 0);