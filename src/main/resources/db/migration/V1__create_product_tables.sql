CREATE TABLE IF NOT EXISTS products (
                                        id VARCHAR(255) PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    deskripsi VARCHAR(255),
    harga DOUBLE PRECISION NOT NULL,
    stok INTEGER NOT NULL,
    negara_asal VARCHAR(255),
    tanggal_pembelian DATE,
    tanggal_kembali DATE,
    jastiper_id VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS product_images (
                                              product_id VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    CONSTRAINT fk_product_images_product
    FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_products_nama
    ON products(nama);

CREATE INDEX IF NOT EXISTS idx_products_jastiper_id
    ON products(jastiper_id);

CREATE INDEX IF NOT EXISTS idx_product_images_product_id
    ON product_images(product_id);