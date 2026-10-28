---
title: 'Full interactive map view with distinct sighting markers'
type: 'feature'
ticket: '2'
created: '2026-10-28'
status: 'draft'
baseline_revision: ''
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: []
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md'
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-views-and-map/epic-views-and-map.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Highway travelers need to explore roadside shoe sightings across the highway network on an interactive map, with clear visual differentiation between their own sightings and those logged by other travelers.

**Approach:** Build a dedicated full-viewport Leaflet map in the `[Map]` tab that lazy-initializes on first view, loads all sightings from `GET /api/v1/sightings?limit=500`, renders distinct markers (Signal Blue `#3B82F6` for user's own shoes vs Neutral Slate `#94A3B8` for others), displays detail popups with formatted coordinates and local timestamps, and listens for live event updates.

## Boundaries & Constraints

**Always:**
- Lazy-initialize `#fullMap` only when the `[Map]` tab is first activated to preserve mobile device RAM.
- Use Leaflet 1.9.4 with local vendored assets and OpenStreetMap tiles styled with dark slate navigation filter.
- Markers for current user (`localStorage.user_id`) must render in Signal Blue (`#3B82F6`).
- Markers for other users must render in Neutral Slate (`#94A3B8`).
- Clicking a marker opens a popup showing recorder name, local time, and formatted coordinates (`N XX° MM,MMM' / E XXX° MM,MMM'`).
- Call `invalidateSize()` immediately whenever the `[Map]` tab becomes active to prevent gray-box rendering anomalies.
- Listen for `sighting-added` and `sighting-deleted` custom events to update map markers optimistically.
- Listen for `user-changed` custom events to re-color markers when the user changes their name in onboarding.

**Never:**
- Do not make external CDN network requests for map libraries.
- Do not freeze the mobile UI when loading hundreds of sightings.
- Filter toggle ("All Shoes" vs "My Shoes") belongs to Story 2.4.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| First Click on [Map] Tab | User taps `[Map]` tab | Map container initializes; fetches `/api/v1/sightings`; fits bounds or centers on Germany | Shows loading state; handles empty sightings gracefully |
| Own Sighting vs Other | Sightings loaded from API | Matching `user_id` pins render in Signal Blue; non-matching render in Slate | Compares case-insensitively / trimmed |
| Marker Click | User taps a shoe pin | Popup opens showing author, local timestamp, and coordinates | Popup stays inside viewport bounds |
| New Sighting Logged | `sighting-added` event dispatched | New Signal Blue pin appears immediately on map without full page reload | Added directly to Leaflet layer group |
| Sighting Undone | `sighting-deleted` event dispatched | Pin with matching ID is removed from Leaflet layer group | Cleanly removed from map layer |
| Name Change in Onboarding | `user-changed` event dispatched | Marker colors immediately recalculate for new active user | Re-renders layer icons |

</frozen-after-approval>

## Code Map

- `src/main/resources/META-INF/resources/js/fullmap.js` -- Full interactive map manager (lazy initialization, marker rendering, popups, event bus)
- `src/main/resources/META-INF/resources/css/style.css` -- Full map container layout, marker pin styles, popup card styling
- `src/main/resources/META-INF/resources/index.html` -- `#viewMap` pane markup containing `#fullMapContainer` and script import
- `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Automated tests verifying full map script, container, and marker classes

## Tasks & Acceptance

**Execution:**
- [ ] Full Map Controller -- Implement `fullmap.js` with lazy initialization, `GET /api/v1/sightings?limit=500` fetch, Signal Blue / Slate marker rendering, and popup cards.
- [ ] Event Bus Integration -- Wire `fullmap.js` to `sighting-added`, `sighting-deleted`, and `user-changed` DOM CustomEvents.
- [ ] Full Map Styling -- Add CSS in `style.css` for `#fullMapContainer`, custom SVG pin icons, and dark-theme Leaflet popups.
- [ ] View Markup -- Update `#viewMap` in `index.html` with `#fullMapContainer` and load `js/fullmap.js`.
- [ ] Automated Test Suite -- Update `FrontendAssetTest.kt` verifying full map assets and marker elements, and run `./gradlew test`.

**Acceptance Criteria:**
- Given the `[Map]` tab is activated, then the full interactive Leaflet map initializes and displays all sightings from the API.
- Given sightings from the database, then markers for the active user render in Signal Blue (`#3B82F6`) and markers for others render in Slate (`#94A3B8`).
- Given a tap on a marker, then an interactive popup displays the recorder name, local time, and formatted coordinates.
- Given a new sighting is logged on the Spot screen, then it immediately appears on the Map view without a page reload.
- Given `./gradlew test`, then all automated integration and asset tests pass cleanly.

## Implementation Notes

## Plan Change Log

## Review Triage Log

## Design Notes

- Custom Leaflet DivIcons with SVG shoe pins provide crisp rendering on high-DPI retina mobile screens without bitmap pixelation.

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `curl -s http://localhost:8080/js/fullmap.js` -- expected: HTTP 200 with full map controller code.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing `#fullMapContainer`.
