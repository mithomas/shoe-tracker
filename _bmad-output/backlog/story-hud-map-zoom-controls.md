---
type: story
title: "Add Compact +/- Zoom Controls to HUD Mini-Map"
parent: ""
covers: []
sources: []
companions: []
after: []
refined: true
hitl: false
risk: low
---

# Add Compact +/- Zoom Controls to HUD Mini-Map

## Description

Adds compact `+` and `-` zoom buttons inside the Spot screen's HUD mini-map so highway passengers can adjust the radar zoom level while keeping the map auto-centered on live GPS updates and locked against accidental touch panning or pinch-zooming.

## Requirements

1. **In-Map Compact Zoom Buttons**
   **Given** the Spot screen renders the HUD mini-map
   **When** the mini-map initializes
   **Then** small, unobtrusive `+` and `-` zoom controls are displayed inside the HUD mini-map container
   **And** tapping `+` increments the mini-map zoom level while tapping `-` decrements it within allowed Leaflet tile zoom bounds

2. **Persistence of Selected Zoom During Live GPS Updates**
   **Given** the user has adjusted the HUD mini-map zoom level via the `+` or `-` buttons while GPS is active
   **When** subsequent background GPS telemetry updates arrive
   **Then** the HUD mini-map pans smoothly to the new coordinates while preserving the user-selected active zoom level instead of resetting to zoom `15`

3. **Disabled States at Zoom Bounds**
   **Given** the HUD mini-map reaches its configured minimum or maximum zoom level
   **When** the user views or taps the corresponding `-` or `+` button
   **Then** the button reflects a disabled state and does not trigger out-of-range zoom transitions

## Boundaries

- Must not change: Locked HUD radar gesture restrictions (manual touch dragging, pinch-to-zoom, double-click zoom, scroll-wheel zoom, and keyboard panning remain disabled so the map stays centered on the live GPS marker).
- Must not change: HUD mini-map container height constraints that keep the 210px Spot button visible without scrolling.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md

## Notes

- Decision: 2026-10-30: Default active tracking zoom remains `15` on initial GPS lock until adjusted by the user via `+` / `-`.
