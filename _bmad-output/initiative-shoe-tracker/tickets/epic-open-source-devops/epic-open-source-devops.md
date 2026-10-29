---
type: epic
title: "Open Source Licensing & CI/CD Publication"
parent: "initiative-shoe-tracker"
covers: ["R10", "R11", "R12", "R13", "R14", "R15", "R16", "R17"]
sources: []
companions: []
after: ["epic-views-and-map"]
refined: true
risk: low
status: "done"
---

# Open Source Licensing & CI/CD Publication

## Description

Adopts the GNU Affero General Public License (AGPL-3.0) for the repository, establishes a GitHub Actions workflow for automated test execution, publishes multi-platform container images to Docker Hub on release tags, exposes the Git commit hash in the version display, configures automated Dependabot dependency updates, migrates tests to AssertJ fluent assertions, realigns domain and service layer packages, and enforces code hygiene across Kotlin and web assets via a 100% pure-JVM Spotless and Closure Compiler linting suite.

## Outcome

Shoe Tracker is legally protected as copyleft open-source software, continuously tested and published to Docker Hub upon release, stamped with traceable Git commit metadata, kept up-to-date automatically, and guarded by fluent tests, clean DDD boundaries, and pure-JVM linting and formatting.

## Requirements

- R10: Repository and application artifacts are licensed under the GNU Affero General Public License v3.0 (AGPL-3.0) with copyleft notice and source code availability link in web footer.
- R11: Continuous Integration workflow executes Gradle check and test suites on every pull request and push to main.
- R12: Release workflow builds and publishes multi-platform container images to Docker Hub on semantic version tags (`v*`).
- R13: Version display in footer and `/api/v1/version` reports current Git short commit hash.
- R14: Dependabot monitors Gradle, Docker, and GitHub Actions dependencies weekly.
- R15: Test assertions across Kotlin integration and unit test suites use AssertJ fluent assertions (`assertThat`) with detailed failure diffs.
- R16: Build lifecycle enforces code hygiene, formatting, and linting across Kotlin and web assets via pure JVM Spotless and Closure Compiler without Node.js dependencies.
- R17: Architecture package boundaries separate pure domain entities (`org.shoetracker.domain`) from application orchestration (`org.shoetracker.service`), and Quarkus static web resource layout (`META-INF/resources`) is documented.

## Done when

- `LICENSE` file contains full text of GNU Affero General Public License v3 (AGPL-3.0).
- `README.md` documents AGPL-3.0 licensing and Docker Compose deployment instructions.
- Web interface footer links to source code availability per AGPL network copyleft terms.
- Application version badge and `/api/v1/version` output current Git short commit hash.
- `.github/workflows/ci.yml` runs `./gradlew check test` on PRs and main branch pushes.
- Release workflow pushes `<dockerhub-user>/shoe-tracker:<version>` and `:latest` images to Docker Hub on `v*` tags.
- `.github/dependabot.yml` monitors Gradle, Docker, and GitHub Actions dependencies.
- Kotlin test suites assert conditions using AssertJ `assertThat`.
- SightingService resides in `org.shoetracker.service` package and `org.shoetracker.domain` has zero outward dependencies on `repository` or `api.dto`.
- `./gradlew check` and `./gradlew spotlessCheck` enforce formatting and linting for Kotlin (`ktlint`), Gradle scripts, HTML/CSS/JS web assets, and Closure Compiler JS analysis.

## Boundaries

- Frontend UI feature enhancements and database migrations belong to other epics.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md

## Notes

- Decision: 2026-10-29: Uncoupled from Epic 3 to build immediately following Epic 2 (`epic-views-and-map`).
- Decision: 2026-10-29: Default Docker Hub target repository set to `${{ secrets.DOCKERHUB_USERNAME }}/shoe-tracker`.
- Decision: 2026-10-29: AGPL-3.0 copyleft source code link placed in web interface footer.
