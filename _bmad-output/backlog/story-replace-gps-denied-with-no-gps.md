---
type: story
title: "Replace 'GPS Denied' with 'No GPS' / 'Kein GPS' Across UI Labels, i18n Keys, DOM IDs, CSS Classes, and Geolocation State"
parent: "epic-versions-and-build"
covers: ["R41"]
after: [1, 2, 3, 4, 5]
hitl: false
risk: low
status: "done"
---

# Replace 'GPS Denied' with 'No GPS' / 'Kein GPS' Across UI Labels, i18n Keys, DOM IDs, CSS Classes, and Geolocation State

## Description

Because browsers enter the unavailable/blocked geolocation state not only when a user clicks "Block" (`PERMISSION_DENIED`), but also when `navigator.geolocation` is unsupported, accessed over an insecure HTTP LAN origin, or OS-level Location Services are turned off, labeling the UI and internal state `"GPS Denied"` / `"GPS Verweigert"` / `"Location Access Denied"` is both factually too narrow and unnecessarily verbose on mobile viewports.

This story replaces the `'denied'` geolocation state, DOM ID (`#miniMapDeniedOverlay`), CSS modifier (`.gps-pill.denied`), and i18n keys (`gps.denied`, `spot.denied`, `spot.deniedOverlay`) with `no-gps` / `noGps` equivalents (`'no-gps'`, `#miniMapNoGpsOverlay`, `.gps-pill.no-gps`, `gps.noGps`, `spot.noGps`, `spot.noGpsOverlay`), and updates the displayed text to `'No GPS'` / `'⚠️ No GPS'` (EN) and `'Kein GPS'` / `'⚠️ Kein GPS'` (DE).

## Acceptance Criteria

1. **Given** `src/main/resources/META-INF/resources/js/geo.js` and `src/main/resources/META-INF/resources/js/minimap.js`
   **When** geolocation is unsupported or `watchPosition` reports error code `1`
   **Then** `GeolocationWatcher` transitions to state `'no-gps'` (instead of `'denied'`) and `HudMiniMapManager` checks `status === 'no-gps'` and toggles `#miniMapNoGpsOverlay`

2. **Given** `src/main/resources/META-INF/resources/index.html`, `src/main/resources/META-INF/resources/css/style.css`, and `src/main/resources/META-INF/resources/js/spot.js`
   **When** the no-GPS state is active
   **Then** `#gpsPill` receives class `gps-pill no-gps` (styled by `.gps-pill.no-gps`), `#gpsPillLabel` uses `gps.noGps` (fallback `'No GPS'`), `#btnSpotLabel` uses `spot.noGps` (fallback `'No GPS'`), and `#miniMapNoGpsOverlay` uses `data-i18n="spot.noGpsOverlay"` with default text `'⚠️ No GPS'`

3. **Given** `src/main/resources/META-INF/resources/js/i18n.js`
   **When** English (`en`) or German (`de`) translations are loaded
   **Then**:
   - `en['gps.noGps']` is `'No GPS'` and `de['gps.noGps']` is `'Kein GPS'`
   - `en['spot.noGps']` is `'No GPS'` and `de['spot.noGps']` is `'Kein GPS'`
   - `en['spot.noGpsOverlay']` is `'⚠️ No GPS'` and `de['spot.noGpsOverlay']` is `'⚠️ Kein GPS'`

4. **Given** `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`
   **When** `./gradlew check test spotlessCheck lintJs` is executed
   **Then** the test suite verifies all `no-gps` / `noGps` IDs, keys, CSS selectors, and localized strings (`No GPS`, `Kein GPS`, `⚠️ No GPS`, `⚠️ Kein GPS`) and confirms zero stale `miniMapDeniedOverlay`, `gps.denied`, `spot.denied`, `spot.deniedOverlay`, `GPS Denied`, or `GPS Verweigert` references remain.
