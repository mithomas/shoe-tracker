---
id: 3
type: story
title: "Configurable Multi-Target Datasource (MySQL, H2 File, or H2 In-Memory)"
parent: epic-database-modernization
covers: [R26]
after: [1]
hitl: false
risk: low
---

# Configurable Multi-Target Datasource (MySQL, H2 File, or H2 In-Memory)

## Description

Adds `io.quarkus:quarkus-jdbc-mariadb` to dependencies alongside `quarkus-jdbc-h2`. Configures Quarkus datasource and Flyway so a single environment variable configuration (`QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_DB_KIND`, `QUARKUS_DATASOURCE_USERNAME`, `QUARKUS_DATASOURCE_PASSWORD`) seamlessly selects between:
1. External MySQL or MariaDB (`jdbc:mariadb://host:3306/shoetracker` with `db-kind=mariadb`)
2. Default embedded H2 file persistence (`jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`)
3. Embedded H2 in-memory mode (`jdbc:h2:mem:shoetracker;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`)

Ensures `DB_CLOSE_DELAY=-1` is mandated for in-memory mode so Flyway migrations and runtime queries access the same in-memory database instance.

## Requirements

- Add `io.quarkus:quarkus-jdbc-mariadb` dependency in `build.gradle.kts`.
- Default configuration in `application.properties` points to H2 file in `./data/shoe-tracker`.
- Document and support switching to in-memory H2 via `QUARKUS_DATASOURCE_JDBC_URL=jdbc:h2:mem:shoetracker;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`.
- Support external MySQL/MariaDB via `QUARKUS_DATASOURCE_DB_KIND=mariadb` and connection credentials.
- Update `docker-compose.yml` with optional MariaDB service definition and environment documentation.
- Automated tests verify application startup with default H2 file, in-memory H2, and simulated external datasource configuration.
- Verify: Application boots cleanly against default H2 file, H2 in-memory, and simulated external MySQL/MariaDB datasource configurations.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
