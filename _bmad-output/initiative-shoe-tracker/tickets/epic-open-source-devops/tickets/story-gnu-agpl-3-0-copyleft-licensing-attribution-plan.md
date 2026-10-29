---
title: 'GNU AGPL-3.0 Copyleft Licensing & Attribution'
type: 'chore'
ticket: '1'
created: '2026-10-29'
status: 'built'
baseline_revision: '97e370516aa3126dc8fccf6a7f06a6594ab3eac6'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran:
  - 'quick'
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-open-source-devops/epic-open-source-devops.md'
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** Shoe Tracker lacks formal copyleft open-source licensing. Under network-deployed copyleft requirements (AGPL-3.0 Section 13), users interacting with the application over a network must have access to the corresponding source code.

**Approach:**
1. Add root `LICENSE` file containing the complete official GNU Affero General Public License v3.0 (AGPL-3.0) text.
2. Update `README.md` to state AGPL-3.0 copyleft terms and link to `LICENSE`.
3. Update `index.html` footer to include a source code availability link ("AGPL-3.0 Source") satisfying network copyleft requirements.
4. Add styling for `.footer-license-link` in `style.css`.
5. Add automated tests in `FrontendAssetTest.kt` verifying root `LICENSE` existence, AGPL-3.0 text headers, and web footer attribution links.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: static license file creation and minor footer link markup/styling with automated test verification.

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | `index.html:70` Footer only rendered in `#viewSpot`, hidden on tab change | medium | patch | Added footer to `#viewList` so users browsing historical sightings also have the copyleft source link. |
| quick | `README.md:160` Missing explicit repository link in license text | low | patch | Added explicit hyperlink to `https://github.com/mitch/shoe-tracker`. |
| quick | `index.html:73` Repo URL hardcoded to `mitch/shoe-tracker` | false | rejected | Disproved: `mitch/shoe-tracker` is the project's official Git repository as shown in git history and author credentials. |
| quick | `index.html:72` Footer divider uses Unicode bullet `•` | low | patch | Replaced with standard ASCII `|` character per `AGENTS.md`. |

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `test -f LICENSE` -- expected: File exists with non-zero size.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing AGPL-3.0 source availability link.
