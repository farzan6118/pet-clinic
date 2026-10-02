# Cursor rules for `pet-clinic`

These rules are tailored to this Spring Boot veterinary-clinic backend and should not be copied from or applied as
generic rules for unrelated services.

## Rule files

- `architecture.mdc` — feature packages, request flow, domain boundaries, and scheduling context
- `coding-conventions.mdc` — Java, Spring, DTO, and configuration practices
- `database-concurrency.mdc` — PostgreSQL transactions, interval conflicts, and selected pessimistic locks
- `external-integrations.mdc` — Keycloak HTTP and email integration boundaries
- `security-scope.mdc` — verified JWT/Keycloak setup and the current `permitAll` authorization state
- `service-repository.mdc` — Spring Data JPA, services, DTOs, mapping, and lifecycle conventions
- `testing.mdc` — existing JUnit/Mockito tests and Maven wrapper usage

All `.mdc` files use Cursor's `alwaysApply: true` metadata for cross-cutting project guidance. Keep each rule factual
and revise it when the repository's dependencies or architecture change.

## Source of truth

Use `README.md`, `PROJECT_CONTEXT.md`, `pom.xml`, controllers/DTOs, service/repository implementations, and tests as
project references. Source and tests are authoritative when prose differs; verify potentially stale documentation before
relying on it.

The application is a single Spring Boot REST backend using PostgreSQL. It does not currently have unrelated template's
SQL Server, gRPC, Kafka, branch/business scope model, generic base-service hierarchy, or Testcontainers integration-test
setup. Do not add those assumptions to future rules without an actual project change.
