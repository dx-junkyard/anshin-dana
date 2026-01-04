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
- PostgreSQL is started in the same Compose stack. The API applies Flyway migrations automatically on boot using `DATABASE_URL`, `DATABASE_USER`, and `DATABASE_PASSWORD` (all included in `.env.example`).

### Database migrations

- Schema changes are managed by Flyway in `api/src/main/resources/db/migration`. Running the API (or worker) will auto-apply pending migrations at startup.
- To run migrations manually for local development, from `api/` execute:

```bash
./gradlew flywayMigrate \
  -Dspring.datasource.url=$DATABASE_URL \
  -Dspring.datasource.username=$DATABASE_USER \
  -Dspring.datasource.password=$DATABASE_PASSWORD
```

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
