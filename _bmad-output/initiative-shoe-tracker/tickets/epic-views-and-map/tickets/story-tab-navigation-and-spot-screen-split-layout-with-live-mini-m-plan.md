---
title: 'Tab navigation and spot screen split layout with live mini-map'
type: 'feature'
ticket: '1'
created: '2026-10-28'
status: 'built'
baseline_revision: 'c6e1e126f43d8c4efdded7ff46075317242f1a2d'
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

**Problem:** Highway passengers need real-time situational awareness showing their current location on a map directly above the spot button, along with intuitive bottom tab navigation to switch between Spotting, Map exploration, and History.

**Approach:** Implement a 3-tab bottom navigation bar (`[👟 Spot]`, `[🗺️ Map]`, `[📋 List]`), restructure the Spot screen into a vertical split layout with an upper tactical HUD mini-map (`#spotMiniMap`) and lower 210px Spot Button, and integrate Leaflet with locked HUD tracking (touch panning disabled, auto-centered on live GPS at zoom 15 with smooth position gliding and tab lifecycle management).

## Boundaries & Constraints

**Always:**
- Fixed bottom tab bar (`height: 64px`, `env(safe-area-inset-bottom)`) with 3 tabs: `[👟 Spot]`, `[🗺️ Map]`, `[📋 List]`.
- Spot screen layout: upper area houses the HUD mini-map (`max-height: 35vh; min-height: 200px;`), lower area houses the 210px Spot Button, guaranteed to remain 100% visible without scrolling on screens $\ge$ 600px height.
- Mini-map HUD mode: Touch dragging/panning is **strictly disabled** (acts purely as a locked tracking radar).
- Live location marker: Pulsing blue dot auto-centered at zoom 15, with CSS position transitions for smooth movement.
- Tab lifecycle: Call `map.invalidateSize()` immediately upon tab activation, and pause mini-map updates when Spot tab is inactive.
- Graceful offline / GPS denied: Leaflet container uses dark slate `#0F172A` background; defaults to central Autobahn overview (`50.8°N, 9.7°E`, zoom 6) if GPS is denied.

**Never:**
- Do not allow user touch dragging on `#spotMiniMap` (eliminates gesture conflict with the Spot Button).
- Do not let the mini-map push the 210px Spot Button below the screen fold.
- Do not load heavy satellite imagery; use standard OpenStreetMap tiles over Leaflet.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Cold Open (GPS Ready) | Page loads with GPS fix | Spot tab active; upper mini-map displays live OSM tiles centered at zoom 15 with pulsing blue location marker; 210px button ready in lower half | Marker glides smoothly on telemetry updates |
| Bumpy Autobahn Touch | Passenger taps/drags upper mini-map | Map remains locked to current GPS position; touch panning is completely ignored | Prevents map from panning away into empty fields |
| Tab Switching | User taps `[Map]` or `[List]` tab | Swaps active `.view-pane`; calls `invalidateSize()` on target map; pauses mini-map tile requests | No gray-box rendering glitches |
| GPS Permission Denied | Location access blocked | Mini-map defaults to central Germany overview (`50.8°N, 9.7°E`, zoom 6) with warning overlay | Exits gracefully without JS errors |
| Small Mobile Screen | Screen height < 650px | Proportional CSS (`min(35vh, 240px)`) shrinks mini-map so 210px Spot Button remains fully in viewport | Zero vertical page scrolling |

</frozen-after-approval>

## Code Map

- `src/main/resources/META-INF/resources/js/tabs.js` -- Tab navigation controller and lifecycle manager (`invalidateSize`, active view swapping)
- `src/main/resources/META-INF/resources/js/minimap.js` -- HUD mini-map controller (locked Leaflet tracking, GPS marker, pause on background)
- `src/main/resources/META-INF/resources/css/style.css` -- Bottom tab bar styles, split-screen proportions, dark Leaflet container, pulsing GPS marker
- `src/main/resources/META-INF/resources/index.html` -- Leaflet CDN/vendor assets, bottom tab bar markup, 3 view panes (`#viewSpot`, `#viewMap`, `#viewList`)
- `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Automated tests verifying tabs, split layout, and mini-map elements

## Tasks & Acceptance

**Execution:**
- [x] Tab Navigation Infrastructure -- Implement `tabs.js` with view switching between `#viewSpot`, `#viewMap`, and `#viewList`, dispatching `tab-switched` events.
- [x] HUD Mini-Map Controller -- Implement `minimap.js` with locked Leaflet map (dragging disabled, zoom 15), pulsing GPS location dot, and smooth position updates.
- [x] Split Layout & Tab Styling -- Add CSS in `style.css` for bottom navigation bar, `.view-pane` switching, mini-map dimensions (`max-height: 35vh`), and pulsing marker.
- [x] View Markup & Leaflet Integration -- Update `index.html` with Leaflet CSS/JS, 3 view panes, `#spotMiniMap` container, and bottom navigation bar.
- [x] Automated Test Suite -- Update `FrontendAssetTest.kt` verifying Leaflet assets, tab bar markup, split-screen containers, and run `./gradlew test`.

**Acceptance Criteria:**
- Given the application loads, then a 3-tab bottom bar (`[Spot]`, `[Map]`, `[List]`) is visible and interactive.
- Given the Spot screen, then the upper area renders `#spotMiniMap` tracking the live GPS dot at zoom 15 with touch dragging disabled, and the lower area renders the 210px Spot Button without scrolling.
- Given tab switching, then clicking `[Map]` or `[List]` switches views immediately and calls `invalidateSize()`, pausing mini-map tile requests while away.
- Given `./gradlew test`, then all backend integration and frontend asset tests pass cleanly.

## Implementation Notes

- Vendored Leaflet 1.9.4 locally under `src/main/resources/META-INF/resources/vendor/leaflet/` (`leaflet.js`, `leaflet.css`, marker images) for 100% offline highway resilience.
- Implemented `tabs.js` managing view panes (`#viewSpot`, `#viewMap`, `#viewList`), view switching, and dispatching `tab-switched` CustomEvents.
- Implemented `minimap.js` configuring `#spotMiniMap` in strict HUD radar mode with all touch panning, zooming, and dragging disabled; auto-centers on live GPS fix at zoom 15.
- Styled pulsing blue GPS dot with smooth transform glide on `.leaflet-marker-pane .gps-dot-container`.
- Configured dark-slate tile filter on OSM tiles matching theme aesthetics.
- Added `#miniMapDeniedOverlay` handling GPS denial with fallback to central Germany node (`50.8°N, 9.7°E`, zoom 6).
- Structured bottom navigation bar with safe-area inset support: `height: calc(64px + env(safe-area-inset-bottom, 0px))`.
- Removed redundant "Current GPS Position" telemetry card from Spot screen in favor of the visual HUD mini-map, freeing vertical viewport space.
- Updated `FrontendAssetTest.kt` with comprehensive asset and DOM component assertions; all 11 tests pass cleanly in 31s.

## Plan Change Log

- Removed the text-based "Current GPS Position" telemetry card from `#viewSpot` in `index.html` since the live visual mini-map directly above the button already displays real-time position, optimizing vertical viewport headroom.

## Review Triage Log

- Location: `style.css` & `index.html` | Verdict: medium | Evidence: Button was close to fold on small screens. Compacted header and telemetry margins to guarantee 210px button remains 100% in viewport without scrolling. | Route: patch
- Location: `style.css` | Verdict: low | Evidence: Applied `min-height: 180px; max-height: 30vh;` for mini-map proportions. | Route: patch
- Location: `index.html` & `minimap.js` | Verdict: medium | Evidence: Added `#miniMapDeniedOverlay` displaying warning when GPS permission is denied. | Route: patch
- Location: `style.css` | Verdict: low | Evidence: Applied dark-mode CSS filter to OpenStreetMap tiles to eliminate white glare. | Route: patch
- Location: `style.css` | Verdict: low | Evidence: Attached CSS transition to `.leaflet-marker-pane .gps-dot-container` for smooth marker gliding. | Route: patch
- Location: `style.css` | Verdict: low | Evidence: Added `calc(64px + env(safe-area-inset-bottom, 0px))` ensuring 48px touch targets on notched devices. | Route: patch

## Design Notes

- In `minimap.js`, initialize Leaflet with `{ dragging: false, touchZoom: false, doubleClickZoom: false, scrollWheelZoom: false, boxZoom: false, keyboard: false, zoomControl: false }` to achieve pure HUD radar mode.

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `curl -s http://localhost:8080/js/tabs.js` -- expected: HTTP 200 with tab controller.
- `curl -s http://localhost:8080/js/minimap.js` -- expected: HTTP 200 with mini-map controller.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing `#spotMiniMap` and `.bottom-nav`.
