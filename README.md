# Veterinary Clinic

A backend service for managing veterinary clinic operations. The application exposes a Spring REST API for owners, pets, veterinarians, clinic rooms, veterinarian availability, visits, and visit duration templates. It is built with Java 21, Spring Boot, PostgreSQL, and Maven.

> **Status:** Active development. The React administration interface is planned and is not part of this repository. Review the security and configuration notes below before exposing the service beyond a trusted development environment.

## Contents

- [Capabilities](#capabilities)
- [Architecture](#architecture)
- [Technology](#technology)
- [Requirements](#requirements)
- [Local setup](#local-setup)
- [Profiles and configuration](#profiles-and-configuration)
- [API overview](#api-overview)
- [Domain and persistence](#domain-and-persistence)
- [Tests](#tests)
- [Repository layout](#repository-layout)
- [Current limitations](#current-limitations)

## Capabilities

- Manage owners and their contact details, pets, species, veterinarians, clinics, rooms, and room types.
- Manage veterinarian availability and visit duration templates.
- Book, search, reschedule, cancel, and complete visits.
- Prevent booking conflicts across veterinarian availability, veterinarian reservations, pet reservations, and room reservations.
- Create a medical record as part of completing a visit.
- Send visit notifications using Thymeleaf email templates and Spring Mail.
- Provide pagination, validation, soft deletion, audit metadata, and selected Hibernate Envers history.
- Integrate with Keycloak for login and identity operations, Redis for caching infrastructure, and PostgreSQL for persistence.

## Architecture

The code uses a feature-oriented package structure. Controllers define HTTP boundaries and DTOs; services hold application workflows; repositories handle persistence; mappers translate between DTOs and entities. Shared concerns such as exceptions, pagination, auditing, configuration, and value objects live under `common/` and `config/`.

```text
Client (Swagger UI or another HTTP client)
                  |
                  v
       Spring MVC REST controllers
                  |
                  v
        Application services
          /             \
         v               v
   Domain entities    Integrations
         |          Keycloak / email
         v          Redis / cache
 Spring Data JPA
         |
         v
     PostgreSQL
```

The central scheduling flow lives in `appointment/`. `Visit` and `VetAvailability` both store their interval as the embedded `common.valueobject.DateTimeRange`. Visit APIs continue to accept and return explicit start and end date-times; the embedded object is an internal persistence/domain representation.

## Technology

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.1.1, Spring MVC |
| Persistence | Spring Data JPA, Hibernate 7, PostgreSQL |
| Dynamic queries | QueryDSL 5.1.0 (Jakarta) |
| Security and identity | Spring Security, OAuth2 Resource Server/JWT, Keycloak 26.7.2 |
| Cache and supporting services | Spring Cache, Redis, Caffeine |
| Email | Spring Mail, Thymeleaf |
| API documentation | Springdoc OpenAPI 3.1.0 / Swagger UI |
| Build and tests | Maven, JUnit 5, Mockito, Spring Boot Test |

## Requirements

- JDK 21
- PostgreSQL (the `home` profile expects database `pet_clinic` on `localhost:5432`)
- Maven, or the included Maven Wrapper (`mvnw` / `mvnw.cmd`)
- Redis and Keycloak for their related runtime features
- Mailpit or another SMTP server for local email delivery (the included compose resource starts Mailpit)

The application uses Spring Boot Docker Compose integration for `src/main/resources/docker/mailpit-docker-compose.yml`. That compose file provides Mailpit; it does not provision PostgreSQL, Redis, or Keycloak.

## Local setup

1. Create a PostgreSQL database named `pet_clinic` and configure the profile's database connection.
2. Start any required local services. Configure Keycloak at the issuer URL and realm expected by the selected profile; start Redis if using cache-backed features. Mailpit is configured through the application's Docker Compose integration.
3. Set profile-specific values and secrets through environment variables or a local, untracked configuration file. Never commit credentials.
4. From the repository root, compile and test:

   ```bash
   ./mvnw test
   ```

   On Windows PowerShell:

   ```powershell
   .\mvnw.cmd test
   ```

5. Start the service:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows PowerShell:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The application listens on port `8010` by default. Maven activates the `home` profile by default. To select `company`, use `./mvnw -Pcompany spring-boot:run` (or `mvnw.cmd -Pcompany spring-boot:run` on Windows).

Swagger UI is available at `/swagger-ui/index.html`; the OpenAPI document is at `/v3/api-docs` when the application is running.

### Seed data

`common.persistence.FillInitialRecords` inserts initial duration templates, species, a clinic, room types, owners, veterinarians, pets, rooms, and future veterinarian availability when the corresponding repositories are empty. Availability is generated relative to the current date. The seed data is intended for development and demonstration, not production provisioning.

## Profiles and configuration

Configuration is in:

```text
src/main/resources/application.yaml          shared settings and default port
src/main/resources/application-home.yaml     local development settings
src/main/resources/application-company.yaml  company environment settings
```

The profiles configure PostgreSQL, Keycloak issuer/client settings, Redis, SMTP, clinic hours, and API docs. Review the files and replace local example values with environment-specific settings before running the application. The current profile YAML contains inline database and Keycloak credentials; treat them as development-only and move secrets to environment variables or an external secret store.

Hibernate currently uses `ddl-auto: update`. Flyway settings are commented out and there are no active schema migration scripts. This is convenient for development but does not provide a reviewed, repeatable production migration process.

## API overview

All API routes use the `/api` prefix. Request and response bodies use DTOs; persistence entities are not intended to be exposed directly.

| Resource | Base route | Main operations |
| --- | --- | --- |
| Authentication | `/api/auth/login` | Login |
| User identity | `/api/user` | Current user (`/me`) |
| Owners | `/api/owners` | Create, update, soft-delete, fetch, paginate, list an owner's pets |
| Pets | `/api/pets` | Create, update, soft-delete, fetch, paginate |
| Species | `/api/species` | Create, update, soft-delete, fetch, paginate, list |
| Veterinarians | `/api/vets` | Create, update, soft-delete, fetch, paginate, list |
| Veterinarian availability | `/api/vets/{vetUuid}/availabilities` | Create, update, soft-delete, paginate |
| Clinics | `/api/clinics` | Create, update, soft-delete, fetch, paginate, list |
| Rooms | `/api/rooms` | Create, update, soft-delete, fetch, paginate, list |
| Room types | `/api/room-types` | Create, update, soft-delete, fetch, paginate, list |
| Visits | `/api/visits` | Book, reschedule, cancel, complete, fetch, paginate, advanced search |
| Duration templates | `/api/duration-templates` | Create, update, soft-delete, fetch, paginate, list |

Visit routes include `POST /api/visits`, `PUT /api/visits/{uuid}`, `DELETE /api/visits/{uuid}`, `PATCH /api/visits/{uuid}/complete`, `GET /api/visits/{uuid}`, `GET /api/visits/page`, and `GET /api/visits/search`. Consult Swagger UI or controller DTOs for request fields, response shapes, validation rules, and query parameters.

## Domain and persistence

```text
Owner ──< Pet ──< Visit >── Vet ──< VetAvailability
                         |
                         └── optional Room ── Clinic
                                         └── RoomType

Visit ── optional MedicalRecord
Owner / Vet ── Person ── Profile
                       └── Address
```

- `DateTimeRange` is a JPA embeddable with required start and end timestamps, duration/date/time helpers, validity checks, same-day checks, and overlap checks.
- `Visit` scheduling and rescheduling require a positive, same-day interval. Completion changes the end timestamp and status.
- `VisitServiceCommandImpl` checks clinic opening hours and closed days, veterinarian availability, and overlapping veterinarian, pet, and room reservations. It locks the veterinarian during booking and locks an existing visit during rescheduling/completion.
- `FillInitialRecords` is a `CommandLineRunner` and only seeds each data group when its repository is empty.
- Common entity persistence includes UUIDs, status/soft-delete behavior, and audit timestamps. Some entities also use Hibernate Envers.
- Medical-record creation is connected to visit completion. A standalone medical-record history API is not currently exposed.

## Tests

Run the full test suite with `./mvnw test` or `mvnw.cmd test`. The suite includes unit tests using JUnit 5 and Mockito plus Spring context tests. Spring context tests use the configured local PostgreSQL database, so PostgreSQL and suitable profile configuration must be available. Some integration behaviors, such as concurrent booking against a real database, need dedicated tests.

## Repository layout

```text
src/main/java/com/github/farzan6118/petclinic/
  appointment/      visits, scheduling, duration templates, search
  auth/             login and current-user endpoints
  clinic/           clinics, rooms, room types
  common/            shared DTOs, enums, exceptions, persistence, value objects
  config/             application, security, cache, OpenAPI configuration
  infrastructure/     Keycloak and email integrations
  owner/              owner domain and API
  person/             shared person, profile, and address data
  pet/                pets, species, medical records
  vet/                veterinarians and availability
src/main/resources/
  application*.yaml   profile configuration
  templates/email/    notification templates
  docker/             local Mailpit compose resource
src/test/java/         unit and Spring context tests
```

## Current limitations

- **Security is not enforced globally.** `WebSecurityConfig` permits all requests. The OAuth2/JWT and Keycloak integrations do not mean API routes are protected; implement and test endpoint authorization before deployment.
- **Secrets are present in profile configuration.** Replace them with environment-provided secrets and rotate any credentials that have been shared outside the intended local environment.
- **Schema updates are not versioned.** Hibernate `update` is enabled and Flyway is not active; add and review migrations before production use.
- **Frontend is not included.** This repository contains the backend service only.
- **Development seed data uses sample identities and addresses.** It is not production or customer data.

For deeper implementation notes and maintenance guidance, see [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md). That file is working context for development sessions; this README is the standalone project guide.
