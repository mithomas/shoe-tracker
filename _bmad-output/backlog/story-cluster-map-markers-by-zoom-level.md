---
type: story
title: "Cluster Nearby Map Sightings by Zoom Level with Count Badges"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Cluster Nearby Map Sightings by Zoom Level with Count Badges

## Description

Collapses multiple nearby shoe sightings on the map into grouped cluster bubbles depending on the current zoom level, displaying the number of collapsed findings inside each bubble. Zooming in or tapping a cluster expands into individual shoe markers.

## Background & Rationale

As more sightings are logged along congested highway sections and interchanges, rendering individual markers at low zoom levels causes visual clutter and overlapping pins. Grouping nearby sightings into clean count bubbles keeps the map readable at state and national zoom levels while clearly indicating high-density spotting hotspots.

## Acceptance Criteria

- Integrate map marker clustering for Leaflet (e.g., Leaflet.markercluster or lightweight zoom-distance grouping).
- At lower zoom levels (zoomed out), nearby markers collapse into a circular cluster bubble showing the count of grouped sightings.
- If a cluster contains sightings from the active user, the cluster styling visually highlights this (e.g. blue border or indicator).
- Clicking or tapping a cluster zooms the map to fit the cluster bounds and expands the sightings.
- Filtering with the "My Shoes" toggle correctly recalculates clusters to only include own sightings.
- Automated tests verify cluster generation and marker expansion at varying zoom thresholds.
