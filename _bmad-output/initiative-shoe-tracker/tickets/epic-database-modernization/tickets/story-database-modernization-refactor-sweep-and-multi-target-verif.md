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

Final audit and verification sweep for Epic 6. Audits build dependencies to confirm complete removal of `sqlite-jdbc`, runs the full Gradle check and Spotless formatting suite, executes multi-target database integration tests (H2 file, H2 in-memory, and external datasource), tests live health probes, and verifies both JVM fast-jar and GraalVM native container builds.

## Requirements

- Audit `build.gradle.kts` and classpath: zero references to `org.xerial:sqlite-jdbc`.
- Run full test and check suite: `./gradlew check test spotlessCheck lintJs` with zero failures.
- Verify container builds:
  - Standard fast-jar: `docker build -t shoe-tracker:h2 .`
  - Native binary container: `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .`
- Verify live health probes reporting live `databaseProductName` (`H2` or `MariaDB`) on `/q/health/ready`.
- Verify: All Gradle checks, Spotless formatting, health probes, container fast-jar, and native binary builds pass cleanly.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
