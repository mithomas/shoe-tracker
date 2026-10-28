---
type: epic
title: "Container Scaffold & Core Sighting Capture"
parent: "initiative-shoe-tracker"
covers: ["R1", "R2", "R6", "R7", "R9"]
sources: []
companions: []
after: []
refined: true
risk: low
status: "done"
---

# Container Scaffold & Core Sighting Capture

## Description

Sets up the single-container Quarkus + Kotlin application delivering the mobile web frontend and REST API backed by SQLite. Delivers the core single-tap shoe spotting experience: first-time user name onboarding, continuous background GPS monitoring via `navigator.geolocation.watchPosition` to eliminate latency, single-tap capture with cooldown protection, and persistent SQLite storage.

## Outcome

A smartphone user on the autobahn can open the web app, set their name, and record shoe sightings instantaneously with a single tap, persisted reliably into SQLite through a lean REST API.

## Requirements

- R1: One-tap button on mobile web interface records shoe spotting with current timestamp and hot device geolocation coordinates (parent R1).
- R2: First-time prompt asks for user name and associates it with all subsequent log entries, serving as both unique user identifier and display name for MVP (parent R2).
- R6: Backend exposes a lean REST API under `/api/v1/sightings` for creating and querying sightings (parent R6).
- R7: Interface enforces a mandatory cooldown period (e.g., 3-5 seconds) after logging a sighting to prevent accidental duplicate taps (parent R7).

## Constraints

- C1: Two-tier architecture with frontend separated from backend and shielded by a lean API.
- C2: Backend implemented in Kotlin using the Quarkus framework (JVM mode, Java 21, Gradle Kotlin DSL).
- C3: Persistence in SQLite database using direct JDBC and Flyway migrations in WAL mode.
- C4: Packaged and hosted as a single container serving both Quarkus backend and frontend assets.
- C5: Mobile-first responsive UI optimized for quick single-tap action per DESIGN.md and EXPERIENCE.md.

## Done when

- Quarkus project builds with Kotlin and runs as a container serving both backend endpoints and frontend static assets.
- SQLite database is initialized and stores sightings with timestamp, latitude, longitude, and author user name.
- First-time visit prompts for user name and persists it in browser localStorage as both identity and display name.
- Main mobile screen displays a large single-tap button.
- Browser actively watches position in the background so coordinates are hot upon tap.
- Tapping the button records sighting immediately, engages a 3–5s cooldown blocking duplicate taps, and displays a sighting confirmation modal with time and coordinates.

## Boundaries

- Map and historical list exploration belong to `epic-views-and-map`.
- Offline local queueing is out of scope.
- Dedicated user profiles and changing user names are out of scope (deferred to backlog).
- Sighting classification (colors, types) is out of scope.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md

## Notes

- Decision: 2026-10-28: Geolocation continuously watched in background (`watchPosition`) to eliminate GPS lag at autobahn speeds.
- Decision: 2026-10-28: Mandatory cooldown period enforced after button tap to prevent double-tap and accidental logging.
- Decision: 2026-10-28: Author name serves as both user identifier and display name for MVP; dedicated profiles and name changes deferred to backlog.
