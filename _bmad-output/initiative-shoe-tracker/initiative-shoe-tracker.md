---
type: initiative
title: "Shoe Tracker - Autobahn Shoe Spotting Web App"
sources: []
companions: ["_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md", "_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md"]
after: []
risk: low
---

# Shoe Tracker - Autobahn Shoe Spotting Web App

## Description

A mobile-friendly web application allowing travelers on the autobahn to log spotted shoes with a single tap. The system records the precise geolocation (kept warm in the background to eliminate GPS latency at speed) and timestamp of each shoe sighting under the user's name, providing both a historical list and an interactive map visualization. The application is designed as a two-tier system with a lean REST API, powered by a Kotlin Quarkus backend persisting to an embedded H2 database (`MODE=MySQL`) by default or an external MariaDB/MySQL database switched automatically at startup from a single JDBC URL string, and packaged together with the frontend inside a single deployable container (supporting both JVM fast-jar and GraalVM native binary images).

## Outcome

A smartphone user traveling on the autobahn can record a shoe sighting with a single tap without GPS lag, protected against accidental entries by an immediate confirmation modal with Undo, and view all recorded shoes indefinitely with their own shoes visually distinguished on an interactive map and list view served from a single Quarkus container.

## Requirements

- R1: One-tap button on mobile web interface records shoe spotting with current timestamp and hot device geolocation coordinates.
- R2: First-time prompt asks for user name and associates it with all subsequent log entries, serving as both unique user identifier and display name for MVP (name collisions are tolerated).
- R4: List view displays recorded shoe sightings with timestamp, location coordinates, and recorder name, with distinct visual indicator for user's own shoes and a toggle to hide other users' shoes.
- R5: Map view displays interactive geographical markers for recorded shoe sightings indefinitely, using a distinct marker color for user's own shoes and an option to toggle visibility of other users' shoes.
- R6: Backend exposes a lean REST API under `/api/v1/sightings` for creating and querying sightings.
- R7: Interface enforces a mandatory cooldown period (e.g., 3-5 seconds) after logging a sighting to prevent accidental duplicate taps.
- R8: Spot screen displays a live map in the upper half of the screen showing current user position centered and updating in real-time, with the spot button positioned in the lower half.
- R9: Sighting confirmation modal provides an immediate Undo action allowing the user to cancel and remove an accidental sighting via DELETE /api/v1/sightings/{id}.
- R18: Catch-all RFC 7807 `ExceptionMapper<Throwable>` returns 500 `application/problem+json` with unique UUID `error_id`, logging stack traces internally and returning sanitized details to clients.
- R19: `SightingRepository` validates database connectivity fail-fast during `@PostConstruct` (`SELECT 1`) at startup.
- R20: Database lock contention errors translate into RFC 7807 `503 Service Unavailable` with `Retry-After: 1` header.
- R21: Quarkus SmallRye Health exposes `/q/health/live` and `/q/health/ready` endpoints verifying application liveness and database connectivity.
- R22: Continuous Integration workflow executes dedicated Spotless formatting checks, Closure Compiler JavaScript linting, and container build verification on every pull request and push to main.
- R23: Embedded H2 database (`io.quarkus:quarkus-jdbc-h2`) replaces `sqlite-jdbc` with native `TIMESTAMP(3)` Flyway DDL, removing `writeLock` serialization from `SightingRepository`.
- R24: Unified `DatabaseContentionExceptionMapper` replaces `SQLiteExceptionMapper`, translating ANSI SQLState contention codes (`40001`, `40P01`, `57P01`, `HYT00`) and vendor lock-timeout codes into RFC 7807 `503 Service Unavailable` with `Retry-After: 1` header across all database kinds.
- R25: `DatabaseHealthCheck` dynamically reports the connected database product name (`connection.metaData.databaseProductName`) in `/q/health/ready`.
- R26: Unified single-JDBC-URL datasource configuration bundles both H2 and MariaDB/MySQL drivers internally and switches automatically at startup based on the driver prefix in `QUARKUS_DATASOURCE_JDBC_URL` (`jdbc:h2:file:...` vs `jdbc:mariadb://...`).
- R27: Quarkus GraalVM native image compilation builds a standalone Linux binary using in-container Mandrel tooling with `src/main/docker/Dockerfile.native` achieving sub-50ms cold starts.
- R28: Spot button removes the 3-second countdown cooldown ring in favor of the immediate Sighting Confirmation Modal barrier and in-flight request lock, returning the Spot button to its ready state immediately upon modal dismissal.
- R29: Top header bar provides a Light and Dark Mode Theme Switcher (`#themeToggleBtn`) that auto-detects OS preference via `window.matchMedia('(prefers-color-scheme: dark)')`, persists manual selection in `localStorage.theme` (`'light'` or `'dark'`), applies `data-theme` on the document root with light-mode CSS tokens, and renders natural non-inverted map tiles in light mode.
- R30: Multi-language localization (`i18n.js`) supports runtime switching between English (`en`) and German (`de`) from the top bar (`#langToggleBtn`), auto-detects browser language (`navigator.language`), persists selection in `localStorage.lang`, and localizes UI strings including authentic Autobahn terminology (`"SHOE SPOTTED!"` -> `"Schuh!"` on the Spot button and `"SCHUH!"` in the confirmation modal).
- R31: Live tracking HUD mini-map (`#spotMiniMap`) fetches and renders nearby recorded shoe sightings as compact dot markers (Signal Blue `#3B82F6` for the active user's shoes, Slate `#94A3B8` for others) alongside the live GPS dot, updating reactively on sighting creation, deletion, and user change events.
- R32: Full interactive map (`#fullMap`) dynamically clusters nearby sighting markers at lower zoom levels (`zoom < 15`) into circular count badges (styled in Signal Blue `#3B82F6` when the cluster includes at least one of the active user's sightings, or Slate `#475569` otherwise), zooms/expands clusters on click, renders individual pins at street/highway zoom (`zoom >= 15`), and recalculates clusters when the `"My Shoes"` filter toggles or sightings change.
- R33: Sighting entries in the List view (`#sightingsList`) include a `"View on Map"` action button that switches to the `[Map]` tab, automatically switches the filter to `"All Shoes"` if `"My Shoes"` is active and the target sighting belongs to another user, smoothly centers and zooms the map (`zoom 16`) on the sighting's coordinates, and opens its marker popup.
- R34: Interface displays reactive Total and Personal sighting counters (e.g., `"12 Total | 4 Mine"` in English / `"12 Gesamt | 4 Meine"` in German) across views, recalculating immediately on sighting creation, undo/deletion, and username changes without requiring a page reload.
- R35: Static web assets consume Leaflet via the `org.mvnpm:leaflet:1.9.4` Maven dependency in `build.gradle.kts` (tracked by Dependabot) with local high-DPI retina marker assets (`marker-icon-2x.png`) and an offline tile fallback indicator when map tiles fail to load, and serve a dedicated sneaker favicon (`/favicon.svg` and `/favicon.ico`) declared in `index.html`.
- R36: Local development, Gradle Java toolchain (`JavaLanguageVersion.of(25)`), `sourceCompatibility`/`targetCompatibility` (`JavaVersion.VERSION_25`), Kotlin `JvmTarget.JVM_25`, GitHub Actions CI (`java-version: '25'`), and `README.md` prerequisites target JDK 25 with zero Java 21 fallbacks.
- R37: GitHub Actions CI workflow (`.github/workflows/ci.yml`) runs on `actions/checkout@v5`, `actions/setup-java@v5`, and `gradle/actions/wrapper-validation@v5` targeting Node.js 24 natively with zero `@v4` references.
- R38: Docker Hub CI release job builds and publishes only the `linux/amd64` container platform without `linux/arm64` cross-compilation or `docker/setup-qemu-action` emulation overhead.
- R39: Default container image in `docker-compose.yml` (`${DOCKERHUB_IMAGE:-mithomas/shoe-tracker:latest}`) and container deployment examples in `README.md` reference `mithomas/shoe-tracker` instead of placeholder `mitch/shoe-tracker`.
- R40: HUD mini-map location-denied overlay (`#miniMapDeniedOverlay` in `index.html` and `spot.deniedOverlay` in `i18n.js`) displays concise `⚠️ Location Access Denied` (EN) and `⚠️ Standortzugriff verweigert` (DE) without the redundant ` — Germany Overview` / ` — Deutschland-Übersicht` suffix.
- R41: Geolocation unavailable/denied state (`no-gps`), DOM/CSS/i18n identifiers (`#miniMapNoGpsOverlay`, `.gps-pill.no-gps`, `gps.noGps`, `spot.noGps`, `spot.noGpsOverlay`), and UI labels across the header GPS pill, giant Spot button, and HUD mini-map overlay use concise `No GPS` / `⚠️ No GPS` (EN) and `Kein GPS` / `⚠️ Kein GPS` (DE) instead of `GPS Denied` / `GPS Verweigert` / `Location Access Denied`.
- R42: Quarkus Flyway runtime bundles `io.quarkus:quarkus-flyway-mysql` (`org.flywaydb:flyway-mysql`) so Flyway 12 supports MariaDB (including MariaDB 11.8) and MySQL alongside H2; JVM container and Gradle test runtimes pass `--enable-native-access=ALL-UNNAMED` to eliminate JDK 25 JNA restricted `System::load` warnings; and a `@JdbcDriver("other")` `AgroalConnectionConfigurer` bean (`MultiTargetAgroalConnectionConfigurer`) registers `MySQLExceptionSorter` so Agroal detects fatal connection exceptions and suppresses the `database kind: other` startup warning.
- R43: Sighting Confirmation Modal (`#sightingModal`) renders as a centered floating modal card with its primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) first as a full-size `210px` circular button matching the size, shape, and orange color palette of the main Spot button (`#btnSpotGiant`), followed below by the captured sighting details box (`Time`, `Location`, `Spotted By`) and stacked secondary action buttons (`View on Map` and `Undo`).
- R44: Sighting Confirmation Modal primary accept button (`#btnSightingAwesome`) includes a 20-second automatic dismissal timer displayed as an SVG circular progress outline ring around the `210px` button perimeter that drains clockwise (`100% -> 0%`) over 20 seconds, automatically closing the modal after 20 seconds and immediately cancelling/resetting the timer and animation on manual `Awesome!`, `View on Map`, or `Undo` actions or modal re-open (keeping the modal open if `Undo` fails).
- R45: Giant `210px` circular action buttons (`#btnSpotGiant` / `.btn-spot-giant` and `#btnSightingAwesome`) use a toned-down orange/amber gradient and reduced outer `box-shadow` glow intensity applied identically across both Dark and Light themes for comfortable cabin use while preserving WCAG AA contrast (`>= 4.5:1`) and clear visual distinction from muted disabled states (`Waiting for GPS...` / `No GPS`).
- R46: Dedicated `/js/wakelock.js` module requests an active Screen Wake Lock (`navigator.wakeLock.request('screen')`) on page load, automatically re-acquires the wake lock when `document.visibilityState` returns to `'visible'`, includes a one-time `pointerdown`/`click` fallback if initial load requires a user gesture, and fails silently when unsupported or rejected by OS battery-saver policies.
- R47: HUD mini-map (`#spotMiniMap` in `.mini-map-container`) renders without the floating `"HUD Radar"` / `"HUD-Radar"` badge (`#miniMapBadge`, `.mini-map-badge`, `spot.hudRadar`), leaving the upper-left corner of the mini-map unobstructed while preserving `#miniMapNoGpsOverlay`.
- R48: Full interactive map view (`#viewMap` / `#fullMapContainer`) removes the floating empty-state banners (`#fullMapEmpty` and `#fullMapEmptyOwn` in `index.html`, `fullmap.js`, and `i18n.js`) so the map canvas remains unobstructed when zero sightings exist or match the active `"My Shoes"` filter, while preserving empty-state cards (`#listEmptyCard`, `#listEmptyOwnCard`) in the List view.
- R49: Simulated GPS mode in `GeolocationWatcher` (`geo.js`) supports compact custom coordinate URL query parameters (`?mockGps=<lat>,<lon>`), validating finite WGS84 bounds (`lat` in `[-90, 90]`, `lon` in `[-180, 180]`), holding the simulated GPS position stationary at those exact coordinates without persisting to `localStorage` (falling back to the default moving Frankfurt A5 simulation on `?mockGps=true` or invalid coordinates), and clearing persisted `localStorage` mock state when `?mockGps=false` is passed.
- R50: `README.md` documents the `?mockGps` simulation mode under local development and manual testing, covering default moving simulation (`?mockGps=true`), non-persisted stationary custom coordinates (`?mockGps=<lat>,<lon>`), and exiting persisted mock mode (`?mockGps=false`).

## Constraints

- C1: Two-tier architecture with frontend separated from backend and shielded by a lean API.
- C2: Backend implemented in Kotlin using the Quarkus framework.
- C3: Persistence in embedded H2 file database (`MODE=MySQL`) by default, switchable at startup to external MariaDB/MySQL via a single JDBC URL environment variable (`QUARKUS_DATASOURCE_JDBC_URL`).
- C4: Packaged and hosted as a single container serving both Quarkus backend and frontend assets.
- C5: Mobile-first responsive UI optimized for quick single-tap action.
- C6: Spot screen mini-map must operate in locked HUD radar mode (touch dragging, pinching, and keyboard navigation strictly disabled; fixed zoom ~15 auto-centered on GPS position) with height constrained (max 35vh) to guarantee the 210px Spot Button never scrolls off-screen.

## Conventions

- GPS Coordinate Notation: All user-facing coordinates must be rendered in standard navigational notation: `N XX° MM,MMM' / E XXX° MM,MMM'` (comma decimal separator, 2-digit lat / 3-digit lon degrees, minute zero-padding, and 60.000-minute mathematical rollover protection). In confirmation modal, coordinates render right-aligned across two lines.

## Done when

- First-time access prompts for user name and remembers it in local browser storage across sessions.
- Browser actively monitors geolocation in the background so tapping the main button records coordinates with zero acquisition lag.
- Mandatory cooldown engages immediately after tap, blocking duplicate entries for the duration.
- Sighting record persists to the configured database (embedded H2 file by default or external MariaDB/MySQL) with native millisecond timestamp, coordinates, and user name.
- Sighting confirmation modal allows immediate cancellation of an accidental sighting via an Undo button, deleting it from the database.
- Spot screen displays a live current-location map in the upper half of the viewport above the spot button.
- List view renders saved sightings with distinct styling for own shoes and a toggle to hide others.
- Map view renders markers with distinct color for own shoes and an option to toggle visibility of other users' shoes.
- The Quarkus container builds (both JVM fast-jar and GraalVM native image), starts, and serves both the frontend web app and the backend API with persistent database storage.

## Boundaries

- Non-goal: Dedicated login, user profiles, and changing usernames (deferred post-MVP).
- Non-goal: Real-time map pushes via Server-Sent Events (SSE) (deferred post-MVP).
- Non-goal: Offline logging / local queuing (deferred post-MVP).
- Non-goal: Batch upload endpoint for offline sync (deferred post-MVP).
- Non-goal: Geospatial viewport bounding-box filtering (deferred post-MVP).
- Non-goal: Compass heading / bearing tracking (deferred post-MVP).
- Non-goal: Dedicated authentication or LDAP integration (deferred post-MVP).
- Non-goal: Shoe attributes such as type and color classification (deferred post-MVP; architectural hint: model as JSON attributes container).
- Non-goal: Sighting expiration / TTL (sightings persist indefinitely).
- Non-goal: Safety / driver disclaimer modal dialogs on launch.
- Non-goal: Native mobile app store packaging (iOS / Android); responsive web application only.
- Non-goal: Turn-by-turn navigation or route tracing along the highway.
- Non-goal: Image or photo uploads of shoes.

## References

- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md

## Notes

- Assumption: Map visualization uses OpenStreetMap tiles with Leaflet.js to avoid external API key dependencies.
- Assumption: Device location is maintained via browser `navigator.geolocation.watchPosition` to guarantee instantaneous coordinate availability at speed.
- Decision: 2026-10-28: Geolocation continuously watched in background (`watchPosition`) to eliminate GPS lag at autobahn speeds.
- Decision: 2026-10-28: Mandatory cooldown period enforced after button tap to prevent double-tap and accidental logging.
- Decision: 2026-10-28: Author name serves as both unique user identifier and display name for MVP (user_id and user_name are identical); dedicated login, profiles, and name changes deferred to backlog.
- Decision: 2026-10-28: Shoe sightings remain on map and list indefinitely (no TTL or cleanup purging).
- Decision: 2026-10-28: No safety or driver warning confirmation modal.
- Decision: 2026-10-28: API versioned in URI path as `/api/v1/sightings`; collection responses wrapped in `{ items, total, limit, offset }`.
- Decision: 2026-10-28: Offline support, batch uploads, bounding-box queries, real-time SSE streaming, compass heading, LDAP/profile login, and shoe types/colors deferred as future backlog ideas.
- Decision: 2026-10-28: Views display all sightings with user's own shoes visually distinguished (distinct color on map, distinct styling in list) and provide a toggle to filter out other users' shoes.
