---
id: 6
type: story
title: "Replace 'GPS Denied' with 'No GPS' / 'Kein GPS' Across UI Labels, i18n Keys, DOM IDs, CSS Classes, and Geolocation State"
parent: epic-versions-and-build
covers: [R41]
after: [1, 2, 3, 4, 5]
hitl: false
risk: low
---

# Replace 'GPS Denied' with 'No GPS' / 'Kein GPS' Across UI Labels, i18n Keys, DOM IDs, CSS Classes, and Geolocation State

## Description

Replaces the `'denied'` geolocation state, `#miniMapDeniedOverlay` DOM ID, `.gps-pill.denied` CSS selector, and `gps.denied` / `spot.denied` / `spot.deniedOverlay` i18n keys with `no-gps` / `noGps` equivalents (`'no-gps'`, `#miniMapNoGpsOverlay`, `.gps-pill.no-gps`, `gps.noGps`, `spot.noGps`, `spot.noGpsOverlay`), and updates the displayed text to `'No GPS'` / `'⚠️ No GPS'` (EN) and `'Kein GPS'` / `'⚠️ Kein GPS'` (DE).

## Requirements

- In `src/main/resources/META-INF/resources/js/geo.js`:
  - Replace `'denied'` state with `'no-gps'` (`this.setState('no-gps', ...)` and `if (this.state === 'no-gps') return;`).
- In `src/main/resources/META-INF/resources/js/minimap.js`:
  - Replace `document.getElementById('miniMapDeniedOverlay')` with `document.getElementById('miniMapNoGpsOverlay')` and `if (status === 'denied')` with `if (status === 'no-gps')`.
- In `src/main/resources/META-INF/resources/css/style.css`:
  - Rename `.gps-pill.denied` to `.gps-pill.no-gps`.
- In `src/main/resources/META-INF/resources/index.html`:
  - Rename `id="miniMapDeniedOverlay"` to `id="miniMapNoGpsOverlay"` and `<span data-i18n="spot.deniedOverlay">⚠️ Location Access Denied</span>` to `<span data-i18n="spot.noGpsOverlay">⚠️ No GPS</span>`.
  - Update `updateGpsPillText` to use `t('gps.noGps')` with fallback `'No GPS'`.
- In `src/main/resources/META-INF/resources/js/spot.js`:
  - Update `setGpsStatus` to use `i18n.t('spot.noGps')` with fallback `'No GPS'`.
- In `src/main/resources/META-INF/resources/js/i18n.js`:
  - Replace `'gps.denied'`, `'spot.denied'`, and `'spot.deniedOverlay'` with:
    - `en`: `'gps.noGps': 'No GPS'`, `'spot.noGps': 'No GPS'`, `'spot.noGpsOverlay': '⚠️ No GPS'`
    - `de`: `'gps.noGps': 'Kein GPS'`, `'spot.noGps': 'Kein GPS'`, `'spot.noGpsOverlay': '⚠️ Kein GPS'`
- In `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`:
  - Update assertions to verify `miniMapNoGpsOverlay`, `spot.noGpsOverlay`, `gps.noGps`, `spot.noGps`, `No GPS`, `Kein GPS`, `⚠️ No GPS`, and `⚠️ Kein GPS`, and assert the absence of `miniMapDeniedOverlay`, `GPS Denied`, and `GPS Verweigert`.
- Verify: `./gradlew check test spotlessCheck lintJs` passes cleanly.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md
