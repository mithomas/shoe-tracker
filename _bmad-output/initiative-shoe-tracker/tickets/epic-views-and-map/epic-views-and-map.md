---
type: epic
title: "Sighting Exploration (Map & List Views)"
parent: "initiative-shoe-tracker"
covers: ["R4", "R5", "R8"]
sources: []
companions: []
after: ["epic-core-capture"]
refined: true
risk: low
---

# Sighting Exploration (Map & List Views)

## Description

Adds comprehensive map visualization and chronological listing to Shoe Tracker. Renders a live tracking mini-map in the top half of the main Spot screen centered on the user's current GPS position moving in real-time above the spot button. In addition, provides dedicated full Map and List exploration tabs with OpenStreetMap/Leaflet integration, highlighting the user's own shoes in vibrant signal blue and offering a filter toggle to hide other users' shoes.

## Outcome

A user traveling on the autobahn can view their live location in real time directly above the spot button, and seamlessly switch to dedicated map and list tabs to explore all recorded sightings with clear visual differentiation for their own shoes.

## Requirements

- R4: List view displays recorded shoe sightings with timestamp, location coordinates, and recorder name, with distinct visual indicator for user's own shoes and a toggle to hide other users' shoes (parent R4).
- R5: Map view displays interactive geographical markers for recorded shoe sightings indefinitely, using a distinct marker color for user's own shoes and an option to toggle visibility of other users' shoes (parent R5).
- R8: Spot screen displays a live map in the upper half of the screen showing current user position centered and updating in real-time, with the spot button positioned in the lower half (parent R8).

## Done when

- Spot screen splits into a top half live tracking map (centered on current GPS fix with a blue location marker) and bottom half spot action button.
- Dedicated Map tab renders an interactive Leaflet/OSM map displaying all sightings across the highway network.
- Sighting markers for the user's own shoes render in signal blue (`#3B82F6`), while other users' shoes render in neutral slate (`#94A3B8`).
- Filter toggle control ("All Shoes" / "My Shoes") hides sightings from other users on both map and list views.
- Tapping a map marker opens a popup displaying recorder name, timestamp, and formatted coordinates.
- List tab renders chronological sightings cards showing recorder name, local time, and formatted coordinates.

## Boundaries

- Core capture, database schema, and single-tap button belong to `epic-core-capture`.
- Native highway routing and polyline route tracing are out of scope.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md

## Notes

- Decision: 2026-10-28: Views display all sightings with user's own shoes visually distinguished (distinct color on map, distinct styling in list) and provide a toggle to filter out other users' shoes.
- Decision: 2026-10-28: Map visualization uses OpenStreetMap tiles with Leaflet.js to avoid external API key dependencies.
- Decision: 2026-10-28: Spot screen layout allocates upper half to live location map and lower half to 210px Spot Button.
- Decision: 2026-10-28: Mini-map HUD mode has touch dragging completely disabled (strictly locked tracking radar) and auto-centers at zoom 15 with smooth position transitions. Proportional height `min(35vh, 260px)` guarantees the 210px Spot Button never scrolls.
- Decision: 2026-10-28: Tab lifecycle pauses mini-map tile requests when Spot tab is inactive; invokes `invalidateSize()` immediately upon tab switch.
- Decision: 2026-10-28: Full map view is lazy-initialized on first click of the Map tab to minimize mobile memory footprint.
- Decision: 2026-10-28: New sightings dispatch `sighting-added` custom events for optimistic insertion into Map and List views.
- Decision: 2026-10-28: Name changes in onboarding/user-chip dispatch `user-changed` custom events to immediately update "My Shoes" filtering.
- Decision: 2026-10-28: When GPS is denied, mini-map gracefully defaults to central Autobahn junction (50.8°N, 9.7°E, zoom 6) with warning overlay.
