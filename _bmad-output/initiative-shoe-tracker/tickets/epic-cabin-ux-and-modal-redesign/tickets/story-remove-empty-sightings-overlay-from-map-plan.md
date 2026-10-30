---
title: "Remove 'No Shoes Yet' Empty Banner from Map View"
type: 'feature'
ticket: '6'
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

# Implementation Plan - Remove 'No Shoes Yet' Empty Banner from Map View (Story 8.6)

## Overview
Remove the `#fullMapEmpty` and `#fullMapEmptyOwn` overlay banners from `#viewMap` (`index.html`), remove their DOM queries and display toggles from `js/fullmap.js`, and remove `'map.empty'` and `'map.emptyOwn'` from `js/i18n.js`, while preserving `#fullMapLoading` on the Map view and `#listEmptyCard` / `#listEmptyOwnCard` in the List view. Also update `FrontendAssetTest.kt` and remove completed Epic 8 items from `README.md` (`## Roadmap & Backlog`).

## Tasks
1. **Update `src/main/resources/META-INF/resources/index.html`**:
   - Remove `#fullMapEmpty` and `#fullMapEmptyOwn` from `#fullMapContainer` while keeping `#fullMapLoading`, `#listEmptyCard`, and `#listEmptyOwnCard`.
2. **Update `src/main/resources/META-INF/resources/js/fullmap.js`**:
   - Remove `fullMapEmpty` and `fullMapEmptyOwn` lookups and `.style.display` toggles in `fetchSightings()` and `renderAllMarkers()`.
3. **Update `src/main/resources/META-INF/resources/js/i18n.js`**:
   - Remove `'map.empty'` and `'map.emptyOwn'` from `en` and `de` translations while keeping `'list.emptyTitle'`, `'list.emptyBody'`, and `'list.emptyOwnBody'`.
4. **Update `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`**:
   - Update `testIndexHtmlServedWithAllComponents()`, `testUnifiedFilterAssetsAndMarkup()`, and `testMultiLanguageSupportEnglishAndGerman()` to assert that `fullMapEmpty`, `fullMapEmptyOwn`, `map.empty`, and `map.emptyOwn` are absent while `fullMapLoading`, `listEmptyCard`, and `listEmptyOwnCard` remain present.
5. **Update `README.md`**:
   - Remove completed Epic 8 items from `## Roadmap & Backlog`.
