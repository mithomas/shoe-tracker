---
title: 'Harden Contention Causal Traversal, Deterministic Pagination, URL Normalization, MariaDB Unix Socket Support, and Zero Test SQLite Remnants'
type: 'bugfix'
ticket: 6
created: '2026-10-29'
status: 'built'
baseline_revision: '00293d7'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'auto'
lenses_ran: ['quick']
review_loop_iteration: 1
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** MariaDB Connector/J (`org.mariadb.jdbc:mariadb-java-client:3.5.10`) marks `net.java.dev.jna:jna` and `net.java.dev.jna:jna-platform` as optional Maven dependencies, causing `ConnectionHelper` to silently fall back to an unconnected TCP `java.net.Socket` and crash with `IllegalArgumentException: connect: The address can't be null` when connecting via a Unix domain socket (`?localSocket=/var/run/mysqld/mysqld.sock`). Additionally, the Epic 6 grill review identified: (1) `DatabaseContentionExceptionMapper.findContentionException` drops `cause` when `nextException != null` and is bypassed when `SQLException` is wrapped in a `RuntimeException` handled by `ThrowableExceptionMapper`, (2) `SightingRepository` `ORDER BY recorded_at DESC` lacks an `, id DESC` tie-breaker for identical millisecond timestamps, (3) `MultiTargetJdbcDriver` does not trim whitespace or match JDBC prefixes case-insensitively, (4) `Dockerfile.native` omits the default `ENV QUARKUS_DATASOURCE_JDBC_URL`, and (5) test files still contain legacy `sqlite-busy` / `sqlite-error` names not scanned by `testZeroSqliteRemnantsAcrossProjectAssets`.

**Approach:** Add `net.java.dev.jna:jna` and `net.java.dev.jna:jna-platform` to `build.gradle.kts` so `SocketUtility.getSocketHandler()` initializes `UnixDomainSocket` support; update `DatabaseContentionExceptionMapper.findContentionException` to accept `Throwable` and traverse both `nextException` and `cause` via an `ArrayDeque` + `IdentityHashMap` visited set (up to 16 nodes) and invoke it inside `ThrowableExceptionMapper.toResponse`; append `, id DESC` to `FIND_WITH_USER_SQL` and `FIND_ALL_SQL` in `SightingRepository.kt`; trim and normalize URL prefixes case-insensitively in `MultiTargetJdbcDriver.kt`; add default `ENV QUARKUS_DATASOURCE_JDBC_URL` to `src/main/docker/Dockerfile.native`; and rename `sqlite-busy` / `sqlite-error` test endpoints and test methods while expanding `DevOpsAssetTest.testZeroSqliteRemnantsAcrossProjectAssets` to scan `src/test`.

</frozen-after-approval>

## Implementation Notes

- Oneshot route selected because the changes are targeted bugfixes and test updates under ~100 lines of logic.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
