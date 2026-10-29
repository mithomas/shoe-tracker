---
title: 'Spotting Polish Refactor Sweep and End-to-End Verification'
type: 'feature'
ticket: 8
created: '2026-10-29'
status: 'built'
baseline_revision: '15cc66a808fe7a4384ab05bd1731ce178778281c'
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

**Problem:** After landing Stories 3.1 through 3.7, all Epic 3 components (`R28`-`R35`) need a cohesive polish audit across English/German localization, light/dark theme contrast, static JS analysis (`lintJs`), Spotless formatting (`spotlessCheck`), and end-to-end automated integration test coverage.

**Approach:**
1. Audit and polish English/German i18n coverage (`data-i18n-title` support in `i18n.js` for `#userChip` and localized theme toggle tooltips in `theme.js` on `lang-changed`).
2. Refine `[data-theme="light"]` contrast rules in `style.css` for `.badge-own` and `.shoe-cluster-other` on light surfaces and map tiles.
3. Add `testEpic3SpottingPolishEndToEndVerification` in `FrontendAssetTest.kt` verifying all Epic 3 requirements (`R28`-`R35`) end-to-end.
4. Run full Gradle verification (`./gradlew spotlessApply check test`).

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route.
- Added `data-i18n-title` attribute handling in `i18n.js`, localized `#userChip` title in `index.html`, and localized `#themeToggleBtn` aria-label/title on `lang-changed` in `theme.js`.
- Added light-mode contrast refinements for `.badge-own` and `.shoe-cluster-other` in `style.css`.
- Added `testEpic3SpottingPolishEndToEndVerification` in `FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

- Iteration 1 (`quick` lens): 0 findings.

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
