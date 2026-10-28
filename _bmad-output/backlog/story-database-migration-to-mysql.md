---
type: story
title: "Configurable External MariaDB/MySQL Datasource with H2 Fallback"
parent: ""
covers: []
sources: []
companions: []
after: ["story-replace-sqlite-with-h2.md"]
risk: low
refined: false
---

# Configurable External MariaDB/MySQL Datasource with H2 Fallback

## Description

Enables connecting Shoe Tracker to an external MariaDB (or MySQL) server via environment variable configuration, while keeping embedded H2 as the default out-of-the-box fallback when no external datasource is configured. No data migration between databases is performed.

## Background & Rationale

With H2 established as the default embedded storage engine running in `MODE=MySQL`, both H2 and MariaDB/MySQL share the exact same ANSI/MySQL Flyway DDL. By including both the H2 driver and the MariaDB driver in the container build, users can run the single container standalone with embedded H2 or point it to a clustered production MariaDB instance simply by supplying standard Quarkus environment variables at runtime, without code changes or data migration scripts.

## Acceptance Criteria

- Include both `io.quarkus:quarkus-jdbc-h2` and `io.quarkus:quarkus-jdbc-mariadb` (or `mysql`) dependencies in `build.gradle.kts`.
- Default configuration uses embedded H2 (`quarkus.datasource.db-kind=h2`).
- When environment variables are supplied:
  ```bash
  QUARKUS_DATASOURCE_DB_KIND=mariadb
  QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://host:3306/shoetracker
  QUARKUS_DATASOURCE_USERNAME=user
  QUARKUS_DATASOURCE_PASSWORD=pass
  ```
  Quarkus initializes its connection pool against the external MariaDB instance.
- No live data migration scripts are required; Flyway executes the existing `V1` DDL directly on whichever database is targeted.
- Automated tests verify application startup with both H2 default and simulated external datasource configuration.
