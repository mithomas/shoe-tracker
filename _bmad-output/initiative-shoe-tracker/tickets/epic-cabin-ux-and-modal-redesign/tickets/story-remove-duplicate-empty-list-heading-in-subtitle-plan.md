---
title: "Remove Duplicate 'No shoes spotted yet!' Text from Second Line of List View Empty State"
type: 'feature'
ticket: '9'
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

# Implementation Plan - Remove Duplicate 'No shoes spotted yet!' Text from Second Line of List View Empty State (Story 8.9)

## Overview
Remove the duplicate `"No shoes spotted yet!"` (`"Noch keine Schuhe gemeldet!"`) prefix from `<p data-i18n="list.emptyBody">` in `index.html` and `'list.emptyBody'` in `js/i18n.js` so that `#listEmptyCard` shows `"No shoes spotted yet!"` (`"Noch keine Schuhe gemeldet!"`) in the `<h3>` heading and `"Tap the Spot button to be the first."` (`"Tippe auf den Button für die erste Meldung."`) on the second line.

## Tasks
1. **Update `src/main/resources/META-INF/resources/index.html`**:
   - Change `<p data-i18n="list.emptyBody">No shoes spotted yet! Tap the Spot button to be the first.</p>` inside `#listEmptyCard` to `<p data-i18n="list.emptyBody">Tap the Spot button to be the first.</p>`.
2. **Update `src/main/resources/META-INF/resources/js/i18n.js`**:
   - Update `en['list.emptyBody']` to `'Tap the Spot button to be the first.'`.
   - Update `de['list.emptyBody']` to `'Tippe auf den Button für die erste Meldung.'`.
3. **Update `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`**:
   - Assert in `testMultiLanguageSupportEnglishAndGerman()` that `index.html` contains `<p data-i18n="list.emptyBody">Tap the Spot button to be the first.</p>` and does not contain `No shoes spotted yet! Tap the Spot button to be the first.`, and that `js/i18n.js` contains `'list.emptyBody': 'Tap the Spot button to be the first.'` and `'list.emptyBody': 'Tippe auf den Button für die erste Meldung.'`.
