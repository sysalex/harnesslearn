# Backend Multi-Module Refactor Design

**Date:** 2026-04-24
**Status:** approved by user direction to proceed without staged checkpoints
**Scope:** Refactor `backend` from a single Spring Boot module into a six-module Maven reactor aligned to the `bh-im-server` layering style.

## Goal

Refactor the backend into a real multi-module Maven project under `backend/`, using:

- `attendance-server-starter`
- `attendance-server-interfaces`
- `attendance-server-application`
- `attendance-server-domain`
- `attendance-server-infrastructure`
- `attendance-server-shared`

All Java packages move from `com.attendance...` to `com.attendance.server...`.

The refactor must preserve the current auth/login behavior, keep the current database mapping strategy, and prepare the Phase 1 backend for future feature growth without leaving half-migrated single-module remnants behind.

## Constraints

- Keep the repository root layout stable: the parent reactor remains at `backend/`.
- Follow the reference project at the module boundary level, not by copying its business model.
- Do not introduce new business features in this task.
- Current user changes already moved part of the code into layered packages inside the single module. Continue from that state rather than reverting it.
- The final result must remove the old single-module `src/main/java/com/attendance/...` layout from `backend`.

## Target Structure

```text
backend/
  pom.xml                                # reactor parent
  attendance-server-shared/
    pom.xml
    src/main/java/com/attendance/server/shared/...
  attendance-server-domain/
    pom.xml
    src/main/java/com/attendance/server/domain/...
  attendance-server-infrastructure/
    pom.xml
    src/main/java/com/attendance/server/infrastructure/...
  attendance-server-application/
    pom.xml
    src/main/java/com/attendance/server/application/...
  attendance-server-interfaces/
    pom.xml
    src/main/java/com/attendance/server/interfaces/...
  attendance-server-starter/
    pom.xml
    src/main/java/com/attendance/server/starter/...
    src/main/resources/application.yml
```

## Module Responsibilities

### `attendance-server-shared`

Holds shared cross-layer types that are not specific to one business module:

- API envelope
- business exception types
- shared error response support

This module stays lightweight and has no internal module dependencies.

### `attendance-server-domain`

Holds domain objects and pure domain-facing contracts:

- `BaseEntity`
- `User`
- domain service contracts such as `UserQueryService`

This module can depend on framework annotations used directly by entities, but it must not depend on web controllers or Spring Security config.

### `attendance-server-infrastructure`

Holds all implementation details and technical adapters:

- MyBatis-Plus config
- Spring Security config
- JWT provider and filter
- MyBatis mapper interfaces
- persistence-backed implementations of domain/application contracts

This module depends on `domain` and `shared`.

### `attendance-server-application`

Holds use-case orchestration:

- login application service
- future user profile/password application services

This module coordinates domain services and infrastructure-exposed technical services. For this refactor, it may depend on `infrastructure` pragmatically to keep the migration small and aligned with the reference project's dependency style.

### `attendance-server-interfaces`

Holds delivery adapters:

- REST controllers
- request/response DTOs
- global exception mapping for HTTP

This module depends on `application` and `shared`.

### `attendance-server-starter`

Holds only the runnable Spring Boot entry and runtime resources:

- `AttendanceServerApplication`
- `application.yml`

This module depends on `interfaces`, which pulls the rest transitively.

## Dependency Direction

The reactor dependency graph is:

```text
starter -> interfaces -> application -> infrastructure -> domain -> shared
```

`interfaces` also depends directly on `shared` for API envelope and HTTP exception mapping.

This is intentionally pragmatic rather than "clean architecture pure". It matches the user's request to align with the `bh-im-server` style and avoids over-designing the current auth-only codebase.

## Package Naming

All packages move to the `com.attendance.server` root:

- `com.attendance.server.starter`
- `com.attendance.server.interfaces.rest.auth`
- `com.attendance.server.application.auth`
- `com.attendance.server.domain.user.entity`
- `com.attendance.server.domain.user.service`
- `com.attendance.server.infrastructure.persistence.mapper`
- `com.attendance.server.infrastructure.security`
- `com.attendance.server.infrastructure.config`
- `com.attendance.server.shared.response`
- `com.attendance.server.shared.exception`

## Auth Flow After Refactor

The runtime login flow remains:

```text
AuthController
  -> AuthApplicationService
  -> UserQueryService / user access service
  -> JwtTokenProvider
  -> LoginResponse
```

No contract or behavior change is intended for:

- `POST /api/auth/login`
- JWT token generation and parsing
- exception semantics for `400/401/403`

## Testing Strategy

The refactor is driven by failing tests first:

1. Add a multi-module architecture test that asserts:
   - six child modules exist
   - parent `backend/pom.xml` is `packaging=pom`
   - starter module references the interfaces module
   - the new `com.attendance.server...` classes exist
2. Update existing unit and slice tests to new packages and module-local locations
3. Keep current auth/security/config tests green after migration
4. Run reactor-level `mvn test`
5. Run reactor-level `mvn checkstyle:check`

## Documentation Changes

The refactor must update:

- `docs/task-list.md`
- `CHANGELOG.md`
- `session-handoff.md`
- `.harness/learnings.md` if new reusable lessons appear
- `docs/README.md` if `docs/superpowers/...` becomes part of maintained navigation

## Risks

### Package rename fallout

Risk: imports, test slices, and Spring scanning may silently drift.

Mitigation: lock the new module/class layout with architecture tests before moving implementation.

### Cross-module dependency gaps

Risk: classes compile in the old single module but fail after the split.

Mitigation: create child POMs first and compile through the reactor, not module-by-module guessing.

### Dirty worktree merge risk

Risk: current local changes represent in-flight migration work.

Mitigation: only extend the current direction; do not revert moved files back to the old structure.

## Out of Scope

- implementing `profile/password` endpoints
- changing SQL schema
- changing API contracts
- introducing Docker changes
