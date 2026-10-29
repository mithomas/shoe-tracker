---
title: 'Multi-Language Support (English and German)'
type: 'feature'
ticket: 4
created: '2026-10-29'
status: 'built'
baseline_revision: '9853508e329b9a29dc636961e2d50b0150b8d8e2'
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

**Problem:** German-speaking highway users need full German localization alongside English, with automatic `navigator.language` detection, a top-bar `DE`/`EN` toggle button (`#langToggleBtn`), and persistence in `localStorage.lang`.

**Approach:** Create `src/main/resources/META-INF/resources/js/i18n.js` (`I18nManager`) with English (`en`) and German (`de`) dictionaries, add `#langToggleBtn` in `.header-status` in `index.html`, add `data-i18n` attributes to static DOM nodes in `index.html`, and wire `lang-changed` listeners in `spot.js`, `onboarding.js`, `modal.js`, `fullmap.js`, `listview.js`, and `index.html` so all UI labels update reactively without page reload.

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route.
- Created `src/main/resources/META-INF/resources/js/i18n.js` with `en` and `de` translation dictionaries (`SHOE SPOTTED!` -> `SCHUH!`, `GPS Ready` -> `GPS Bereit`, `Waiting for GPS...` -> `Warte auf GPS...`, `GPS Denied` -> `GPS Verweigert`, `Recent Sightings` -> `Letzte Sichtungen`, `What's your name?` -> `Wie heißt du?`, etc.), `navigator.language` auto-detection, `localStorage.lang` persistence, `data-i18n` DOM translation, and `lang-changed` event dispatch.
- Added `#langToggleBtn` (`DE` / `EN`) in `index.html` and wired dynamic components (`spot.js`, `onboarding.js`, `modal.js`, `fullmap.js`, `listview.js`) to `window.ShoeTracker.i18n.t(...)` and `lang-changed`.
- Added unit/asset verification in `FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

- `quick` review subagent (`13dec6bf-3bb8-4f23-9e70-2830ddaaf507`): 0 findings (`[]`).

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
