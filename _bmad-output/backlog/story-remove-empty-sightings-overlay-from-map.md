---
type: story
title: "Remove 'No Shoes Yet' Empty Banner from Map View"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Remove 'No Shoes Yet' Empty Banner from Map View

## Description

Removes the floating "No shoes spotted yet" / "No shoes spotted by you yet" overlay banners from the full map view (`#fullMapEmpty` and `#fullMapEmptyOwn`), letting the map remain clean and unobstructed even when 0 sightings exist or when the "My Shoes" filter returns no items.

## Background & Rationale

On the full interactive map, the persistent empty banner pill at the top can obstruct map tiles and highway routes. A map with zero sightings is naturally self-explanatory without floating text banners blocking the view. The empty state messaging is still preserved in the dedicated chronological List view where empty card placeholders are contextually appropriate.

## Acceptance Criteria

- Remove `#fullMapEmpty` and `#fullMapEmptyOwn` banner elements or their display logic from `fullmap.js` and `index.html`.
- Map view remains fully interactive and unobstructed when no sightings match the current filter or when 0 sightings exist in the database.
- Existing empty states in the List view remain intact.
- Update automated frontend asset tests to reflect the removal of the map empty banner overlays.
