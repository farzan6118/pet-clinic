# Veterinary Clinic — Project Context

Last reviewed: October 2, 2026

This file is the working context for future development sessions. Read it before making project-wide assumptions or starting a new task.

## 1. What this project is

`pet-building` is a backend-first veterinary building management system. It is intended to manage building operations
such as:

- Pet owners and their pets
- Pet species and related information
- Veterinarians and their profiles
- Veterinarian availability
- Clinic rooms and room types
- Veterinary visits and appointments
- Visit scheduling, rescheduling, cancellation, and completion
- Authentication and user identity through Keycloak
- Email notifications for visit events
- Clinic administration and future reporting/dashboard functionality

The project is currently focused on the Spring Boot backend. A React administration panel is planned as a later phase.

## 2. Current project maturity

The project has a serious domain-oriented backend foundation and is more than a simple CRUD application. It includes scheduling rules, resource conflict checks, locking, validation, authentication infrastructure, email templates, and pagination/search support.

It is still active development and should not yet be considered production-ready.

Important current state observed on October 2, 2026:

- Check `git status` before editing because user changes may already be in progress.
- `mvn -q -DskipTests compile` passes after converting `Visit` to embed `DateTimeRange` and updating repository queries.
- `mvn -q test` passes after the test fixtures were updated to access `DateTimeRange` through the embedded `timeRange` property. The Spring context test connects to local PostgreSQL.
- The previously stale visit-service test has been replaced with a focused four-test suite for `VisitServiceCommandImpl`.
- Security configuration currently permits all requests and therefore bypasses real endpoint protection.
- A Keycloak client secret is currently stored directly in `application-home.yaml` and should be externalized.
- Database schema management currently uses Hibernate `ddl-auto: update`; Flyway configuration is commented out.

## 3. Technology stack

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring MVC / WebMVC
- Spring Data JPA
- Hibernate ORM
- PostgreSQL
- QueryDSL 5.1.0 for dynamic queries
- Spring Security
- OAuth2 Resource Server with JWT
- Keycloak 26.7.2 for identity and authentication
- Spring Data Redis
- Spring Mail
- Thymeleaf for email templates
- Springdoc OpenAPI 3.1.0
- Lombok
- Maven
- Docker Compose for local infrastructure
- JUnit 5, Mockito, and Spring Boot Test
- Datafaker for test data

### Planned frontend

- React.js
- JavaScript or TypeScript
- Separate administration panel consuming the REST API

## 4. Project structure

The main Java package is:

```text
com.github.farzan6118
```

The code is organized mainly by domain feature:

```text
auth/           Authentication endpoints and identity DTOs
common/         Shared exceptions, enums, persistence, pagination, mappers
config/         Spring, security, OpenAPI, Redis/client, and building configuration
infrastructure/ External integrations such as Keycloak and email
owner/          Owner entity, DTOs, mapper, repository, service, controller
pet/            Pet and species entities, DTOs, mappers, repositories, services, controllers
building/         Clinics, rooms, and room types
vet/            Veterinarians, profiles, and availability
appointment/    Visits, duration templates, scheduling, searching, and notifications
```

Resources include:

```text
src/main/resources/application.yaml
src/main/resources/application-home.yaml
src/main/resources/application-company.yaml
src/main/resources/templates/email/...
src/main/resources/docker/mailpit-docker-compose.yml
```

## 5. Domain model

Shared person data lives in `person/`. `Person` stores title, first name, last name, and national ID, and has required
one-to-one associations to `Contact` and `Address`. `Contact` stores email, mobile number, birth date, and photo.
`Address` stores structured location details and optional coordinates. `Owner` and `Vet` each refer to a `Person`; vet
availability remains on `Vet`. Owner and vet create/update requests carry nested `person`, `contact`, and `address`
objects, and responses expose the corresponding mapped DTOs. Contact lookups and uniqueness checks therefore follow the
`person.contact` relationship.

The central relationship is:

```text
Owner
  └── Pet
        └── Visit
              ├── Vet
              └── Room
```

Important entities include:

- `Owner`: association to a shared `Person`, responsible for one or more pets
- `Pet`: animal belonging to an owner and participating in visits
- `Species`: pet species/type data
- `Vet`: association to a shared `Person` with veterinarian availability
- `Contact`: shared contact details, birth date, and photo for a person
- `Address`: structured address and optional geolocation associated with a person
- `Building`: building location with a required address
- `Room`: building room associated with a building and room type
- `VetAvailability`: a veterinarian's available time interval, embedded as `DateTimeRange`
- `RoomType`: classification of building rooms
- `Visit`: appointment connecting a pet, veterinarian, embedded `DateTimeRange`, visit type, and optionally a room
- `DateTimeRange`: JPA embeddable storing required start/end timestamps and providing duration, date/time, validity, same-day, and overlap helpers; used by both `Visit` and `VetAvailability`
- `DurationTemplate`: reusable duration configuration, including the `STANDARD` duration used by visit booking
- `MedicalRecord`: clinical record authored by a veterinarian for a pet and associated with a visit; currently connected to persistence and visit completion, but not yet exposed through a medical-record read API

Common enums include:

- `VisitStatus`
- `VisitType`
- `VisitCategory`
- `AppointmentType`
- `AppointmentDuration`
- `SlotStatus`
- `EntityStatus`
- `Sex`

## 6. Visit scheduling behavior

`VisitServiceCommandImpl` contains visit mutations and is currently the most important business service. `VisitServiceQueryImpl` contains visit reads and search operations.

When booking a visit, the service generally:

1. Reads requested duration in minutes (default 15), rounds it up to the configured availability block size (default 5 minutes), and builds start/end timestamps.
3. Validates that the time range is valid and remains on one day.
4. Checks building working hours and closed days.
5. Locks the selected Room when present, then the veterinarian and Pet in a consistent order.
6. Uses the locked Pet row.
7. Requires the supplied Room UUID for ONSITE visits and rejects a Room UUID for ONLINE or OFFSITE visits.
8. Checks veterinarian availability.
9. Checks veterinarian reservation conflicts.
10. Checks room reservation conflicts.
11. Checks pet reservation conflicts.
12. Saves the visit in a transaction.
13. Sends visit notifications by email.

Rescheduling locks the current Visit, then the selected Room when present, Vet, and Pet; it repeats
availability/conflict checks while excluding the current visit. It preserves the existing duration and rounds it using
`building.availability.time-block-minutes`.

Visit completion validates the current status and ensures the visit has started but has not already ended.

Visit cancellation is idempotent for already-cancelled visits, but completed visits cannot be cancelled.

Medical records are represented by `pet/model/MedicalRecord.java`. A record links a `Pet`, `Visit`, and `Vet`, and supports consultation, prescription, vaccination, surgery, follow-up, and other record types. It stores diagnosis, clinical notes, treatment plan, prescription text, follow-up information, vaccination details, and surgery details.

The pet package's medical-record feature currently contains:

- `MedicalRecordRepository` for persistence
- `MedicalRecordService` and `MedicalRecordServiceImpl` for creation
- `MedicalRecordMapper` for both request-to-entity and entity-to-response mapping
- `MedicalRecordResponseDto` for the future read API

The visit completion workflow accepts medical-record fields through `CompleteVisitRequestDto`. `VisitServiceCommandImpl.completeVisit(...)` completes the visit and delegates medical-record creation to `MedicalRecordService` in the same outer transaction. The endpoint is intended to be used by the attending vet or an operator entering the vet's clinical result. Authorization rules for those roles are not yet enforced because the current security configuration permits all requests.

The future API should expose a pet's medical-record history to authorized operators and veterinarians. That API has not been added yet.

## 7. API and application behavior

The API is organized under `/api`. Daily Room availability is served at
`/api/rooms/{uuid}/availability?date=YYYY-MM-DD`; daily Vet availability is served at
`/api/vets/{uuid}/availability?date=YYYY-MM-DD`. Room blocks are bounded by building working hours, while Vet blocks are
bounded by active Vet availability intervals. `building.availability.time-block-minutes` controls the block size
(default 5 minutes); returned timelines include only blocks inside those windows.

Clinic administration uses `/api/buildings`; room requests refer to a building and room type by UUID. Room types,
species, pets, vets, and buildings use paginated list responses. CRUD writes validate duplicate natural keys before
saving and use soft deletion through `EntityStatus`.

The visit controller currently exposes endpoints for:

- Booking a visit: `POST /api/visits`
- Rescheduling: `PUT /api/visits/{uuid}`
- Listing visits: `GET /api/visits/page`
- Getting one visit: `GET /api/visits/{uuid}`
- Cancelling: `DELETE /api/visits/{uuid}`
- Advanced search: `GET /api/visits/search`
- Completing: `PATCH /api/visits/{uuid}/complete`

DTOs are used for request and response boundaries rather than exposing entities directly.

Pagination is handled with shared request/response DTOs and `PageMapper`.

Validation and application errors are centralized through custom exceptions and `GlobalExceptionHandler`.

## 8. Configuration and local infrastructure

Profiles currently include:

- `home`, active by default through Maven
- `company`

The home contact currently defaults to:

- PostgreSQL on `localhost:5432`
- Database name `pet_clinic`
- Database user `user`
- Database password `pass`
- Keycloak on `localhost:8180`
- Mailpit SMTP on `localhost:1025`
- Redis on `localhost:6379`
- Application port `8010`

The Docker Compose resource currently defines Mailpit only. PostgreSQL, Redis, and Keycloak are expected to be available separately in the local environment.

Email templates exist for owner and veterinarian notifications for:

- Visit scheduled
- Visit rescheduled
- Visit cancelled
- Checkup reminders for owners

## 9. Security status and required caution

The project has the infrastructure for stateless JWT-based security:

- Spring Security
- OAuth2 Resource Server
- JWT decoder configuration through issuer URI
- Keycloak integration
- Method-security support enabled with `@EnableMethodSecurity`

However, `WebSecurityConfig` currently contains:

```java
.anyRequest().permitAll()
```

This means all endpoints are currently accessible without authentication. The commented `.anyRequest().authenticated()` indicates that the security setup is unfinished or intentionally relaxed for development.

Before deployment, endpoint access rules and method-level authorization must be implemented and tested. Do not assume that the presence of Keycloak configuration means the API is secured.

Also treat the Keycloak client secret in `application-home.yaml` as sensitive. It should be moved to environment variables or an ignored local configuration source.

## 10. Testing status

Visit-focused tests include the command-service unit test and an HTTP/database integration test. The focused unit test is:

```text
src/test/java/com/github/farzan6118/visit/service/VisitServiceImplTest.java
```

The test suite covers useful scenarios such as:

- Onsite booking
- Online and offsite visits without rooms
- Emergency room selection
- Missing pet/vet cases
- Availability conflicts
- Rescheduling
- Cancellation
- Completion
- Pagination

The focused test suite covers success paths for booking, rescheduling, cancellation, and completion with medical-record creation. It uses `VisitServiceCommandImpl` and mocks its current dependencies. The suite includes `appointment/controller/VisitIntegrationTest`, which exercises booking and rescheduling through MockMvc and verifies persisted rounded time ranges. Spring context/integration tests require the configured local PostgreSQL service.

When changing visit logic, update the focused unit tests first and add integration tests for database locking and overlapping reservations.

Medical-record implementation notes:

- `MedicalRecordMapper.toEntity(...)` currently receives `CompleteVisitRequestDto`, which keeps the completion payload tied to the visit feature. This is acceptable for the current workflow, but a dedicated medical-record creation request DTO may be cleaner when the medical API expands.
- `MedicalRecordServiceImpl` is class-level `@Transactional(readOnly = true)` and its `create(...)` method currently has no method-level write transaction override. This should be corrected before relying on the service independently.
- `VisitServiceCommandImpl` delegates record creation to `MedicalRecordService`; response mapping is prepared for the future medical-record read API.

## 11. Highest-priority technical improvements

Recommended order:

1. Keep `VisitServiceCommandImplTest` focused and green as visit behavior evolves.
2. Restore authentication and define explicit role/permission rules.
3. Remove committed secrets from application configuration.
4. Add Flyway or Liquibase migrations and stop relying on `ddl-auto: update` for deployed environments.
5. Add integration tests for concurrent booking and database lock behavior.
6. Inject a `Clock` instead of calling `LocalDateTime.now()` directly to make time-dependent logic deterministic.
7. Review API status codes and parameter annotations, especially rescheduling and cancellation.
8. Add authorization tests for owners, vets, administrators, and building staff.
9. Add observability and operational documentation before production deployment.
10. Build the React administration panel after the API contract stabilizes.

## 12. Review guidance for future tasks

Before making changes, inspect:

1. `README.md` and the relevant `.cursor/rules/` files
2. `README.md`
3. Relevant domain package
4. Its service, controller, repository, DTOs, mapper, and tests
5. `WebSecurityConfig` if the task touches access control
6. Profile configuration if the task touches infrastructure or external services

Preserve the existing feature-package structure and DTO boundary. Prefer small, coherent changes. Do not assume that a configured integration is active until its runtime configuration and tests confirm it.

For scheduling changes, always consider all four conflict dimensions:

- Veterinarian availability
- Veterinarian reservation overlap
- Pet reservation overlap
- Room reservation overlap

For production-facing changes, also consider transaction boundaries, concurrent requests, timezone behavior, notification failures, authorization, and migration safety.

## 13. Overall assessment

The project has a good architectural direction and a realistic veterinary-building domain. Its strongest area is the
business-oriented appointment model, which already accounts for real operational constraints instead of treating visits
as simple records.

The main weakness is project maturity around reliability: tests have fallen behind implementation changes, security is currently bypassed, and environment secrets/schema management need hardening.

The project is a promising, well-structured backend in active development. The next phase should focus on making the existing foundation trustworthy before adding large new features.
