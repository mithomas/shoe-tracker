---
title: 'Cluster Nearby Map Sightings by Zoom Level with Count Badges'
type: 'feature'
ticket: 6
created: '2026-10-29'
status: 'built'
baseline_revision: '2e306b83d6efb80511760f3dabf838a9cf8f1543'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 1
context: []
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** Overlapping or nearby shoe sightings on the full interactive map (`#fullMap`) clutter the viewport at lower zoom levels, making it difficult to read dense highway corridors.

**Approach:**
1. Enhance `fullmap.js` (`#fullMap`) with zoom-adaptive spatial clustering (`clusterSightings`) that collapses nearby sightings when `zoom < 15` into circular count badges (`.shoe-cluster-badge`), colored Signal Blue (`#3B82F6`, `.shoe-cluster-own`) if any sighting in the cluster belongs to the active user and Slate (`#475569`, `.shoe-cluster-other`) otherwise.
2. On cluster click, compute `clusterBounds = L.latLngBounds(latLngs)`; when tightly co-located points span `< 0.0005` degrees in both latitude and longitude, zoom directly via `setView(clusterBounds.getCenter(), Math.max(currentZoom + 2, 15))`, otherwise call `fitBounds(clusterBounds, { padding: [40, 40], maxZoom: 15 })` so clicking a cluster always expands toward individual pins (`zoom >= 15`).
3. Render individual shoe pins (`createMarker`) at `zoom >= 15` (and for single-sighting groups at any zoom), and recalculate clusters on `zoomend`, `sightings-loaded`, `sighting-added`, `sighting-deleted`, `user-changed`, `filter-changed`, and `lang-changed`.

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route.
- Added `clusterSightings` and `createClusterMarker` to `FullMapManager` in `src/main/resources/META-INF/resources/js/fullmap.js`, plus `zoomend` and `sightings-loaded` listeners.
- Added `.shoe-cluster-container`, `.shoe-cluster-badge`, `.shoe-cluster-own`, and `.shoe-cluster-other` styles in `src/main/resources/META-INF/resources/css/style.css`.
- Added cluster tooltip translation key `map.clusterTitle` in `src/main/resources/META-INF/resources/js/i18n.js`.
- Added verification test `testMapZoomClusteringWithCountBadges` in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

- Iteration 1 (`quick` lens): 0 findings.

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
