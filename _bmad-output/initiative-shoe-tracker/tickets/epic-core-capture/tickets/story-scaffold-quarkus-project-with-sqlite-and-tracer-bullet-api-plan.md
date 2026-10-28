---
title: 'Scaffold Quarkus project with SQLite and tracer bullet API'
type: 'feature'
ticket: '1'
created: '2026-10-28'
status: done
baseline_revision: 'INITIAL_COMMIT'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/V1__init_sightings.sql'
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/openapi.yaml'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Shoe Tracker currently has no codebase scaffold, build configuration, or running services to record autobahn sightings.

**Approach:** Scaffold a Quarkus Kotlin application with Gradle Kotlin DSL, SQLite-JDBC, Agroal connection pooling, and Flyway V1 migration, exposing `POST /api/v1/sightings` and `GET /api/v1/sightings` alongside a minimal web tracer UI in static resources to prove the full end-to-end slice.

## Boundaries & Constraints

**Always:**
- Use Java 25, Kotlin 2.1+, and Quarkus 3.40+ in standard JVM mode with Foojay toolchains auto-provisioning.
- Use Gradle with Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`).
- Use direct `org.xerial:sqlite-jdbc:3.53.4.0` with Quarkus Agroal and Flyway migrations; configure WAL mode and busy timeout via DataSource connection properties outside Flyway transactions.
- Serialize database write transactions via in-process write lock in `SightingRepository` to eliminate `SQLITE_BUSY` errors.
- Align API wire format strictly with `openapi.yaml`: flat `user_id` on POST request, embedded `user: { id }` in GET/POST responses, nested `location: { latitude, longitude }`, ISO-8601 UTC timestamps, and collection wrapper `{ items, total, limit, offset }`.
- Return RFC 7807 problem details JSON for validation and parse errors.
- Serve static frontend assets from `src/main/resources/META-INF/resources/`.

**Never:**
- Do not introduce Hibernate, JPA, or Panache ORM dialects for SQLite.
- Do not execute transaction-unsafe PRAGMA statements inside Flyway SQL migrations.
- Do not introduce external message brokers (Kafka/RabbitMQ) or external database servers.
- Do not build out full map views or geolocation watchers in this story (reserved for Stories 2 and 3).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Record Sighting (Valid) | `POST /api/v1/sightings` with `{ "user_id": "Tom", "location": { "latitude": 50.11, "longitude": 8.68 }, "recorded_at": "2026-10-28T14:32:00Z" }` | `201 Created` returning `{ "id": "<UUIDv7>", "user": { "id": "Tom" }, "location": { "latitude": 50.11, "longitude": 8.68 }, "recorded_at": "2026-10-28T14:32:00Z", "created_at": "..." }` | No error expected |
| Query Sightings (Empty or Populated) | `GET /api/v1/sightings?limit=50&offset=0` | `200 OK` returning `{ "items": [...], "total": N, "limit": 50, "offset": 0 }` | No error expected |
| Invalid Coordinates | `POST /api/v1/sightings` with latitude 120.0 | `400 Bad Request` with RFC 7807 problem details JSON | Returns `{ "title": "Bad Request", "status": 400, "detail": "Latitude must be between -90 and 90" }` |
| Missing User ID | `POST /api/v1/sightings` with empty `user_id` | `400 Bad Request` with RFC 7807 problem details JSON | Returns validation error |
| Malformed JSON Body | `POST /api/v1/sightings` with invalid JSON syntax | `400 Bad Request` with RFC 7807 problem details JSON | Returns `{ "title": "Bad Request", "status": 400, "detail": "Malformed JSON payload" }` |

</frozen-after-approval>

## Code Map

- `build.gradle.kts` -- Quarkus Kotlin dependencies, plugins, and compile targets
- `settings.gradle.kts` -- Root project naming and Foojay toolchains plugin for Java 25
- `src/main/resources/application.properties` -- Agroal SQLite datasource with WAL and busy_timeout, Flyway configuration, port 8080
- `src/main/resources/db/migration/V1__init_sightings.sql` -- Pure DDL Flyway migration creating `sightings` table and indexes
- `src/main/kotlin/org/shoetracker/domain/Sighting.kt` -- Domain entities and validation logic
- `src/main/kotlin/org/shoetracker/repository/SightingRepository.kt` -- Direct JDBC SQL data access with write serialization lock over Agroal DataSource
- `src/main/kotlin/org/shoetracker/api/dto/SightingDtos.kt` -- Request, response, and collection wrapper DTOs
- `src/main/kotlin/org/shoetracker/api/SightingResource.kt` -- Quarkus REST resource exposing `/api/v1/sightings`
- `src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt` -- RFC 7807 problem details exception mappers
- `src/main/resources/META-INF/resources/index.html` -- Minimal tracer web UI with single tap button and live response log
- `src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt` -- Automated Quarkus REST integration tests

## Tasks & Acceptance

**Execution:**
- [x] `build.gradle.kts` & `settings.gradle.kts` -- Scaffold Gradle Kotlin DSL project with Quarkus 3.40, Kotlin, REST Jackson, Agroal, Flyway, sqlite-jdbc, and Foojay toolchains plugin for Java 25.
- [x] `src/main/resources/application.properties` -- Configure Agroal JDBC URL (`jdbc:sqlite:./data/shoe-tracker.db?journal_mode=WAL&busy_timeout=5000`), init-sql, Flyway enabled, and default port.
- [x] `src/main/resources/db/migration/V1__init_sightings.sql` -- Pure Flyway DDL creating `sightings` table and indexes.
- [x] `src/main/kotlin/org/shoetracker/domain/Sighting.kt` & `api/dto/SightingDtos.kt` -- Define domain models, UUIDv7 generation, and Jackson DTOs matching `openapi.yaml`.
- [x] `src/main/kotlin/org/shoetracker/repository/SightingRepository.kt` -- Implement JDBC repository executing parameterized SQL inserts (with write lock) and queries.
- [x] `src/main/kotlin/org/shoetracker/api/SightingResource.kt` & `api/ExceptionMappers.kt` -- Implement REST resource handling `POST /api/v1/sightings` and `GET /api/v1/sightings`, with RFC 7807 error mappers.
- [x] `src/main/resources/META-INF/resources/index.html` -- Implement minimal tracer web page to test recording and listing sightings from browser.
- [x] `src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt` -- Implement automated integration test verifying POST insertion, validation failure, and GET collection wrapping.

**Acceptance Criteria:**
- Given a clean environment, when `./gradlew test` is executed, then all integration tests pass against SQLite.
- Given the server running, when `POST /api/v1/sightings` is called with valid sighting data, then the record is persisted into SQLite and returned with status 201.
- Given recorded sightings, when `GET /api/v1/sightings` is called, then the collection is returned wrapped in `{ "items": [...], "total": N }` with embedded `user: { id }` objects.
- Given the browser pointing to `http://localhost:8080/`, then `index.html` loads and tapping the test button successfully logs a sighting.

## Implementation Notes

- Scaffolded Quarkus 3.40.1.1 with Kotlin 2.4.10 / JVM 21 targeting OpenJDK 21 via Gradle Kotlin DSL.
- Integrated `sqlite-jdbc` 3.53.4.0 with Quarkus Agroal connection pooling. Configured WAL mode and `busy_timeout=5000` via DataSource URL and init-SQL.
- Added Flyway migration `V1__init_sightings.sql` creating `sightings` table (`id`, `user_id`, `latitude`, `longitude`, `recorded_at`, `created_at`) with indexes on `recorded_at` and `user_id`.
- Implemented RFC 9562 UUIDv7 generator in pure Kotlin for monotonic time-sortable primary keys.
- Implemented `SightingRepository` using parameterized JDBC queries with `ReentrantLock` write serialization to eliminate `SQLITE_BUSY` errors while keeping reads concurrent.
- Implemented `SightingService` with validation (range checks on coordinates, non-blank `user_id`) and DTO mapping.
- Implemented `SightingResource` exposing `POST /api/v1/sightings` (201 Created) and `GET /api/v1/sightings` (200 OK with wrapped collection and pagination).
- Added `IllegalArgumentExceptionMapper` and `JsonProcessingExceptionMapper` returning RFC 7807 problem details JSON on 400 Bad Request.
- Added minimal tracer web UI at `src/main/resources/META-INF/resources/index.html` verifying end-to-end browser tap and query flows.
- Automated tests in `SightingResourceTest.kt` verify valid POST creation, GET collection wrapping, coordinate validation, blank user validation, and malformed JSON rejection. All 5 tests pass cleanly in 12s.

## Plan Change Log

## Review Triage Log

- Location: `src/main/docker/` | Verdict: low | Evidence: Unused native and legacy Dockerfiles deleted. | Route: patch
- Location: `SightingService.kt:24` | Verdict: medium | Evidence: `recorded_at` made strictly required per openapi.yaml. | Route: patch
- Location: `SightingService.kt:37` | Verdict: medium | Evidence: `limit` and `offset` validated against schema bounds (1..500 and >= 0). | Route: patch
- Location: `SightingRepository.kt:31` | Verdict: medium | Evidence: Added fixed 3-digit millisecond ISO formatter for monotonic SQLite string sorting. | Route: patch
- Location: `SightingRepository.kt:44` | Verdict: medium | Evidence: Dynamic SQL query branching added to optimize `idx_sightings_user_id`. | Route: patch
- Location: `V1__init_sightings.sql:10` | Verdict: medium | Evidence: Changed column default to `strftime('%Y-%m-%dT%H:%M:%fZ', 'now')` for ISO format. | Route: patch
- Location: `ExceptionMappers.kt` | Verdict: low | Evidence: Added WebApplicationExceptionMapper producing RFC 7807 ProblemDetails. | Route: patch
- Location: `index.html` | Verdict: low | Evidence: Tracer UI checks `res.ok` and displays actual HTTP status code. | Route: patch
- Location: `SightingResourceTest.kt` | Verdict: low | Evidence: Added test database cleanup and assertions for all edge cases. | Route: patch
- Location: `build.gradle.kts` | Verdict: low | Evidence: Host OpenJDK 21.0.12.1 used with Gradle 9.3.1 per plan fallback instructions. | Route: defer

## Design Notes

- **UUIDv7 Generator:** Use a standard time-sortable UUIDv7 utility function or library (e.g. `com.fasterxml.uuid:java-uuid-generator` or custom 16-byte bitwise generator) to produce monotonic string IDs.
- **SQLite Concurrency:** Configure connection pool `max-size=1` for writers or standard WAL mode pragmas to avoid SQLite file locking contention.

## Verification

**Commands:**
- `./gradlew test` -- expected: BUILD SUCCESS with all test cases passing.
- `curl -s -X POST http://localhost:8080/api/v1/sightings -H "Content-Type: application/json" -d '{"user_id":"Tom","location":{"latitude":50.1109,"longitude":8.6821},"recorded_at":"2026-10-28T14:32:00Z"}'` -- expected: HTTP 201 with JSON sighting.
- `curl -s http://localhost:8080/api/v1/sightings` -- expected: HTTP 200 with `{ "items": [...], "total": 1 }`.
