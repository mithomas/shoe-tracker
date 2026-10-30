---
type: story
title: "Add Optional Map Rotation and Automatic Bearing Orientation to HUD and Full Map"
parent: ""
covers: []
sources: []
companions: []
after: []
refined: true
hitl: false
risk: low
---

# Add Optional Map Rotation and Automatic Bearing Orientation to HUD and Full Map

## Description

Adds optional map rotation support to both the Spot screen HUD mini-map and the full interactive Map screen, supporting manual rotation controls as well as an optional automatic heading-up mode driven by GPS movement bearing or device orientation.

## Requirements

1. **Map Bearing Rotation Support on Both Maps**
   **Given** the HUD mini-map or the full interactive map is rendered
   **When** map rotation is enabled or a non-zero bearing angle is applied
   **Then** the map tiles and marker layer rotate smoothly to match the target bearing without clipping tile edges inside the map viewport
   **And** marker icons, cluster count badges, and popups remain upright and readable

2. **Rotation / Compass Control and North-Up Reset**
   **Given** either the HUD mini-map or the full map is visible
   **When** the user interacts with the map rotation control
   **Then** the user can toggle between North-up (`0 deg`), manual rotation, and automatic heading-up tracking

3. **Automatic Bearing Orientation Mode**
   **Given** automatic bearing orientation is enabled and GPS telemetry is active
   **When** the vehicle moves along the highway and provides a valid GPS heading (`coords.heading`) or computed course-over-ground bearing between consecutive fixes
   **Then** the map rotates smoothly so the direction of travel points upward
   **And** when stationary or when heading data is unavailable, the map preserves its last valid heading or falls back cleanly to North-up without erratic spinning

## Boundaries

- Must not change: Single-container offline/vendored asset policy (any Leaflet rotation plugin such as `leaflet-rotate` or equivalent must be bundled locally without external runtime CDN dependencies).
- Must not change: Sighting marker clustering and popup opening behavior on the full map.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md

## Notes

- Decision: 2026-10-30: Map rotation uses a lightweight Leaflet rotation integration (such as `leaflet-rotate`) bundled in local static resources, with North-up as the default until toggled or auto-bearing is enabled.
