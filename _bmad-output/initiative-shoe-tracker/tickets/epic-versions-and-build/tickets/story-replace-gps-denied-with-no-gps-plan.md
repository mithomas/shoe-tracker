---
title: "Replace 'GPS Denied' with 'No GPS' / 'Kein GPS' Across UI Labels, i18n Keys, DOM IDs, CSS Classes, and Geolocation State"
type: 'feature'
ticket: 6
created: '2026-10-29'
status: 'built'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'auto'
lenses_ran: ['quick']
review_loop_iteration: 1
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md", "/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Geolocation enters the unavailable state not only on explicit permission denial, but also when `navigator.geolocation` is unsupported, served over non-HTTPS LAN origins, or OS location services are disabled. Using `'denied'` / `'GPS Denied'` / `'GPS Verweigert'` / `'⚠️ Location Access Denied'` across state names, DOM IDs, CSS classes, i18n keys, and UI labels is factually inaccurate in those cases and takes unnecessary horizontal space in the mobile header.

**Approach:** Rename the `'denied'` state in `geo.js` and `minimap.js` to `'no-gps'`, rename `#miniMapDeniedOverlay` to `#miniMapNoGpsOverlay`, rename `.gps-pill.denied` to `.gps-pill.no-gps` (matching the existing `.btn-spot-giant.no-gps` convention), rename i18n keys to `gps.noGps`, `spot.noGps`, and `spot.noGpsOverlay`, set their localized strings to `'No GPS'` / `'⚠️ No GPS'` (EN) and `'Kein GPS'` / `'⚠️ Kein GPS'` (DE), and update `FrontendAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

- Aligns `#gpsPill.no-gps` with the existing `.btn-spot-giant.no-gps` class naming convention in `style.css` and `spot.js`.
- Preserves `#gpsWarningBanner` (`banner.gpsTitle` / `banner.gpsBody`) below the header to guide users on enabling location permissions/settings.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
