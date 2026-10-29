---
title: 'Light and Dark Mode Theme Switcher'
type: 'feature'
ticket: 3
created: '2026-10-29'
status: 'built'
baseline_revision: 'e32ec63a8ee2f023bda73e66ed5b736303d15d6e'
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

**Problem:** Users spotting shoes in bright daylight need a high-contrast light mode alongside the default dark slate mode, with automatic OS theme detection, manual override persistence, and natural non-inverted map tiles in light mode.

**Approach:** Add `src/main/resources/META-INF/resources/js/theme.js` (`ThemeManager`), add a compact `#themeToggleBtn` pill inside a `flex-wrap: wrap` `.app-header` in `index.html`, define `[data-theme="light"]` CSS variables and `#spotMiniMap .leaflet-tile, #fullMap .leaflet-tile { filter: none; }` in `style.css`, and update `.map-overlay-banner`, `.mini-map-hud-badge`, and `.mini-map-overlay` to use theme-aware variables.

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route.
- Created `src/main/resources/META-INF/resources/js/theme.js` with OS `prefers-color-scheme: dark` auto-detection, `localStorage.getItem('theme')` persistence, `document.documentElement.setAttribute('data-theme', theme)`, and `#themeToggleBtn` updates (`☀️` / `🌙`).
- Added `[data-theme="light"]` tokens, map tile filter reset (`filter: none`), theme-aware overlay surfaces, and compact `.header-pill-btn` (`padding: 5px 8px; font-size: 12px;`) inside a `flex-wrap: wrap` `.app-header`.
- Added unit/asset verification in `FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

- `quick` review subagent (`33097d92-ca2c-417b-8a62-22fff2e687e8`): 0 findings (`[]`).

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
