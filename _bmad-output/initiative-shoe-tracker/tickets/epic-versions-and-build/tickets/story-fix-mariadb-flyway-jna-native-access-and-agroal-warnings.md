---
id: 7
type: story
title: "Fix MariaDB 11.8 Flyway Support, JDK 25 JNA Native Access Warning, and Agroal Exception Sorter for MultiTargetJdbcDriver"
parent: epic-versions-and-build
covers: [R42]
after: [1, 2, 3, 4, 5, 6]
hitl: false
risk: low
status: "done"
---

# Fix MariaDB 11.8 Flyway Support, JDK 25 JNA Native Access Warning, and Agroal Exception Sorter for MultiTargetJdbcDriver

## Description

Fixes container startup failure against MariaDB 11.8 (`org.flywaydb.core.api.FlywayException: Unsupported Database: MariaDB 11.8`) by adding `io.quarkus:quarkus-flyway-mysql` (`org.flywaydb:flyway-mysql`), suppresses the JDK 25 `java.lang.System::load` restricted method warning from JNA by passing `--enable-native-access=ALL-UNNAMED` in `Dockerfile` and Gradle `Test` tasks, and eliminates the `AgroalConnectionConfigurer` startup warning for `database kind: other` by providing a `@JdbcDriver("other")` `MultiTargetAgroalConnectionConfigurer` bean that installs `MySQLExceptionSorter`.

## Requirements

- In `build.gradle.kts`:
  - Add `implementation("io.quarkus:quarkus-flyway-mysql")` next to `implementation("io.quarkus:quarkus-flyway")`.
  - Configure `tasks.withType<Test> { jvmArgs("--enable-native-access=ALL-UNNAMED") }`.
- In `Dockerfile`:
  - Add `--enable-native-access=ALL-UNNAMED` to `JAVA_TOOL_OPTIONS` and `ENTRYPOINT`.
- In `src/main/kotlin/org/shoetracker/repository/MultiTargetAgroalConnectionConfigurer.kt`:
  - Create an `@ApplicationScoped`, `@Unremovable`, `@JdbcDriver("other")` bean implementing `AgroalConnectionConfigurer` that overrides `setExceptionSorter` to install `MySQLExceptionSorter()` and overrides `disableSslSupport` cleanly without logging unsupported database kind warnings.
- In `src/test/kotlin/org/shoetracker/repository/MultiTargetJdbcDriverTest.kt` and `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`:
  - Verify that Flyway `DatabaseTypeRegister` resolves `MariaDB` (including `MariaDB 11.8`), `MySQL`, and `H2` database types without throwing `FlywayException`.
  - Verify `MultiTargetAgroalConnectionConfigurer` configures `MySQLExceptionSorter` and is registered in Arc for `@JdbcDriver("other")`.
  - Verify `build.gradle.kts` and `Dockerfile` contain `quarkus-flyway-mysql` and `--enable-native-access=ALL-UNNAMED`.
- Verify: `./gradlew check test spotlessCheck lintJs` passes cleanly.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md
