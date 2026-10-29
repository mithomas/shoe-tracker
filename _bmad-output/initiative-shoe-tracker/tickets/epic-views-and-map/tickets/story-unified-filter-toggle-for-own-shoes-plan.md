---
title: 'Unified filter toggle for own shoes'
type: 'feature'
ticket: '4'
created: '2026-10-28'
status: 'built'
baseline_revision: '7321d53599b2bbb4536cb601b5084747e76268a0'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran:
  - 'quick'
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md'
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-views-and-map/epic-views-and-map.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Highway travelers need to toggle visibility between all sightings and only their own sightings across both the Map and List views, without having to re-toggle the filter on each view.

**Approach:** Implement a unified segmented filter pill toggle (`[ All Shoes ]` / `[ My Shoes ]`) present on both Map and List views, synchronized via a `filter-changed` DOM event and persisted in `localStorage.filter_own_only`. Filter map markers and list items instantly, with clear empty states when the user has no personal sightings.

## Boundaries & Constraints

**Always:**
- Segmented pill toggle (`rounded: full`) with options `All Shoes` and `My Shoes`.
- Persist filter choice in `localStorage.filter_own_only` (`'true'` / `'false'`).
- Toggling filter in either view immediately synchronizes the other view.
- Map view: When `My Shoes` is active, display only markers matching the current `user_id`; show empty banner if 0 match.
- List view: When `My Shoes` is active, display only cards matching the current `user_id`; update count badge to show filtered count (e.g. `2 of 8 sightings`).
- Filter compares user identity case-insensitively and trimmed against `localStorage.user_id`.

**Never:**
- Do not re-fetch the entire API on filter toggle (filter in-memory collections instantly).
- Do not desynchronize filter state between Map and List tabs.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Toggle "My Shoes" on Map | User clicks "My Shoes" pill | Map instantly hides other users' slate markers; keeps only blue markers; persists to localStorage | Shows empty banner if 0 own shoes |
| Switch from Map to List | User switches to List tab | List tab opens with "My Shoes" active, displaying only own cards and updated count | Filter state is perfectly synced |
| No Personal Sightings | User has 0 sightings logged | Selecting "My Shoes" shows empty prompt: *"No shoes spotted by you yet — tap the Spot button to log your first!"* | Friendly empty state |
| User Changes Name | `user-changed` event dispatched | Re-evaluates filter immediately for the new user name across Map and List | Updates visible markers and cards live |

</frozen-after-approval>

## Code Map

- `src/main/resources/META-INF/resources/js/filter.js` -- Unified filter controller (toggle management, localStorage persistence, event dispatching)
- `src/main/resources/META-INF/resources/js/fullmap.js` -- Updated to filter markers by `ownOnly`
- `src/main/resources/META-INF/resources/js/listview.js` -- Updated to filter cards by `ownOnly` and update count badge
- `src/main/resources/META-INF/resources/css/style.css` -- Segmented pill toggle styles and empty-filter banner styles
- `src/main/resources/META-INF/resources/index.html` -- Filter toggle elements in `#viewMap` and `#viewList`
- `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Automated tests verifying filter toggle script and elements

## Tasks & Acceptance

**Execution:**
- [x] Filter Controller -- Implement `filter.js` with toggle state management, `localStorage` persistence, and `filter-changed` event dispatching.
- [x] Map View Filter Integration -- Update `fullmap.js` to show/hide markers based on active filter state and show empty-filter message.
- [x] List View Filter Integration -- Update `listview.js` to show/hide cards based on active filter state and update count display.
- [x] Segmented Pill Styling -- Add CSS in `style.css` for `.filter-pill-container` and `.filter-pill-btn`.
- [x] View Markup -- Add filter toggle markup to `#viewMap` and `#viewList` in `index.html`.
- [x] Automated Test Suite -- Update `FrontendAssetTest.kt` verifying filter assets and run `./gradlew test`.

**Acceptance Criteria:**
- Given the Map view, when toggling "My Shoes", then only markers belonging to the active user remain visible.
- Given the List view, when toggling "My Shoes", then only cards belonging to the active user are rendered and the counter reflects the filtered count.
- Given a filter toggle in either view, then the state is remembered across tab switches and page reloads via `localStorage`.
- Given `./gradlew test`, then all automated integration and asset tests pass cleanly.

## Implementation Notes

## Plan Change Log

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | `fullmap.js:135` Viewport camera jump on every state update | medium | patch | `renderAllMarkers()` called `fitBounds` unconditionally on every marker re-render; should only fit bounds on initial load. |
| quick | `fullmap.js:118` & `listview.js:102` Generic empty state displayed when global sightings is 0 even if "My Shoes" active | medium | patch | Matrix requires showing personal empty banner whenever "My Shoes" is active and user has 0 sightings; prioritize `ownOnly && filtered.length === 0` check. |
| quick | `style.css:174` Missing CSS variable `--accent-blue` | false | rejected | Disproved: `--accent-blue: #3B82F6` is explicitly defined in `:root` at `style.css:15`. |
| quick | `filter.js:28` No `filter-changed` dispatched on `user-changed` when `ownOnly: false` | false | rejected | Disproved: When `ownOnly` is false, no items are filtered, and `user-changed` is handled directly by `fullmap.js` and `listview.js` for recoloring. |

## Design Notes

- Segmented pill toggle uses `background: var(--surface-overlay)` with active pill in safety orange `#F97316` or signal blue `#3B82F6` and smooth background slide.

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `curl -s http://localhost:8080/js/filter.js` -- expected: HTTP 200 with filter controller code.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing `.filter-pill-container`.
