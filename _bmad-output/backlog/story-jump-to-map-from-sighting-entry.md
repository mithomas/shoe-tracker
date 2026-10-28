---
type: story
title: "Jump to Map View from Sighting Entry"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Jump to Map View from Sighting Entry

## Description

Adds a "View on Map" action to each sighting entry in the historical List view (and recent sightings feed), allowing travelers to tap any sighting card to immediately transition to the Map tab, smoothly pan to its exact coordinates, and open its detail popup.

## Background & Rationale

While browsing the chronological list of sightings, passengers often want to inspect where along the Autobahn a specific shoe was located (e.g., near which interchange, bridge, or rest stop). Providing an immediate 1-tap shortcut from a list card directly into the focused map view bridges the textual history with geographic exploration.

## Acceptance Criteria

- Each sighting card in the List tab includes a clear, accessible action button (e.g. `📍 View on Map`).
- Tapping "View on Map" switches the active view to the `[Map]` tab.
- The full Leaflet map smoothly centers (`panTo` or `flyTo`) on the target coordinates at highway zoom level 16.
- The corresponding sighting marker is selected and its detail popup automatically opens.
- If the target marker was filtered out by an active "My Shoes" filter, the filter temporarily reveals the selected shoe or alerts the user.
