# Feature Flag System

Spring Boot feature-flag management and evaluation service.

## Management API

`POST /auth/register` creates the first organization administrator; `POST /auth/login` returns a JWT. Management endpoints require an administrator JWT and are restricted to the administrator's organization.

Feature flags are managed under environments:

- `POST /environments/{environmentId}/flags`
- `GET /environments/{environmentId}/flags`
- `GET|PUT|DELETE /flags/{flagId}`
- `POST /flags/{flagId}/releases`
- `GET /flags/{flagId}/releases`
- `POST /flags/{flagId}/releases/{versionId}/rollback`
- `POST /environments/{environmentId}/client-key/rotate`

Flags support typed default values, targeting rules, deterministic percentage rollouts, and weighted variants. A rule can select a variant by setting `variantKey`.

## Client evaluation API

Use the `clientKey` returned when an environment is created as the `X-Environment-Key` header:

```http
POST /client/environments/{environmentId}/flags/{key}/evaluate
X-Environment-Key: <client-key>
Content-Type: application/json

{"context":{"userId":"user-123","plan":"pro"}}
```

The bulk endpoint is `POST /client/environments/{environmentId}/flags/evaluate` with `{ "keys": [...], "context": {...} }`.

## Java SDK

`com.chalk.ffs.sdk.FeatureFlagClient` provides `evaluate`, `isEnabled`, `getBoolean`, `getString`, `getNumber`, and `getJson`, backed by a TTL cache. Register keys through `evaluate` and call `startAutoSync(...)` to refresh the cache in the background.

Database and JWT settings can be supplied through `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `JWT_EXPIRATION_MS` environment variables.
