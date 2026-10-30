---
title: 'Fix GitHub Source Repository Link URL to Point to mithomas/shoe-tracker'
type: 'bugfix'
ticket: '9'
created: '2026-10-30'
status: 'built'
baseline_revision: 'd97ec5834b5c1a40b24849dbfffe73da278d2431'
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

**Problem:** The `AGPL-3.0 Source` links in the web footer (`index.html`) and the GitHub repository link in the `## License` section of `README.md` point to `https://github.com/mitch/shoe-tracker` instead of `https://github.com/mithomas/shoe-tracker`.

**Approach:** Update both footer links in `src/main/resources/META-INF/resources/index.html` and the repository link in `README.md` to `https://github.com/mithomas/shoe-tracker`, and add assertions in `FrontendAssetTest.kt` and `DevOpsAssetTest.kt` verifying `https://github.com/mithomas/shoe-tracker` and the absence of `mitch/shoe-tracker`.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the fix updates two static links in `index.html`, one link in `README.md`, and assertions in `FrontendAssetTest.kt` and `DevOpsAssetTest.kt`.
- Updated both `.footer-license-link` anchors in `src/main/resources/META-INF/resources/index.html` to `https://github.com/mithomas/shoe-tracker`.
- Updated the `Shoe Tracker GitHub Repository` link in `README.md` to `https://github.com/mithomas/shoe-tracker`.
- Added assertions in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` and `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`.

## Plan Change Log

## Review Triage Log

### Iteration 1 (`quick`)
- 0 findings reported.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all frontend and DevOps asset tests passing
