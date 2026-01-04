CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    line_sub VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    picture_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    barcode VARCHAR(64) UNIQUE,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    default_category VARCHAR(255),
    nutrition_json JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stock_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    total_quantity INTEGER NOT NULL DEFAULT 0,
    unit VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, product_id)
);

CREATE TABLE IF NOT EXISTS stock_lots (
    id BIGSERIAL PRIMARY KEY,
    stock_item_id BIGINT NOT NULL REFERENCES stock_items(id),
    quantity INTEGER NOT NULL,
    expires_on DATE NOT NULL,
    purchased_on DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS consumption_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    stock_lot_id BIGINT REFERENCES stock_lots(id),
    delta_quantity INTEGER NOT NULL,
    reason VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_stock_items_user ON stock_items(user_id);
CREATE INDEX IF NOT EXISTS idx_stock_lots_item_expires ON stock_lots(stock_item_id, expires_on);
CREATE INDEX IF NOT EXISTS idx_consumption_logs_user ON consumption_logs(user_id);
