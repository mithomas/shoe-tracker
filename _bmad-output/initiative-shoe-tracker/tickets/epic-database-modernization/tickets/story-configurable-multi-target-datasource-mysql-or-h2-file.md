---
id: 3
type: story
title: "Configurable Multi-Target Datasource (MySQL or H2 File)"
parent: epic-database-modernization
covers: [R26]
after: [1, 2]
hitl: false
risk: low
---

# Configurable Multi-Target Datasource (MySQL or H2 File)

## Description

Bundles `io.quarkus:quarkus-jdbc-mariadb` alongside `io.quarkus:quarkus-jdbc-h2` internally and enables automatic startup datasource routing based solely on the driver prefix in the single `QUARKUS_DATASOURCE_JDBC_URL` environment variable (plus optional `QUARKUS_DATASOURCE_USERNAME` and `QUARKUS_DATASOURCE_PASSWORD`), without requiring the operator to configure `db-kind` or driver class flags:
1. Default embedded H2 file persistence (`jdbc:h2:file:./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE` locally, `/data/shoe-tracker` in container) -> routes to `org.h2.Driver` / H2 datasource at startup.
2. External MariaDB or MySQL (`jdbc:mariadb://host:3306/shoetracker` or `jdbc:mysql://host:3306/shoetracker`) -> routes to `org.mariadb.jdbc.Driver` / MariaDB datasource at startup.

Because Quarkus fixes `quarkus.datasource.db-kind` at build time (`SRCFG00025`), this story implements internal startup driver routing (via a delegating `java.sql.Driver` with `db-kind=other` or named H2/MariaDB datasources switched at startup by a `SmallRye ConfigSourceInterceptor` / active datasource producer) so a single `QUARKUS_DATASOURCE_JDBC_URL` string transparently selects the matching internal driver and runs Flyway migrations at startup.

Also updates `Dockerfile` and `docker-compose.yml` to replace the stale `jdbc:sqlite:/data/shoe-tracker.db...` URL with the H2 file URL, and documents single-URL datasource configuration in `README.md`.

## Requirements

- Add `io.quarkus:quarkus-jdbc-mariadb` dependency in `build.gradle.kts` alongside `io.quarkus:quarkus-jdbc-h2`.
- Implement internal startup driver switching based on the prefix of `QUARKUS_DATASOURCE_JDBC_URL` (`jdbc:h2:file:...` vs `jdbc:mariadb://...` / `jdbc:mysql://...`) so both H2 and MariaDB/MySQL work from a single JDBC URL environment variable.
- Default configuration in `application.properties` points to H2 file in `./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`.
- Update `Dockerfile` and `docker-compose.yml` default `QUARKUS_DATASOURCE_JDBC_URL` from `jdbc:sqlite:/data/shoe-tracker.db...` to `jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`, and include commented/optional MariaDB service and `QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://...` override examples in `docker-compose.yml`.
- Document datasource configuration options (default embedded H2 file and external MariaDB/MySQL via `QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME`, `QUARKUS_DATASOURCE_PASSWORD`) in root `README.md`.
- Add automated tests (including `QuarkusTestProfile` / driver routing tests and `DevOpsAssetTest`) verifying startup routing for both `jdbc:h2:file:...` and `jdbc:mariadb://...` URLs and asserting `Dockerfile`, `docker-compose.yml`, and `README.md` contain the H2 file and MariaDB configuration.
- Verify: Application switches datasource driver automatically at startup based solely on `QUARKUS_DATASOURCE_JDBC_URL` (H2 file vs MariaDB/MySQL), `Dockerfile` and `docker-compose.yml` use H2 file URLs, and `README.md` documents single-URL configuration.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
