---
title: 'GraalVM Native Image Compilation with Dockerfile.native'
type: 'feature'
ticket: 4
created: '2026-10-29'
status: 'built'
baseline_revision: 'c44fa95'
route: 'full'
route_source: 'auto'
risk: 'medium'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 1
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Shoe Tracker currently packages only a JVM fast-jar container image and lacks `@RegisterForReflection` annotations on REST DTOs returned via generic `Response.entity(...)` builders, GraalVM native image build properties for H2/MariaDB/Flyway resources, and a `src/main/docker/Dockerfile.native` runtime definition for sub-50ms native startup.

**Approach:** Annotate all REST DTOs in `SightingDtos.kt` and `VersionResource.kt` with `@RegisterForReflection`, configure GraalVM native image build properties in `application.properties` / `build.gradle.kts` so `./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true` compiles a standalone Linux binary via Mandrel, create `src/main/docker/Dockerfile.native` based on `registry.access.redhat.com/ubi9/ubi-minimal` running as UID `185`, and verify native binary startup (< 50ms) and full CRUD/health/RFC-7807 parity against MariaDB/MySQL or an H2 TCP server.

## Boundaries & Constraints

**Always:**
- Annotate `GeoLocationDto`, `UserDto`, `CreateSightingRequest`, `SightingResponse`, `SightingListResponse`, and `ProblemDetails` in `SightingDtos.kt` plus `VersionResponse` in `VersionResource.kt` with `@io.quarkus.runtime.annotations.RegisterForReflection` (`AD-3`, `R27`).
- Ensure `MultiTargetJdbcDriver`, `org.mariadb.jdbc.Driver`, and `org.h2.Driver` are registered for reflection and that `version.properties` and `db/migration/V1__init_sightings.sql` are included as native resources (`quarkus.native.resources.includes=version.properties,db/migration/**`).
- Author `src/main/docker/Dockerfile.native` using `registry.access.redhat.com/ubi9/ubi-minimal:9.5` (or `ubi9/ubi-minimal`), creating `/work` and `/data` owned by UID `185` with `chmod 775`, copying `build/*-runner` to `/work/application`, exposing `8080`, and setting `USER 185`.
- Because `io.quarkus:quarkus-jdbc-h2` includes `io.quarkus.jdbc.h2.runtime.graal.Engine` which disables embedded `jdbc:h2:file:...` inside a GraalVM native binary, verify the native binary at runtime against an external MariaDB container (`jdbc:mariadb://...`) or a JVM-hosted H2 TCP server (`jdbc:h2:tcp://...;MODE=MySQL;...`).
- Document the native image build and `Dockerfile.native` container build commands in `README.md`.

**Never:**
- Do not break the default JVM build (`./gradlew build` without `-Dquarkus.native.enabled=true` must remain fast and default to JVM mode).
- Do not run the native container as `root`; always run as non-root `USER 185` matching the JVM `Dockerfile`.
- Do not omit `@RegisterForReflection` on `ProblemDetails`, as `ExceptionMappers.kt` passes it via `Response.entity(problem)` where generic type erasure hides it from Quarkus's compile-time Jackson scanner.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| GraalVM native compilation | `./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true -x test` | Produces executable `build/shoe-tracker-1.0.0-SNAPSHOT-runner` via Mandrel container build | Fails build if reflection/resource metadata is invalid |
| Native binary startup against MariaDB or H2 TCP | Execute `./build/*-runner` with `QUARKUS_DATASOURCE_JDBC_URL` pointing to MariaDB or H2 TCP server | Starts in `< 50ms` (`started in 0.0...s`), runs Flyway `V1__init_sightings.sql`, and serves `/q/health/live` and `/q/health/ready` (`200 OK`) | Exits fail-fast with `SQLException` if database is unreachable |
| Native REST CRUD & RFC 7807 error serialization | `POST /api/v1/sightings` (valid & invalid), `GET /api/v1/sightings`, `DELETE /api/v1/sightings/{id}`, `GET /api/v1/version` | Returns `201 Created`, `200 OK`, `204 No Content`, and `400 Bad Request` (`application/problem+json` `ProblemDetails`) with all JSON fields serialized accurately | No Jackson `InvalidDefinitionException` in native mode |

</frozen-after-approval>

## Code Map

- `src/main/kotlin/org/shoetracker/api/dto/SightingDtos.kt` -- Add `@RegisterForReflection` to `GeoLocationDto`, `UserDto`, `CreateSightingRequest`, `SightingResponse`, `SightingListResponse`, and `ProblemDetails` (lines 7-50).
- `src/main/kotlin/org/shoetracker/api/VersionResource.kt` -- Add `@RegisterForReflection` to `VersionResponse` (lines 10-13).
- `src/main/kotlin/org/shoetracker/repository/MultiTargetJdbcDriver.kt` -- Ensure `@RegisterForReflection(targets = [MultiTargetJdbcDriver::class, org.h2.Driver::class, org.mariadb.jdbc.Driver::class])` is present so `DriverManager` / Agroal can instantiate drivers reflectively in native mode.
- `src/main/resources/application.properties` -- Add `quarkus.native.resources.includes=version.properties,db/migration/**` and `quarkus.native.additional-build-args=--initialize-at-run-time=org.mariadb.jdbc.Configuration` (if needed by Mandrel).
- `src/main/docker/Dockerfile.native` -- New runtime Dockerfile packaging `build/*-runner` on `registry.access.redhat.com/ubi9/ubi-minimal` with non-root `USER 185` and `/data` volume permissions.
- `README.md` -- Add "GraalVM Native Image Build" section documenting `./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true` and `docker build -f src/main/docker/Dockerfile.native`.
- `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- Add `testNativeDockerfileAndReflectionAnnotations` verifying `src/main/docker/Dockerfile.native` structure and `@RegisterForReflection` presence on all DTOs.

## Tasks & Acceptance

**Execution:**
- [x] `src/main/kotlin/org/shoetracker/api/dto/SightingDtos.kt` and `src/main/kotlin/org/shoetracker/api/VersionResource.kt` -- Annotate all 7 DTO classes (`GeoLocationDto`, `UserDto`, `CreateSightingRequest`, `SightingResponse`, `SightingListResponse`, `ProblemDetails`, `VersionResponse`) with `@RegisterForReflection` -- Prevents GraalVM native Jackson serialization failures (`AD-3`, `R27`).
- [x] `src/main/kotlin/org/shoetracker/repository/MultiTargetJdbcDriver.kt` and `src/main/resources/application.properties` -- Register JDBC driver classes for reflection and include `version.properties` and `db/migration/**` in `quarkus.native.resources.includes` -- Ensures Flyway migrations, `/api/v1/version`, and JDBC driver loading work in native images.
- [x] `src/main/docker/Dockerfile.native` and `README.md` -- Create `src/main/docker/Dockerfile.native` based on `registry.access.redhat.com/ubi9/ubi-minimal` with `USER 185` and `/data` permissions, and document native build/run instructions in `README.md` -- Provides production native container packaging (`AD-3`, `R27`).
- [x] `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- Add automated test asserting `src/main/docker/Dockerfile.native` exists with `ubi-minimal`, `USER 185`, `EXPOSE 8080`, `/work/application`, and that all DTO classes carry `@RegisterForReflection` -- Guards native packaging and reflection invariants in CI.

**Acceptance Criteria:**
- Given all DTOs and native configuration, when `./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true -x test` runs, then the native executable `build/shoe-tracker-1.0.0-SNAPSHOT-runner` compiles cleanly.
- Given the compiled native executable connected to MariaDB/MySQL (or H2 TCP server), when started, then it logs startup in under 50ms (`< 0.050s`) and passes live curls against `/api/v1/version`, `/q/health/live`, `/q/health/ready`, `POST /api/v1/sightings` (201 and 400 RFC 7807), `GET /api/v1/sightings`, and `DELETE /api/v1/sightings/{id}`.
- Given `src/main/docker/Dockerfile.native`, when `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .` runs, then it builds a minimal runnable native container image running as UID `185`.

## Implementation Notes

- Annotated `GeoLocationDto`, `UserDto`, `CreateSightingRequest`, `SightingResponse`, `SightingListResponse`, and `ProblemDetails` in `SightingDtos.kt` plus `VersionResponse` in `VersionResource.kt` with `@RegisterForReflection`.
- Updated `UUIDv7.kt` to initialize `Generators.timeBasedEpochGenerator()` lazily (`by lazy`) and added `quarkus.native.additional-build-args=--initialize-at-run-time=org.shoetracker.domain.UUIDv7` and `quarkus.native.resources.includes=version.properties,db/migration/**` in `application.properties` so GraalVM native image compilation does not capture a build-time `Random` instance in the image heap.
- Added `System.setProperty("quarkus.package.jar.enabled", "false")` when `-Dquarkus.native.enabled=true` in `build.gradle.kts` to satisfy Quarkus 3.40+ packaging rules.
- Authored `src/main/docker/Dockerfile.native` based on `registry.access.redhat.com/ubi9/ubi-minimal:9.5` running as non-root `USER 185` with `/work` and `/data` directories, and documented native build instructions in `README.md`.
- Added `testNativeDockerfileAndReflectionAnnotations` in `DevOpsAssetTest.kt` and verified the compiled native binary (`build/shoe-tracker-1.0.0-SNAPSHOT-runner`) against a live H2 TCP database across `/api/v1/version`, `/q/health/ready`, `POST /api/v1/sightings` (201 & 400 RFC 7807), `GET /api/v1/sightings` (200), and `DELETE /api/v1/sightings/{id}` (204).

## Plan Change Log

## Review Triage Log

- `.dockerignore` (line 3) & `src/main/docker/Dockerfile.native` (line 9): `high` (patch) -- `.dockerignore` listed `build` without `!build/*-runner`, causing `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .` to fail at `COPY --chown=185:185 build/*-runner /work/application`. Patched `.dockerignore` to ignore `build/*` while un-ignoring `!build/*-runner` and `!build/quarkus-app`, and verified `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .` succeeds.

## Design Notes

**Native Image `Dockerfile.native` Layout:**
```dockerfile
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.5
WORKDIR /work/
RUN mkdir -p /work /data \
    && chown 185:185 /work /data \
    && chmod "g+rwX" /work /data
COPY --chown=185:185 build/*-runner /work/application
ENV QUARKUS_HTTP_HOST=0.0.0.0 \
    QUARKUS_HTTP_PORT=8080
VOLUME ["/data"]
EXPOSE 8080
USER 185
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with `DevOpsAssetTest` verifying `Dockerfile.native` and `@RegisterForReflection` annotations.
- `./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true -x test` -- expected: `BUILD SUCCESSFUL` producing `build/shoe-tracker-1.0.0-SNAPSHOT-runner` and verifying `< 50ms` startup and CRUD/health/RFC-7807 parity against MariaDB or H2 TCP.
