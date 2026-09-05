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

## MCP server

The application also exposes an MCP Streamable HTTP server at `/mcp`. MCP clients must send the same administrator JWT used by the management API in an `Authorization: Bearer <token>` header. The endpoint is intentionally covered by the existing admin-only Spring Security rule.

Available tools include `list_feature_flags`, `get_feature_flag`, `evaluate_feature_flag`, `create_feature_flag`, `update_feature_flag`, `publish_feature_flag`, `list_feature_flag_releases`, and `rollback_feature_flag`. Tool actions reuse the existing tenant authorization and validation services, so an MCP client cannot bypass organization or project boundaries.

The direct MCP Java SDK integration uses the Jackson 2 adapter to remain compatible with this Spring Boot application. Configure an MCP client to connect to `http://localhost:8080/mcp` after starting the application and PostgreSQL.

Database and JWT settings can be supplied through `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `JWT_EXPIRATION_MS` environment variables.
