---
type: story
title: "Fix MariaDB 11.8 Flyway Support, JDK 25 JNA Native Access Warning, and Agroal Exception Sorter for MultiTargetJdbcDriver"
status: refined
covers: ["R42"]
---

# Fix MariaDB 11.8 Flyway Support, JDK 25 JNA Native Access Warning, and Agroal Exception Sorter for MultiTargetJdbcDriver

## Context
When starting the Shoe Tracker container on JDK 25 against an external MariaDB 11.8 instance (`QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://...`), three issues occur at startup:
1. `org.flywaydb.core.api.FlywayException: Unsupported Database: MariaDB 11.8` because Flyway 12 (`flyway-core:12.0.0` in Quarkus `3.40.1.1`) splits MariaDB/MySQL support into `io.quarkus:quarkus-flyway-mysql` (`org.flywaydb:flyway-mysql`).
2. `WARNING: A restricted method in java.lang.System has been called` (`java.lang.System::load has been called by com.sun.jna.Native in an unnamed module`) when MariaDB Connector/J initializes JNA on JDK 25 without `--enable-native-access=ALL-UNNAMED`.
3. `WARN [io.quarkus.agroal.runtime.AgroalConnectionConfigurer] (main) Agroal does not support detecting if a connection is still usable after an exception for database kind: other` because `quarkus.datasource.db-kind=other` falls back to `UnknownDbAgroalConnectionConfigurer`.

## Acceptance Criteria
1. `build.gradle.kts` includes `implementation("io.quarkus:quarkus-flyway-mysql")` so Flyway's `DatabaseTypeRegister` registers `MariaDBDatabaseType` and `MySQLDatabaseType` alongside `H2DatabaseType` and supports MariaDB 11.8+.
2. `Dockerfile` (`JAVA_TOOL_OPTIONS` and `ENTRYPOINT`) and `build.gradle.kts` (`tasks.withType<Test>()`) pass `--enable-native-access=ALL-UNNAMED` to suppress JDK 25 restricted native access warnings from JNA.
3. `MultiTargetAgroalConnectionConfigurer` (`@ApplicationScoped`, `@JdbcDriver("other")`, `@Unremovable`) implements `AgroalConnectionConfigurer` and configures `MySQLExceptionSorter` on the Agroal connection pool so fatal connection errors (`SQLState 08*` and MariaDB/MySQL fatal error codes) are detected and the `database kind: other` startup warning is eliminated.
4. `MultiTargetJdbcDriverTest.kt` and `DevOpsAssetTest.kt` verify Flyway MariaDB/MySQL/H2 database type registration, `MultiTargetAgroalConnectionConfigurer`, `quarkus-flyway-mysql`, and `--enable-native-access=ALL-UNNAMED`, and `./gradlew check test spotlessCheck lintJs` passes cleanly.
