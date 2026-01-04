## Architecture (baseline)

- **Frontend (web/)**: Next.js (App Router) LIFF client. Acquires LINE `idToken`, calls backend to exchange for app JWT, surfaces four core screens (home/tasks, scan, stocks, consume). Early phases rely on mocked API responses for rapid UI iteration.
- **Backend (api/)**: Spring Boot REST API. Handles LINE Login token verification, JWT issuance, stock/lot management with FEFO consumption, and scheduled digests under a `worker` profile.
- **Worker (api/, worker profile)**: Scheduled job runner sharing the same codebase. Sends digest notifications and refreshes suggestion caches.
- **Database**: PostgreSQL primary store; Redis optional for queues/caching/rate limiting.
- **Reverse proxy (infra/nginx)**: Proxies `/api` to backend and everything else to Next.js.
- **Local orchestration**: Docker Compose wiring services together for development.

### Data model (MVP)

- `users(id, line_sub, display_name, picture_url, timestamps)`
- `products(id, barcode, name, brand, default_category, nutrition_json)`
- `stock_items(id, user_id, product_id, total_quantity, unit, timestamps)`
- `stock_lots(id, stock_item_id, quantity, expires_on, purchased_on, timestamps)`
- `consumption_logs(id, user_id, stock_lot_id, delta_quantity, reason, created_at)`

### Flow highlights

- **Auth**: LIFF → `idToken` → `POST /api/auth/line` → verify via JWKS → upsert user → issue app JWT.
- **Scan**: `POST /api/scan { barcode }` → product candidate & expiry templates.
- **Register**: `POST /api/stocks` with minimal fields → stock item + lot created.
- **Consume**: `POST /api/consume { stockItemId, quantity }` → FEFO depletion of lots.
- **Tasks**: `GET /api/tasks/today` returning expiring/expired/low stock/suggestions buckets.

### Profiles

- `dev` (default): in-memory/mock services for rapid iteration.
- `worker`: scheduled digest sender and suggestion refresher; shares domain services.

### Security notes

- LINE JWKS URL must be configurable and cached.
- Application JWT secret kept in `APP_JWT_SECRET`.
- Ensure `aud/iss/exp` validation on LINE ID tokens.
