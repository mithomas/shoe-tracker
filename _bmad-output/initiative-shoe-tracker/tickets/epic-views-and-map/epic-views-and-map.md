---
type: epic
title: "Sighting Exploration (Map & List Views)"
parent: "initiative-shoe-tracker"
covers: ["R4", "R5"]
sources: []
companions: []
after: ["epic-core-capture"]
refined: false
risk: low
---

# Sighting Exploration (Map & List Views)

## Description

Adds the map and chronological list views to the mobile web interface. Visualizes all recorded shoe sightings indefinitely on an interactive OpenStreetMap/Leaflet map with custom marker styling, highlights the current user's own shoes in a distinct color, displays coordinates and timestamps in a responsive list, and provides a filter toggle to hide other users' shoes.

## Outcome

A user can explore all recorded shoe sightings across the autobahn network on an interactive map and list, clearly distinguishing their own sightings from those of other users.

## Done when

- List view displays chronological history of sightings with timestamp, location coordinates, and recorder name.
- Sightings logged by the active user have a distinct visual badge/style in the list.
- Interactive map renders markers at geographical coordinates for all recorded sightings using Leaflet and OpenStreetMap.
- Sighting markers for the user's own shoes use a distinct color.
- Filter toggle lets the user hide sightings from other users on both map and list views.
- Tapping a map marker displays sighting details (timestamp, recorder name, coordinates).

## Boundaries

- Core capture, database schema, and single-tap button belong to `epic-core-capture`.
- Native routing and highway tracing are out of scope.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md

## Notes

- Decision: 2026-10-28: Views display all sightings with user's own shoes visually distinguished (distinct color on map, distinct styling in list) and provide a toggle to filter out other users' shoes.
- Decision: 2026-10-28: Map visualization uses OpenStreetMap tiles with Leaflet.js to avoid external API key dependencies.
