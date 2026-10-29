# Walkthrough: Epic 5 (Architecture Hardening & Offline Resilience)

Target: `1dc8e79..7627066`

- [x] [Overview](#overview) (done)
- [x] [Slice 1: Global RFC 7807 500 ExceptionMapper](#slice-1-global-rfc-7807-500-exceptionmapper) (done)
- [x] [Slice 2: Eager Statements & Startup Fail-Fast Validation](#slice-2-eager-statements--startup-fail-fast-validation) (done)
- [x] [Slice 3: SQLite Lock Contention Translation (503)](#slice-3-sqlite-lock-contention-translation-503) (done)
- [x] [Slice 4: SmallRye Health Liveness & Readiness Probes](#slice-4-smallrye-health-liveness--readiness-probes) (done)
- [x] [Slice 5: CI Workflow Linters & Checkers](#slice-5-ci-workflow-linters--checkers) (done)
- [x] [Periphery](#periphery) (done)

---

## Overview

Epic 5 hardens the backend architecture and container orchestration against runtime errors, transient database locks, and silent startup failures. It establishes a catch-all RFC 7807 500 exception mapper with unique UUID error correlation, compiles all prepared statements eagerly with startup `SELECT 1` validation, translates SQLite lock contention into retryable HTTP 503 responses with `Retry-After: 1`, provides standard container health probes (`/q/health/live`, `/q/health/ready`), and adds discrete Spotless, Closure Compiler, and PR container build steps to CI.

---

## Slice 1: Global RFC 7807 500 ExceptionMapper

Catches all unhandled server runtime exceptions (`Throwable`), generates a unique UUID `error_id`, logs the internal stack trace, and returns a sanitized HTTP 500 `application/problem+json` response without leaking internal errors.

- [ExceptionMappers.kt](../../../src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt) (`ThrowableExceptionMapper`)
- [SightingDtos.kt](../../../src/main/kotlin/org/shoetracker/api/dto/SightingDtos.kt) (`ProblemDetails.errorId`)
- [SightingResourceTest.kt](../../../src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt)

---

## Slice 2: Eager Statements & Startup Fail-Fast Validation

Annotates `SightingRepository` with `@Startup` and `@PostConstruct` to compile all 7 prepared statements (including `deleteById`) at application bootstrap. Executes `SELECT 1` to fail fast on boot if database connectivity is broken, and guards `@PreDestroy close()` against uninitialized property errors.

- [SightingRepository.kt](../../../src/main/kotlin/org/shoetracker/repository/SightingRepository.kt)
- [SightingRepositoryTest.kt](../../../src/test/kotlin/org/shoetracker/repository/SightingRepositoryTest.kt)

---

## Slice 3: SQLite Lock Contention Translation (503)

Intercepts `org.sqlite.SQLiteException` and translates transient concurrency contention (`SQLITE_BUSY`, `SQLITE_LOCKED`) into HTTP 503 `Service Unavailable` with header `Retry-After: 1` and RFC 7807 problem details.

- [ExceptionMappers.kt](../../../src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt) (`SQLiteExceptionMapper`)
- [SightingResourceTest.kt](../../../src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt)

---

## Slice 4: SmallRye Health Liveness & Readiness Probes

Adds `io.quarkus:quarkus-smallrye-health` and implements a dedicated `DatabaseHealthCheck` (`@Readiness`) executing `SELECT 1` on SQLite. Exposes `/q/health/live`, `/q/health/ready`, and `/q/health` returning HTTP 200 `UP`.

- [build.gradle.kts](../../../build.gradle.kts)
- [DatabaseHealthCheck.kt](../../../src/main/kotlin/org/shoetracker/health/DatabaseHealthCheck.kt)
- [HealthResourceTest.kt](../../../src/test/kotlin/org/shoetracker/health/HealthResourceTest.kt)

---

## Slice 5: CI Workflow Linters & Checkers

Updates `.github/workflows/ci.yml` test job to add discrete steps for Spotless code formatting (`spotlessCheck`), Google Closure Compiler JavaScript static analysis (`lintJs`), full checks and tests (`check test`), and pull request container build validation (`docker build`).

- [.github/workflows/ci.yml](../../../.github/workflows/ci.yml)
- [DevOpsAssetTest.kt](../../../src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt)

---

## Periphery

- [epic-architecture-resilience.md](../../tickets/epic-architecture-resilience/epic-architecture-resilience.md)
- [tickets.toml](../../tickets/epic-architecture-resilience/tickets/tickets.toml)
