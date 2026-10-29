---
id: 5
type: story
title: "Database Modernization Refactor Sweep and Multi-Target Verification"
parent: epic-database-modernization
covers: [R23, R24, R25, R26, R27]
after: [1, 2, 3, 4]
hitl: false
risk: low
---

# Database Modernization Refactor Sweep and Multi-Target Verification

## Description

Final audit and verification sweep for Epic 6. Audits build dependencies, source files, and DevOps assets (`Dockerfile`, `docker-compose.yml`, `.github/workflows/ci.yml`, `README.md`) to confirm complete removal of `sqlite-jdbc` and stale `jdbc:sqlite:` references, runs the full Gradle check, Spotless formatting, and JS lint suite, executes multi-target database integration tests verifying startup switching between embedded H2 file persistence and MariaDB/MySQL via the single `QUARKUS_DATASOURCE_JDBC_URL` variable, tests live health probes, and verifies both JVM fast-jar and GraalVM native container builds.

## Requirements

- Audit `build.gradle.kts`, source code, `Dockerfile`, `docker-compose.yml`, and `README.md`: zero references to `org.xerial:sqlite-jdbc`, `org.sqlite`, or `jdbc:sqlite:`.
- Run full test and check suite: `./gradlew check test spotlessCheck lintJs` with zero failures.
- Verify single-URL startup switching between embedded H2 file (`jdbc:h2:file:...`) and MariaDB/MySQL (`jdbc:mariadb://...`).
- Verify container builds:
  - Standard fast-jar: `docker build -t shoe-tracker:h2 .`
  - Native binary container: `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .`
- Verify live health probes reporting live `databaseProductName` (`H2` or `MariaDB`) under `"Database connection health check"` on `/q/health/ready`.
- Verify: All Gradle checks, Spotless formatting, JS lint, DevOps asset assertions, health probes, container fast-jar, and native binary builds pass cleanly.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
