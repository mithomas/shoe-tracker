---
title: 'Chronological historical sightings list view'
type: 'feature'
ticket: '3'
created: '2026-10-28'
status: 'built'
baseline_revision: 'c353bf994c6d3cdee2a572cc31ce5fce505ab0e7'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md'
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-views-and-map/epic-views-and-map.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Highway travelers need a clear, scrollable chronological list of all recorded shoe sightings with distinct visual indicators for their own sightings, local timestamp formatting, and standard navigational coordinates.

**Approach:** Build the dedicated List view (`#listViewContainer`) in the `[List]` tab that queries `GET /api/v1/sightings?limit=500` in descending chronological order, styles user's own sightings with a Signal Blue accent border and "My Sighting" badge, displays formatted coordinates and local timestamps, and listens for live event updates.

## Boundaries & Constraints

**Always:**
- Display sightings in descending chronological order (`recorded_at DESC`).
- Highlight user's own sightings with `border-left: 4px solid var(--accent-blue)` and a distinct `"My Sighting"` badge.
- Render other users' sightings with neutral slate styling.
- Render coordinates in standard navigational notation (`N XX° MM,MMM' / E XXX° MM,MMM'`).
- Display local formatted timestamps with date and time (`Oct 28, 14:32:05`).
- Display empty-state card when 0 sightings exist (`"No shoes spotted yet! Tap the Spot button to be the first."`).
- Listen for `sighting-added` to prepend new sightings to the top of the list optimistically.
- Listen for `sighting-deleted` to remove sightings when undone.
- Listen for `user-changed` to dynamically update own vs other badges.

**Never:**
- Do not require full page reloads to show newly recorded sightings.
- Filter toggle ("All Shoes" vs "My Shoes") belongs to Story 2.4.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Load List Tab | User taps `[List]` tab | Fetches sightings, renders cards with latest at the top | Shows loading spinner; handles network errors gracefully |
| Own vs Other Sighting | Sightings rendered in list | Own sightings have blue left border & badge; others have neutral slate | Compares trimmed / case-insensitively |
| Empty Database | 0 sightings in API response | Displays engaging empty-state card with prompt to spot | Clean empty card |
| New Sighting Added | `sighting-added` event dispatched | New card prepended to top of list with smooth animation | Updated counter & DOM without full refresh |
| Sighting Undone | `sighting-deleted` event dispatched | Card with matching ID removed from list | Decrements counter |
| User Changes Name | `user-changed` event dispatched | Cards update badges and border colors live | Re-evaluates active user |

</frozen-after-approval>

## Code Map

- `src/main/resources/META-INF/resources/js/listview.js` -- Chronological list controller (fetching, card rendering, event bus listeners)
- `src/main/resources/META-INF/resources/css/style.css` -- Sighting card styles, own-shoe blue accent border, empty-state styling
- `src/main/resources/META-INF/resources/index.html` -- `#viewList` pane markup containing `#listViewContainer` and script import
- `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Automated tests verifying list view script, container, and card elements

## Tasks & Acceptance

**Execution:**
- [x] List View Controller -- Implement `listview.js` with `GET /api/v1/sightings?limit=500` fetch, chronological card rendering, and count badge.
- [x] Visual Differentiation -- Style user's own shoes with `border-left: 4px solid var(--accent-blue)` and `.badge-own`, and others with neutral borders.
- [x] Event Bus Integration -- Wire `listview.js` to `sighting-added`, `sighting-deleted`, and `user-changed` DOM CustomEvents.
- [x] View Markup -- Update `#viewList` in `index.html` with `#listViewContainer` and load `js/listview.js`.
- [x] Automated Test Suite -- Update `FrontendAssetTest.kt` verifying list view assets and card elements, and run `./gradlew test`.

**Acceptance Criteria:**
- Given the `[List]` tab is activated, then all sightings are rendered in descending chronological order.
- Given sightings in the list, then items matching the active user display a blue left accent border and `"My Sighting"` badge.
- Given a newly recorded sighting, then it is immediately prepended to the top of the list without a page refresh.
- Given an undone sighting, then it is immediately removed from the list.
- Given `./gradlew test`, then all automated integration and asset tests pass cleanly.

## Implementation Notes

- Implemented `listview.js` querying `GET /api/v1/sightings?limit=500` and rendering chronological cards with author, timestamp, and coordinates.
- Highlighted user's own sightings with `border-left: 4px solid var(--accent-blue)` and `<span class="badge-own">My Sighting</span>`.
- Added `#listErrorBanner` and `#listEmptyCard` with prompt copy matching specifications.
- Implemented deterministic timestamp formatting (`Oct 28, 14:32:05`).
- Wired event bus listeners: `sighting-added`, `sighting-deleted`, and `user-changed`.
- Sized `#listViewContainer` and updated `FrontendAssetTest.kt`; all 11 tests pass cleanly in 1m 49s.

## Plan Change Log

## Review Triage Log

- Location: `index.html` | Verdict: low | Evidence: Aligned empty-state card prompt text with specification ("No shoes spotted yet! Tap the Spot button to be the first."). | Route: patch
- Location: `listview.js` & `index.html` | Verdict: medium | Evidence: Added `#listErrorBanner` for graceful network error display on fetch failure. | Route: patch
- Location: `listview.js` | Verdict: medium | Evidence: Guarded pre-load `addSighting` state desynchronization when a sighting is logged before List tab activation. | Route: patch
- Location: `listview.js` | Verdict: low | Evidence: Replaced locale-dependent date formatting with deterministic string formatting (`Oct 28, 14:32:05`). | Route: patch

## Design Notes

- Cards use `background: var(--surface-raised)` with subtle hover states for touch feedback.

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `curl -s http://localhost:8080/js/listview.js` -- expected: HTTP 200 with list controller code.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing `#listViewContainer`.
