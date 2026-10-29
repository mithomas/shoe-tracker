---
title: 'Architecture Package Realignment (Service Layer Separation)'
type: 'refactor'
ticket: '8'
created: '2026-10-29'
status: 'built'
baseline_revision: '05777b3255c000f78b97240983e6674ac6bd40d6'
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

**Problem:** `SightingService` resides inside the `org.shoetracker.domain` package despite being an application orchestration service injecting `SightingRepository` and depending on `org.shoetracker.api.dto.*`. This violates DDD boundaries. Furthermore, the rationale for placing frontend web assets in `src/main/resources/META-INF/resources/` is undocumented.

**Approach:**
1. Move `SightingService` from `org.shoetracker.domain` to a new `org.shoetracker.service` package (`src/main/kotlin/org/shoetracker/service/SightingService.kt`).
2. Update callers (`SightingResource.kt`) to import `org.shoetracker.service.SightingService`.
3. Verify `org.shoetracker.domain` contains only pure domain models (`Sighting.kt`, `GeoLocation`, `UUIDv7.kt`) with zero outward dependencies on repository or DTO layers.
4. Document the Quarkus `META-INF/resources/` static web asset convention in `architecture-shoe-tracker.md` and `README.md`.
5. Verify all tests compile and pass cleanly via `./gradlew test`.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: package relocation, caller import updates, architecture documentation, and test verification touching under 60 lines.

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | `SightingService.kt:3` Wildcard import `import org.shoetracker.api.dto.*` | low | patch | Replaced wildcard import with explicit DTO imports (`CreateSightingRequest`, `GeoLocationDto`, `SightingListResponse`, `SightingResponse`, `UserDto`). |
| quick | `/tmp/review_diff_story_4_6.patch` Diff path in `/tmp` instead of session temp dir | low | patch | Generated review diffs under project temporary workspace directory `/home/tom/.gemini/tmp/bmad/`. |

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and asset tests pass cleanly.
- `git grep "package org.shoetracker.domain" src/main/kotlin` -- expected: Only pure entity and utility files.
