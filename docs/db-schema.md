## Database schema (initial proposal)

### users
- `id` (pk, bigint)
- `line_sub` (text, unique)
- `display_name` (text)
- `picture_url` (text, nullable)
- `created_at`, `updated_at`

### products
- `id` (pk, bigint)
- `barcode` (text, unique)
- `name` (text)
- `brand` (text, nullable)
- `default_category` (text, nullable)
- `nutrition_json` (jsonb, nullable)
- `created_at`, `updated_at`

### stock_items
- `id` (pk, bigint)
- `user_id` (fk → users.id)
- `product_id` (fk → products.id)
- `total_quantity` (numeric)
- `unit` (text)
- `created_at`, `updated_at`

### stock_lots
- `id` (pk, bigint)
- `stock_item_id` (fk → stock_items.id)
- `quantity` (numeric)
- `expires_on` (date)
- `purchased_on` (date, nullable)
- `created_at`, `updated_at`

### consumption_logs
- `id` (pk, bigint)
- `user_id` (fk → users.id)
- `stock_lot_id` (fk → stock_lots.id)
- `delta_quantity` (numeric)
- `reason` (enum: consume/dispose/adjust)
- `created_at`
