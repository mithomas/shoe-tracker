---
title: "Remove 'HUD Radar' Badge from HUD Mini-Map"
type: 'feature'
ticket: '5'
created: '2026-10-30'
status: 'built'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 1
context: []
---

# Implementation Plan - Remove 'HUD Radar' Badge from HUD Mini-Map (Story 8.5)

## Overview
Remove the floating `"HUD Radar"` / `"HUD-Radar"` badge (`<span class="mini-map-hud-badge" data-i18n="spot.hudBadge">HUD Radar</span>`) from `.mini-map-container` in `index.html`, remove `.mini-map-hud-badge` from `css/style.css`, and remove `'spot.hudBadge'` from `js/i18n.js` while keeping `#miniMapNoGpsOverlay` (`⚠️ No GPS` / `⚠️ Kein GPS`) and all live mini-map dots intact.

## Tasks
1. **Update `src/main/resources/META-INF/resources/index.html`**:
   - Remove `<span class="mini-map-hud-badge" data-i18n="spot.hudBadge">HUD Radar</span>` inside `.mini-map-container`.
2. **Update `src/main/resources/META-INF/resources/css/style.css`**:
   - Remove the `.mini-map-hud-badge` rule block.
3. **Update `src/main/resources/META-INF/resources/js/i18n.js`**:
   - Remove `'spot.hudBadge': 'HUD Radar'` (en) and `'spot.hudBadge': 'HUD-Radar'` (de).
4. **Update `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`**:
   - Assert in `testMultiLanguageSupportEnglishAndGerman()` and `testIndexHtmlServedWithAllComponents()` / `testStaticAssetsServed()` that `mini-map-hud-badge`, `spot.hudBadge`, `HUD Radar`, and `HUD-Radar` are absent while `#miniMapNoGpsOverlay` remains present.
