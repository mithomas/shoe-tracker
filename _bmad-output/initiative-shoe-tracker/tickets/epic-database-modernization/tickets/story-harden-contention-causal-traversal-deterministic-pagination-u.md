---
id: 6
type: story
title: "Harden Contention Causal Traversal, Deterministic Pagination, URL Normalization, MariaDB Unix Socket Support, and Zero Test SQLite Remnants"
parent: epic-database-modernization
covers: [R23, R24, R26, R27]
after: [5]
hitl: false
risk: low
---

# Harden Contention Causal Traversal, Deterministic Pagination, URL Normalization, MariaDB Unix Socket Support, and Zero Test SQLite Remnants

## Description

Addresses all findings from the Epic 6 implementation grill review plus MariaDB Unix domain socket (`?localSocket=...`) support:

1. **MariaDB Unix Domain Socket (`localSocket`) Support (`build.gradle.kts`):** MariaDB Connector/J (`org.mariadb.jdbc:mariadb-java-client:3.5.10`) marks `net.java.dev.jna:jna` and `net.java.dev.jna:jna-platform` as optional Maven dependencies. Without JNA on the runtime classpath, `ConnectionHelper`'s static initializer catches `NoClassDefFoundError: com/sun/jna/Platform` in `SocketUtility.getSocketHandler()` and silently falls back to `ConnectionHelper::standardSocket` (a plain TCP `java.net.Socket`), which then crashes in `connectSocket` with `IllegalArgumentException: connect: The address can't be null` because `connectSocket` sets `InetSocketAddress` to `null` whenever `hostAddress.localSocket != null`. Adding `net.java.dev.jna:jna` and `net.java.dev.jna:jna-platform` to `build.gradle.kts` enables `UnixDomainSocket` for `jdbc:mariadb://localhost/shoetracker?localSocket=/var/run/mysqld/mysqld.sock`.
2. **Contention Causal Graph Traversal & Catch-All Delegation (`ExceptionMappers.kt`):** Updates `DatabaseContentionExceptionMapper.findContentionException` to traverse both `SQLException.nextException` and `Throwable.cause` using an explicit queue and an `IdentityHashMap`-backed visited set (up to 16 nodes) instead of dropping `cause` whenever `nextException != null`. Also delegates `DatabaseContentionExceptionMapper.findContentionException(exception)` inside `ThrowableExceptionMapper.toResponse` so a `SQLException` wrapped inside a `RuntimeException` / `ArcUndeclaredThrowableException` still returns HTTP `503 Service Unavailable` with `Retry-After: 1`.
3. **Deterministic Timestamp Tie-Breaking (`SightingRepository.kt`):** Appends `, id DESC` to `ORDER BY` in `FIND_WITH_USER_SQL` and `FIND_ALL_SQL` (`ORDER BY ..., recorded_at DESC, id DESC`) so pagination (`LIMIT ? OFFSET ?`) is deterministic when multiple sightings share the same millisecond `TIMESTAMP(3)`.
4. **JDBC URL Whitespace & Case Normalization (`MultiTargetJdbcDriver.kt`):** Trims leading/trailing whitespace and matches `jdbc:h2:`, `jdbc:mariadb:`, and `jdbc:mysql:` prefixes case-insensitively (`startsWith(..., ignoreCase = true)`) in `MultiTargetJdbcDriver`.
5. **Native Container Default JDBC URL (`Dockerfile.native`):** Adds default `ENV QUARKUS_DATASOURCE_JDBC_URL="jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE"` in `src/main/docker/Dockerfile.native` matching `Dockerfile` and `Dockerfile.jvm`.
6. **Zero SQLite Remnants in Test Suite (`TestFaultResource.kt`, `SightingResourceTest.kt`, `DevOpsAssetTest.kt`):** Renames `/api/v1/test-faults/sqlite-busy` and `/api/v1/test-faults/sqlite-error` to `/api/v1/test-faults/db-busy` and `/api/v1/test-faults/db-error`, renames `testSqliteBusyReturns503WithRetryAfter` to `testDatabaseBusyReturns503WithRetryAfter`, and expands `DevOpsAssetTest.testZeroSqliteRemnantsAcrossProjectAssets` to scan `src/test` as well as `src/main`.

## Requirements

- In `build.gradle.kts`, add `implementation("net.java.dev.jna:jna")` and `implementation("net.java.dev.jna:jna-platform")` (managed by the Quarkus BOM) so MariaDB Connector/J `UnixDomainSocket` works for `?localSocket=/var/run/mysqld/mysqld.sock`.
- In `src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt`:
  - Traverse both `nextException` and `cause` in `DatabaseContentionExceptionMapper.findContentionException` using an `ArrayDeque<Throwable>` and `Collections.newSetFromMap(IdentityHashMap())` up to 16 nodes.
  - Check `DatabaseContentionExceptionMapper.findContentionException(exception)` at the top of `ThrowableExceptionMapper.toResponse` and return the `503` contention response with `Retry-After: 1` when found.
- In `src/main/kotlin/org/shoetracker/repository/SightingRepository.kt`:
  - Update `FIND_WITH_USER_SQL` and `FIND_ALL_SQL` to end with `ORDER BY is_own_shoe DESC, recorded_at DESC, id DESC LIMIT ? OFFSET ?` and `ORDER BY recorded_at DESC, id DESC LIMIT ? OFFSET ?`.
- In `src/main/kotlin/org/shoetracker/repository/MultiTargetJdbcDriver.kt`:
  - Trim `url` and match `jdbc:h2:`, `jdbc:mariadb:`, and `jdbc:mysql:` prefixes with `ignoreCase = true`, normalizing uppercase scheme prefixes so downstream drivers accept them.
- In `src/main/docker/Dockerfile.native`:
  - Set `ENV QUARKUS_DATASOURCE_JDBC_URL="jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE"`.
- In `src/test/kotlin/org/shoetracker/api/TestFaultResource.kt`, `src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt`, and `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`:
  - Replace `sqlite-busy` / `sqlite-error` endpoints and test names with `db-busy` / `db-error` and `testDatabaseBusyReturns503WithRetryAfter`, add unit tests verifying JNA `SocketUtility.getSocketHandler()` and `localSocket` URL handling (confirming it attempts Unix domain socket connection rather than throwing `IllegalArgumentException: connect: The address can't be null`), wrapped `RuntimeException(SQLException)` contention, branching `nextException` + `cause` traversal, and whitespace/case-insensitive `MultiTargetJdbcDriver` URLs, and scan `src/test` in `testZeroSqliteRemnantsAcrossProjectAssets`.
- Verify: `SocketUtility.getSocketHandler()` initializes `UnixDomainSocket` support with JNA on the classpath instead of throwing `IllegalArgumentException: connect: The address can't be null` on `localSocket` URLs, wrapped and chained contention exceptions return HTTP 503 with Retry-After: 1, SightingRepository orders by recorded_at DESC, id DESC, MultiTargetJdbcDriver handles whitespace and uppercase JDBC URL prefixes, Dockerfile.native includes default QUARKUS_DATASOURCE_JDBC_URL, zero sqlite remnants exist across src/main and src/test, and `./gradlew check test spotlessCheck lintJs` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
