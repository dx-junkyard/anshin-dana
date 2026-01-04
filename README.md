## Anshin Dana

Monorepo for a LINE LIFF-based household stock management assistant. This repository houses:

- **web/**: Next.js client optimized for LIFF with barcode scan-driven flows.
- **api/**: Spring Boot service for authentication, stock management, and scheduled notifications (worker profile).
- **infra/**: Development-time infrastructure (nginx reverse proxy, docker compose scaffolding).
- **docs/**: Architecture, API, and schema references.

### Getting started (development)

1. Copy `.env.example` to `.env` and fill required secrets (LINE channel credentials, JWT secret, etc.).
2. Build and start with Docker Compose:

```bash
docker compose up --build
```

The reverse proxy exposes:

- Next.js web at `http://localhost:8080`
- API proxied at `http://localhost:8080/api`

### Authentication flow

- `POST /api/auth/line` accepts a LINE ID token from LIFF and returns a server-issued JWT. Body example:

```json
{
  "idToken": "LINE_ID_TOKEN",
  "displayName": "optional display name",
  "pictureUrl": "https://example.com/avatar.png"
}
```

- All other `/api/**` endpoints require `Authorization: Bearer <server JWT>`. An invalid or missing token returns `401`.
- Example check without a real LINE token (expecting `401` due to invalid token):

```bash
curl -i -H "Authorization: Bearer invalid" http://localhost:8080/api/plan/emergency
```

LINE ID token signature verification is enforced on the backend; no mock or bypass mode is provided.

### Project goals

- Frictionless registration and daily-use flows (scan → suggest → minimal input).
- FEFO consumption (earliest expiry first).
- Digest-style LINE notifications to avoid noise.

See `docs/architecture.md` for more context.
