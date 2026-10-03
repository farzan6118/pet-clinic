# Veterinary Clinic

A backend service for managing veterinary building operations. The application exposes a Spring REST API for owners,
pets, veterinarians, building rooms, veterinarian availability, visits, and visit duration templates. It is built with
Java 21, Spring Boot, PostgreSQL, and Maven.

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

- Manage owners and their contact details, pets, species, veterinarians, buildings, rooms, and room types.
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
- PostgreSQL (the `home` contact expects database `pet_clinic` on `localhost:5432`)
- Maven, or the included Maven Wrapper (`mvnw` / `mvnw.cmd`)
- Redis and Keycloak for their related runtime features
- Mailpit or another SMTP server for local email delivery (the included compose resource starts Mailpit)

The application uses Spring Boot Docker Compose integration for `src/main/resources/docker/mailpit-docker-compose.yml`. That compose file provides Mailpit; it does not provision PostgreSQL, Redis, or Keycloak.

## Local setup

1. Create a PostgreSQL database named `pet_clinic` and configure the contact's database connection.
2. Start any required local services. Configure Keycloak at the issuer URL and realm expected by the selected contact;
   start Redis if using cache-backed features. Mailpit is configured through the application's Docker Compose
   integration.
3. Set contact-specific values and secrets through environment variables or a local, untracked configuration file. Never
   commit credentials.

The `home` and `company` profiles accept `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KEYCLOAK_SERVER_URL`,
`KEYCLOAK_REALM`, `KEYCLOAK_CLIENT_ID`, and `KEYCLOAK_CLIENT_SECRET`. Database connection values retain local
development defaults; set `KEYCLOAK_CLIENT_SECRET` when using Keycloak login because no client secret is stored in the
contact files.

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

The application listens on port `8010` by default. Maven activates the `home` contact by default. To select `company`,
use `./mvnw -Pcompany spring-boot:run` (or `mvnw.cmd -Pcompany spring-boot:run` on Windows).

Swagger UI is available at `/swagger-ui/index.html`; the OpenAPI document is at `/v3/api-docs` when the application is running.

### Seed data

`common.persistence.FillInitialRecords` inserts initial duration templates, species, a building, room types, owners,
veterinarians, pets, rooms, and future veterinarian availability when the corresponding repositories are empty.
Availability is generated relative to the current date. The seed data is intended for development and demonstration, not
production provisioning.

## Profiles and configuration

Configuration is in:

```text
src/main/resources/application.yaml          shared settings and default port
src/main/resources/application-home.yaml     local development settings
src/main/resources/application-company.yaml  company environment settings
```

The profiles configure PostgreSQL, Keycloak issuer/client settings, Redis, SMTP, building hours, and API docs. Review
the files and replace local example values with environment-specific settings before running the application. The
current contact YAML contains inline database and Keycloak credentials; treat them as development-only and move secrets
to environment variables or an external secret store.

Hibernate currently uses `ddl-auto: update`. Flyway settings are commented out and there are no active schema migration scripts. This is convenient for development but does not provide a reviewed, repeatable production migration process.

## API overview

All API routes use the `/api` prefix. Request and response bodies use DTOs; persistence entities are not intended to be exposed directly.

| Resource                  | Base route                           | Main operations                                                                       |
|---------------------------|--------------------------------------|---------------------------------------------------------------------------------------|
| Authentication            | `/api/auth/login`                    | Login                                                                                 |
| User identity             | `/api/user`                          | Current user (`/me`)                                                                  |
| Owners                    | `/api/owners`                        | Create, update, soft-delete, fetch, paginate, list an owner's pets                    |
| Pets                      | `/api/pets`                          | Create, update, soft-delete, fetch, paginate                                          |
| Species                   | `/api/species`                       | Create, update, soft-delete, fetch, paginate, list                                    |
| Veterinarians             | `/api/vets`                          | Create, update, soft-delete, fetch, paginate, list                                    |
| Veterinarian availability | `/api/vets/{vetUuid}/availabilities` | Create, update, soft-delete, paginate                                                 |
| Clinics                   | `/api/buildings`                     | Create, update, soft-delete, fetch, paginate, list                                    |
| Rooms                     | `/api/rooms`                         | Create, update, soft-delete, fetch, paginate, list                                    |
| Room types                | `/api/room-types`                    | Create, update, soft-delete, fetch, paginate, list                                    |
| Visits                    | `/api/visits`                        | Book, reschedule, cancel, complete, fetch, paginate, advanced search, available slots |
| Duration templates        | `/api/duration-templates`            | Create, update, soft-delete, fetch, paginate, list                                    |

Visit routes include `POST /api/visits`, `PUT /api/visits/{uuid}`, `DELETE /api/visits/{uuid}`, `PATCH /api/visits/{uuid}/complete`, `GET /api/visits/{uuid}`, `GET /api/visits/page`, and `GET /api/visits/search`. Consult Swagger UI or controller DTOs for request fields, response shapes, validation rules, and query parameters.

`GET /api/buildings/availability` returns the weekly schedule from Monday through Sunday. Each entry has `dayOfWeek`,
`available`, `openingTime`, and `closingTime`. Closed days have null times. It uses
`building.availability.working-hours`
and `building.availability.close-days` from the active application YAML contact.

`GET /api/rooms/{roomUuid}/availability?date=YYYY-MM-DD` returns the Room's time blocks within building working hours
for that date. `GET /api/vets/{vetUuid}/availability?date=YYYY-MM-DD` returns blocks within the Vet's active availability
intervals for that date. Both use `building.availability.time-block-minutes` (5 minutes by default), include booked
blocks
with their `visitUuid`, and use strict interval overlap. These responses cover each applicable availability window;
they do not pad the timeline with blocks outside that window. The response status is `AVAILABLE` or `BOOKED` for
returned blocks.

`GET /api/visits/available-slots` accepts `vetUuid`, `petUuid`, `date` (`YYYY-MM-DD`), `visitType`, `durationMinutes`
(default `15`), and `intervalMinutes` (default `15`). It returns candidate start/end date-times and a room UUID for
applicable onsite slots, excluding slots blocked by building hours, veterinarian/pet reservations, or room reservations.
Booking accepts a duration in minutes (default `15`) and rounds it up to a multiple of the configured availability block
size. Rescheduling preserves the current visit duration, applying the same block rounding.

`GET /api/visits/available-slots/range` accepts the same parameters with `dateFrom` and `dateTo` (inclusive, at most 31
days) instead of `date`, returning all available slots in chronological order.

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
- `Visit` scheduling and rescheduling require a positive, same-day interval. Booking requires a Room for `ONSITE` visits and rejects a Room for `ONLINE` and `OFFSITE` visits. Completion changes the end timestamp and status.
- `VisitServiceCommandImpl` checks building opening hours and closed days, veterinarian availability, and overlapping
  veterinarian, pet, and room reservations. Booking locks a selected Room, the Vet, and the Pet; rescheduling locks the
  Visit, selected Room, Vet, and Pet. Preserve transaction and lock ordering when changing these operations.
- Daily availability uses `ClinicAvailabilityService` for Rooms and active `VetAvailability` intervals for Vets, plus QueryDSL overlap queries for Visits. The configured block size also determines rounded booking/rescheduling duration.
- `FillInitialRecords` is a `CommandLineRunner` and only seeds each data group when its repository is empty.
- Common entity persistence includes UUIDs, status/soft-delete behavior, and audit timestamps. Some entities also use Hibernate Envers.
- Medical-record creation is connected to visit completion. A standalone medical-record history API is not currently exposed.

## Tests

Run the full test suite with `./mvnw test` or `mvnw.cmd test`. The suite includes unit tests using JUnit 5 and Mockito,
a Spring context test, and `VisitIntegrationTest`, which exercises HTTP booking/rescheduling and persistence using the
configured local PostgreSQL database. PostgreSQL and suitable contact configuration must be available. Concurrent
booking against a real database still merits dedicated integration coverage.

## Repository layout

```text
src/main/java/com/github/farzan6118/
  appointment/      visits, scheduling, duration templates, search
  auth/             login and current-user endpoints
  building/           buildings, rooms, room types
  common/            shared DTOs, enums, exceptions, persistence, value objects
  config/             application, security, cache, OpenAPI configuration
  infrastructure/     Keycloak and email integrations
  owner/              owner domain and API
  person/             shared person, contact, and address data
  pet/                pets, species, medical records
  vet/                veterinarians and availability
src/main/resources/
  application*.yaml   contact configuration
  templates/email/    notification templates
  docker/             local Mailpit compose resource
src/test/java/         unit and Spring context tests
```

## Current limitations

- **Security is not enforced globally.** `WebSecurityConfig` permits all requests. The OAuth2/JWT and Keycloak integrations do not mean API routes are protected; implement and test endpoint authorization before deployment.
- **Secrets are present in contact configuration.** Replace them with environment-provided secrets and rotate any
  credentials that have been shared outside the intended local environment.
- **Schema updates are not versioned.** Hibernate `update` is enabled and Flyway is not active; add and review migrations before production use.
- **Frontend is not included.** This repository contains the backend service only.
- **Development seed data uses sample identities and addresses.** It is not production or customer data.

For detailed implementation notes and change guidance, see [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) and the relevant `.cursor/rules/` files. Verify both against current source when they describe behavior that may have changed.
