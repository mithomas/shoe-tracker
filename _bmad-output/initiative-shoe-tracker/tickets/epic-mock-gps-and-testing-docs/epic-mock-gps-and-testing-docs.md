---
type: epic
title: "Simulated GPS Custom Coordinates & Testing Documentation"
parent: "initiative-shoe-tracker"
covers: ["R49", "R50"]
sources: ["_bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md"]
companions: ["_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md", "_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md"]
after: []
refined: true
risk: low
---

# Simulated GPS Custom Coordinates & Testing Documentation

## Description

Extends the built-in simulated GPS mode in `GeolocationWatcher` (`geo.js`) so developers and testers can pass optional stationary custom coordinates via the compact `?mockGps=<lat>,<lon>` URL query parameter (without persisting to `localStorage` or drifting position) and cleanly exit persisted mock mode via `?mockGps=false`. Documents the `?mockGps` simulation modes (`?mockGps=true`, `?mockGps=<lat>,<lon>`, and `?mockGps=false`) in `README.md` under local development and manual testing.

## Outcome

Developers and users testing Shoe Tracker locally when OS or browser location services are blocked can either simulate moving highway travel (`?mockGps=true`), pin a stationary test location at arbitrary valid coordinates (`?mockGps=<lat>,<lon>`) without polluting `localStorage`, or return to real GPS (`?mockGps=false`), with clear documentation in `README.md`.

## Requirements

- R49: Simulated GPS mode in `GeolocationWatcher` (`geo.js`) supports compact custom coordinate URL query parameters (`?mockGps=<lat>,<lon>`), validating finite WGS84 bounds (`lat` in `[-90, 90]`, `lon` in `[-180, 180]`), holding the simulated GPS position stationary at those exact coordinates without persisting to `localStorage` (falling back to the default moving Frankfurt A5 simulation on `?mockGps=true` or invalid coordinates), and clearing persisted `localStorage` mock state when `?mockGps=false` is passed.
- R50: `README.md` documents the `?mockGps` simulation mode under local development and manual testing, covering default moving simulation (`?mockGps=true`), non-persisted stationary custom coordinates (`?mockGps=<lat>,<lon>`), and exiting persisted mock mode (`?mockGps=false`).

## Done when

- `GeolocationWatcher` in `src/main/resources/META-INF/resources/js/geo.js` parses `?mockGps=<lat>,<lon>` (with WGS84 range validation), locks onto those static coordinates without simulated drift and without writing to `localStorage`, preserves moving Frankfurt A5 simulation for `?mockGps=true`, and clears `localStorage` mock state when `?mockGps=false` is passed.
- `README.md` documents `?mockGps=true`, `?mockGps=<lat>,<lon>`, and `?mockGps=false` for local testing without OS location services.
- `FrontendAssetTest.kt` and `DevOpsAssetTest.kt` verify the custom coordinate mock GPS logic and `README.md` documentation, and `./gradlew check test spotlessCheck lintJs` passes cleanly.

## Boundaries

- Must not change: Default `?mockGps=true` behavior (moving Frankfurt A5 simulation persisted in `localStorage`) or standard `navigator.geolocation.watchPosition` behavior when mock mode is inactive.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md

## Notes

- Decision: 2026-10-30: Story `9.1` is the tracer bullet implementing `?mockGps=<lat>,<lon>` and `?mockGps=false` in `geo.js`, followed by Story `9.2` documenting the behavior in `README.md`.
