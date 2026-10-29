# Walkthrough: Epic 4 (Open Source Licensing & CI/CD Publication)

Target: `1fd92d9..25b085c`

- [x] [Overview](#overview) (done)
- [x] [Slice 1: Licensing & Copyleft Attribution](#slice-1-licensing--copyleft-attribution) (done)
- [x] [Slice 2: Build Versioning & Commit Hash Stamping](#slice-2-build-versioning--commit-hash-stamping) (done)
- [x] [Slice 3: CI/CD Pipelines & Containerization](#slice-3-cicd-pipelines--containerization) (done)
- [x] [Slice 4: Architecture Package Realignment](#slice-4-architecture-package-realignment) (done)
- [x] [Slice 5: Test Harness Modernization (AssertJ)](#slice-5-test-harness-modernization-assertj) (done)
- [x] [Slice 6: Pure-JVM Linting, Formatting & Line Governance](#slice-6-pure-jvm-linting-formatting--line-governance) (done)
- [x] [Periphery](#periphery) (done)

---

## Overview

Epic 4 establishes copyleft licensing, build reproducibility, automated validation, and code hygiene for the Shoe Tracker repository. It stamps Git commit metadata during build processing and serves it via HTTP and web UI, automates multi-platform container builds on release tags via GitHub Actions, decouples domain entities from service orchestration, migrates test suites to fluent AssertJ assertions, and enforces zero-Node pure-JVM linting and formatting on every build.

---

## Slice 1: Licensing & Copyleft Attribution

Adopts the GNU Affero General Public License v3.0 (AGPL-3.0) to ensure network copyleft protection. The web interface footer includes a prominent source code availability link to fulfill AGPL copyleft obligations for remote web users.

- [LICENSE](../../../LICENSE) -- Full GNU AGPL-3.0 license text.
- [README.md](../../../README.md#license) -- Licensing summary and copyleft notice.
- [index.html](../../../src/main/resources/META-INF/resources/index.html) -- Footer source attribution link (`footer-license-link`).
- [style.css](../../../src/main/resources/META-INF/resources/css/style.css) -- Footer link styling.
- [DevOpsAssetTest.kt](../../../src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt) -- Automated assertions verifying LICENSE file presence and AGPL footer link.

---

## Slice 2: Build Versioning & Commit Hash Stamping

Extracts the short 7-character Git commit hash at build time and writes it to `version.properties` in resources. The `/api/v1/version` REST endpoint reads this property file, and the frontend fetches and displays the version badge and Git hash in the footer upon page load.

- [build.gradle.kts](../../../build.gradle.kts) (`generateVersionProperties`) -- Extracts Git short hash via `git rev-parse --short=7 HEAD` or fallback env var.
- [VersionResource.kt](../../../src/main/kotlin/org/shoetracker/api/VersionResource.kt) -- REST endpoint `/api/v1/version` returning `version` and `commit`.
- [index.html](../../../src/main/resources/META-INF/resources/index.html) -- Script fetching `/api/v1/version` and updating `#versionBadge`.
- [VersionResourceTest.kt](../../../src/test/kotlin/org/shoetracker/api/VersionResourceTest.kt) -- Tests `/api/v1/version` response structure and 7-character hex hash.

---

## Slice 3: CI/CD Pipelines & Containerization

Establishes automated build and test validation on GitHub Actions for pull requests and main branch pushes. Semantic version release tags trigger multi-platform container builds (`linux/amd64`, `linux/arm64`) pushed to Docker Hub, complemented by weekly Dependabot maintenance and local Docker Compose orchestration.

- [.github/workflows/ci.yml](../../../.github/workflows/ci.yml) -- GitHub Actions workflow with `test` and `release` jobs.
- [.github/dependabot.yml](../../../.github/dependabot.yml) -- Dependabot configuration for Gradle, Docker, and GitHub Actions ecosystems.
- [Dockerfile](../../../Dockerfile) -- Multi-stage build on JDK 25 with `.editorconfig` inclusion, non-root user (UID 185), and `/data` volume.
- [docker-compose.yml](../../../docker-compose.yml) -- Standard service definition binding host `./data` directory.
- [DevOpsAssetTest.kt](../../../src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt) -- Structural validation for CI, Dependabot, and Docker Compose configurations.

---

## Slice 4: Architecture Package Realignment

Separates domain models from application service orchestration by migrating `SightingService` from `org.shoetracker.domain` to `org.shoetracker.service`. The `domain` package now contains zero dependencies on repositories or external layers, enforcing clean DDD boundaries.

- [SightingService.kt](../../../src/main/kotlin/org/shoetracker/service/SightingService.kt) -- Application orchestration service in dedicated service layer.
- [SightingResource.kt](../../../src/main/kotlin/org/shoetracker/api/SightingResource.kt) -- Updated import referencing service package.
- [architecture-shoe-tracker.md](../../architecture-shoe-tracker/architecture-shoe-tracker.md) -- Architecture documentation updated with package structure and Quarkus `META-INF/resources` static asset conventions.

---

## Slice 5: Test Harness Modernization (AssertJ)

Migrates all Kotlin test assertions from `kotlin.test` and JUnit assertions to AssertJ fluent assertions (`assertThat`). Provides rich contextual error diagnostics and standardized assertion syntax across all test suites.

- [build.gradle.kts](../../../build.gradle.kts) -- Added `org.assertj:assertj-core:3.27.3`.
- [SightingResourceTest.kt](../../../src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt) -- API integration tests using fluent assertions.
- [VersionResourceTest.kt](../../../src/test/kotlin/org/shoetracker/api/VersionResourceTest.kt) -- Version endpoint tests migrated to AssertJ.
- [DevOpsAssetTest.kt](../../../src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt) -- Configuration and asset tests using fluent assertions.
- [FrontendAssetTest.kt](../../../src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt) -- Static web assets, DOM elements, and coordinate formatting tests using AssertJ.

---

## Slice 6: Pure-JVM Linting, Formatting & Line Governance

Introduces Spotless ktlint, web asset formatting (HTML/CSS/JS), and Google Closure Compiler JavaScript static analysis without Node.js dependencies into `./gradlew check`. Enforces LF line endings across platforms via `.gitattributes` and `.editorconfig` (120-char max line length) while safeguarding binary assets.

- [build.gradle.kts](../../../build.gradle.kts) (`spotless`, `lintJs`) -- Spotless plugin and Closure Compiler task wired into `check`.
- [.editorconfig](../../../.editorconfig) -- Formatting rules and `max_line_length = 120`.
- [.gitattributes](../../../.gitattributes) -- `* text=auto eol=lf`, `*.bat text eol=crlf`, and binary rules (`*.png binary`, etc.).
- [Dockerfile](../../../Dockerfile) -- Copies `.editorconfig` so containerized builds respect line length limits.

---

## Periphery

- [story-cluster-map-markers-by-zoom-level.md](../../backlog/story-cluster-map-markers-by-zoom-level.md) -- Backlog story for clustering nearby sightings by zoom level.
- [story-display-global-and-personal-sighting-counters.md](../../backlog/story-display-global-and-personal-sighting-counters.md) -- Backlog story for total and user sighting counters.
- [story-remove-empty-sightings-overlay-from-map.md](../../backlog/story-remove-empty-sightings-overlay-from-map.md) -- Backlog story for removing empty banner overlay.
- [epic-open-source-devops.md](../../tickets/epic-open-source-devops/epic-open-source-devops.md) -- Refined Epic 4 specification and requirements.
- [tickets.toml](../../tickets/epic-open-source-devops/tickets/tickets.toml) -- Story tickets tracking all 8 Epic 4 deliveries.
