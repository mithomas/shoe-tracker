---
title: "Remove '— Germany Overview' Suffix from GPS Denied Mini-Map Overlay"
type: 'feature'
ticket: 5
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

**Problem:** When GPS is denied or unavailable, `#miniMapDeniedOverlay` appends a redundant ` — Germany Overview` (EN) / ` — Deutschland-Übersicht` (DE) suffix to the warning label, cluttering the HUD mini-map overlay on mobile screens.

**Approach:** Update `#miniMapDeniedOverlay` in `src/main/resources/META-INF/resources/index.html` and `spot.deniedOverlay` in `src/main/resources/META-INF/resources/js/i18n.js` (`en` and `de`) to `'⚠️ Location Access Denied'` and `'⚠️ Standortzugriff verweigert'`, and assert in `FrontendAssetTest.kt` that `Germany Overview` and `Deutschland-Übersicht` are absent.

</frozen-after-approval>

## Implementation Notes

- Keeps the fallback map center (`[50.8, 9.7]`, zoom 6) unchanged in `minimap.js`.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
