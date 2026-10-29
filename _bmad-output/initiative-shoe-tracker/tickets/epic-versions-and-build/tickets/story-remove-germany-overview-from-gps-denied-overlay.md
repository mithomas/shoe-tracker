---
id: 5
type: story
title: "Remove '— Germany Overview' Suffix from GPS Denied Mini-Map Overlay"
parent: epic-versions-and-build
covers: [R40]
after: [1, 2, 3, 4]
hitl: false
risk: low
---

# Remove '— Germany Overview' Suffix from GPS Denied Mini-Map Overlay

## Description

Removes the redundant ` — Germany Overview` (EN) and ` — Deutschland-Übersicht` (DE) suffix from the HUD mini-map location-denied overlay (`#miniMapDeniedOverlay`) when GPS access is unavailable or denied, keeping the warning concise (`⚠️ Location Access Denied` / `⚠️ Standortzugriff verweigert`).

## Requirements

- In `src/main/resources/META-INF/resources/index.html`:
  - Change `<span data-i18n="spot.deniedOverlay">⚠️ Location Access Denied — Germany Overview</span>` inside `#miniMapDeniedOverlay` to `<span data-i18n="spot.deniedOverlay">⚠️ Location Access Denied</span>`.
- In `src/main/resources/META-INF/resources/js/i18n.js`:
  - Change `en['spot.deniedOverlay']` from `'⚠️ Location Access Denied — Germany Overview'` to `'⚠️ Location Access Denied'`.
  - Change `de['spot.deniedOverlay']` from `'⚠️ Standortzugriff verweigert — Deutschland-Übersicht'` to `'⚠️ Standortzugriff verweigert'`.
- In `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`:
  - Assert that `index.html` and `i18n.js` contain `⚠️ Location Access Denied` and `⚠️ Standortzugriff verweigert`, and do not contain `Germany Overview` or `Deutschland-Übersicht`.
- Verify: `index.html` and `i18n.js` render `⚠️ Location Access Denied` and `⚠️ Standortzugriff verweigert` without `Germany Overview` or `Deutschland-Übersicht`, and `./gradlew check test spotlessCheck lintJs` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md
