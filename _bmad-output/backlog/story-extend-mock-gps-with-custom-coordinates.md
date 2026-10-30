---
type: story
title: "Extend Mock GPS Mode with Optional Custom Coordinate Parameters"
parent: ""
covers: []
sources: []
companions: []
after: []
refined: true
hitl: false
risk: low
---

# Extend Mock GPS Mode with Optional Custom Coordinate Parameters

## Description

Extends the built-in simulated GPS mode so developers and testers can supply optional custom static coordinates via the compact `?mockGps=<lat>,<lon>` URL query parameter (without persisting to `localStorage` or moving the simulated position) and cleanly exit persisted mock mode via `?mockGps=false`.

## Requirements

1. **Custom Static Coordinates via `?mockGps=<lat>,<lon>`**
   **Given** the user opens Shoe Tracker with compact custom coordinates in the URL query string (`?mockGps=<lat>,<lon>`)
   **When** the geolocation watcher starts
   **Then** if `<lat>` (`-90` to `90`) and `<lon>` (`-180` to `180`) are valid finite numbers, mock GPS mode initializes at those exact coordinates, remains stationary (does not increment coordinates on subsequent ticks), and does not persist to `localStorage`
   **And** if `?mockGps=true` is passed without coordinates (or with out-of-range/non-numeric values), mock mode uses the default moving Frankfurt A5 simulation (`50.110924, 8.682127` with simulated drift)

2. **Disable Persisted Mock Mode via `?mockGps=false`**
   **Given** mock GPS mode (`?mockGps=true`) was previously persisted in `localStorage`
   **When** the user loads the page with `?mockGps=false`
   **Then** `localStorage` mock GPS state is cleared and standard `navigator.geolocation.watchPosition` tracking starts

3. **Optional Static Coordinate Arguments on `enableMockMode(lat, lon)`**
   **Given** `window.ShoeTracker.geoWatcher.enableMockMode(lat, lon)` is called with custom coordinates
   **When** valid numeric `lat` and `lon` arguments are provided
   **Then** the simulated GPS watcher locks onto those exact static coordinates without simulated movement and without writing to `localStorage`

## Boundaries

- Must not change: Default `?mockGps=true` behavior (moving Frankfurt A5 simulation) when no custom coordinates are supplied, or standard `navigator.geolocation` behavior when mock mode is inactive.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements

## Notes

- Decision: 2026-10-30: Use only compact `?mockGps=<lat>,<lon>` syntax plus `?mockGps=false` (alongside default `?mockGps=true`). When custom coordinates are supplied, do not persist to `localStorage` and keep the simulated position stationary (do not move).
