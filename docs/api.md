## API overview

All endpoints are prefixed with `/api`. Except for `POST /api/auth/line`, endpoints require `Authorization: Bearer <app-jwt>` (issued by this service).

### Auth

- `POST /api/auth/line`
  - Request: `{ "idToken": "<string>" }`
  - Response: `{ "token": "<app-jwt>", "user": { "id": 1, "displayName": "User Name", "pictureUrl": "https://example.com/avatar.png" } }`

### Scan

- `POST /api/scan`
  - Request: `{ "barcode": "4901234567890" }`
  - Response: `{ "productCandidate": { "id": 10, "barcode": "...", "name": "Beans", "brand": "Sample", "category": "canned" }, "lastUsedExpiryTemplates": ["2025-06-01", "2025-12-01"] }`
  - Behavior: looks up products by barcode and, if the user has registered this barcode before, returns recent expiry dates as templates.

### Stock registration

- `POST /api/stocks`
  - Request: `{ "barcode": "...", "name": "string", "brand": "string (optional)", "category": "string", "quantity": 2, "unit": "can", "expiresOn": "2025-01-01", "purchasedOn": "2024-12-20" }`
  - Behavior: creates the product if missing, upserts the user/product stock item, adds a stock lot, and recalculates `total_quantity`.
  - Response: `{ "id": 5, "product": { "id": 10, "barcode": "...", "name": "string", "brand": "string", "category": "string" }, "unit": "can", "totalQuantity": 2, "nextLot": { "id": 12, "expiresOn": "2025-01-01", "quantity": 2, "purchasedOn": "2024-12-20" } }`

### List stocks

- `GET /api/stocks?sort=expiresSoon`
  - Returns the user's stock items including the earliest expiring lot per item. `sort=expiresSoon` (default) orders by soonest expiry, otherwise falls back to product name.

### Consume

- `POST /api/consume`
  - Request: `{ "stockItemId": 1, "quantity": 2, "reason": "consume|dispose|adjust" }` (`reason` is optional; defaults to `consume`)
  - Response: `{ "consumedLots": [{ "id": 2, "expiresOn": "2024-12-01", "quantity": 2, "purchasedOn": "2024-11-20" }], "remainingTotal": 3 }`

### Today tasks

- `GET /api/tasks/today`
  - Response (mock):
    - `expiringSoon[]`
    - `expired[]`
    - `lowStock[]`
    - `suggestedConsume[]`

### Emergency plan (phase 2)

- `GET /api/plan/emergency?people=4&days=3`
  - Response (mock): per-day calorie-friendly combination suggestions.
