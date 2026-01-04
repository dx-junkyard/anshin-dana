## API draft (mock-first)

All endpoints are prefixed with `/api`.

### Auth

- `POST /api/auth/line`
  - Request: `{ "idToken": "<string>" }`
  - Response (mock): `{ "token": "<app-jwt>", "user": { "id": 1, "displayName": "Mock User", "pictureUrl": "https://example.com/avatar.png" } }`

### Scan

- `POST /api/scan`
  - Request: `{ "barcode": "4901234567890" }`
  - Response (mock): `{ "productCandidate": { "barcode": "...", "name": "Mock Beans", "brand": "Sample", "category": "canned" }, "lastUsedExpiryTemplates": ["2025-06-01", "2025-12-01"] }`

### Stock registration

- `POST /api/stocks`
  - Request: `{ "barcode": "...", "name": "string", "category": "string", "quantity": 2, "unit": "can", "expiresOn": "2025-01-01" }`
  - Response (mock): combined stock item + lot object with generated IDs and timestamps.

### List stocks

- `GET /api/stocks?sort=expiresSoon`
  - Response (mock): array of stock items including the soonest expiring lot.

### Consume

- `POST /api/consume`
  - Request: `{ "stockItemId": 1, "quantity": 2 }`
  - Response (mock): `{ "consumedLots": [...], "remainingTotal": 3 }`

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
