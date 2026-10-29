---
type: epic
title: "Build Toolchain Modernization, CI/CD Upgrades & UI Polish"
parent: "initiative-shoe-tracker"
covers: ["R36", "R37", "R38", "R39", "R40", "R41"]
sources: ["_bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md"]
companions: ["_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md", "_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md"]
after: ["epic-database-modernization"]
refined: true
risk: low
---

# Build Toolchain Modernization, CI/CD Upgrades & UI Polish

## Description

Upgrades the local Gradle build toolchain, Java source/target compatibility, Kotlin JVM bytecode target, and GitHub Actions CI setup from Java 21 fallback to JDK 25 (`eclipse-temurin:25`). Migrates GitHub Actions steps (`actions/checkout`, `actions/setup-java`, `gradle/actions/wrapper-validation`) from `v4` (Node 20) to `v5` (Node 24). Streamlines the CI Docker Hub release pipeline to build and publish only `linux/amd64` without `linux/arm64` cross-compilation or QEMU emulation overhead. Updates Docker Hub image references in `docker-compose.yml` and `README.md` from placeholder `mitch/shoe-tracker` to `mithomas/shoe-tracker`. Removes the redundant ` — Germany Overview` / ` — Deutschland-Übersicht` suffix from the HUD mini-map location-denied overlay (`#miniMapDeniedOverlay`).

## Outcome

Shoe Tracker builds and tests uniformly on JDK 25 across local development, CI, and container runtimes; executes CI workflows natively on Node 24 (`@v5`) with fast native `linux/amd64` container builds; points users to the official `mithomas/shoe-tracker` Docker Hub image; and renders a clean, concise GPS-denied overlay in English and German.

## Requirements

- R36: Local development, Gradle Java toolchain (`JavaLanguageVersion.of(25)`), `sourceCompatibility`/`targetCompatibility` (`JavaVersion.VERSION_25`), Kotlin `JvmTarget.JVM_25`, GitHub Actions CI (`java-version: '25'`), and `README.md` prerequisites target JDK 25 with zero Java 21 fallbacks.
- R37: GitHub Actions CI workflow (`.github/workflows/ci.yml`) runs on `actions/checkout@v5`, `actions/setup-java@v5`, and `gradle/actions/wrapper-validation@v5` targeting Node.js 24 natively with zero `@v4` references.
- R38: Docker Hub CI release job builds and publishes only the `linux/amd64` container platform without `linux/arm64` cross-compilation or `docker/setup-qemu-action` emulation overhead.
- R39: Default container image in `docker-compose.yml` (`${DOCKERHUB_IMAGE:-mithomas/shoe-tracker:latest}`) and container deployment examples in `README.md` reference `mithomas/shoe-tracker` instead of placeholder `mitch/shoe-tracker`.
- R40: HUD mini-map location-denied overlay (`#miniMapDeniedOverlay` in `index.html` and `spot.deniedOverlay` in `i18n.js`) displays concise `⚠️ Location Access Denied` (EN) and `⚠️ Standortzugriff verweigert` (DE) without the redundant ` — Germany Overview` / ` — Deutschland-Übersicht` suffix.
- R41: Geolocation unavailable/denied state (`no-gps`), DOM/CSS/i18n identifiers (`#miniMapNoGpsOverlay`, `.gps-pill.no-gps`, `gps.noGps`, `spot.noGps`, `spot.noGpsOverlay`), and UI labels across the header GPS pill, giant Spot button, and HUD mini-map overlay use concise `No GPS` / `⚠️ No GPS` (EN) and `Kein GPS` / `⚠️ Kein GPS` (DE) instead of `GPS Denied` / `GPS Verweigert` / `Location Access Denied`.

## Done when

- `build.gradle.kts` sets `JavaVersion.VERSION_25`, `JavaLanguageVersion.of(25)`, and `JvmTarget.JVM_25` with zero `21` references.
- `.github/workflows/ci.yml` uses `actions/checkout@v5`, `actions/setup-java@v5` with `java-version: '25'`, `gradle/actions/wrapper-validation@v5`, `platforms: linux/amd64` only, and no `setup-qemu-action` or `@v4` references.
- `docker-compose.yml` defaults to `${DOCKERHUB_IMAGE:-mithomas/shoe-tracker:latest}` and `README.md` documents `JDK 25` and `mithomas/shoe-tracker`.
- `index.html`, `style.css`, `geo.js`, `minimap.js`, `spot.js`, and `i18n.js` use `no-gps` / `noGps` identifiers (`#miniMapNoGpsOverlay`, `.gps-pill.no-gps`, `gps.noGps`, `spot.noGps`, `spot.noGpsOverlay`), render `⚠️ No GPS` (EN) and `⚠️ Kein GPS` (DE) on the mini-map overlay without `Germany Overview` or `Deutschland-Übersicht`, and render `No GPS` (EN) / `Kein GPS` (DE) for the header pill and Spot button.
- `DevOpsAssetTest.kt` and `FrontendAssetTest.kt` verify all updated versions, build targets, image names, and UI strings, and `./gradlew check test spotlessCheck lintJs` passes cleanly.

## Boundaries

- Must not change: GitHub repository URL (`https://github.com/mitch/shoe-tracker` in `README.md` and `index.html`) or `.github/workflows/ci.yml` secret interpolation (`${{ secrets.DOCKERHUB_USERNAME }}/shoe-tracker`).
- Must not change: HUD mini-map fallback center coordinates (`[50.8, 9.7]`, zoom `6` in `minimap.js`).

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md

## Notes

- Decision: 2026-10-29: Stories `7.1 -> 7.2 -> 7.3 -> 7.4 -> 7.5` execute sequentially because Stories 7.1–7.4 share edits to `.github/workflows/ci.yml`, `README.md`, and `DevOpsAssetTest.kt`.
