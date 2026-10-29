---
title: 'Zero-Node Pure-JVM Linting and Formatting Suite'
type: 'chore'
ticket: '7'
created: '2026-10-29'
status: 'built'
baseline_revision: 'f201b02f38a50eec0610d066cc4af24edac053ad'
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

**Problem:** The repository lacks automated style enforcement and static linting for Kotlin and frontend web assets. Without automated formatting in `./gradlew check`, code style drift and whitespace discrepancies can accumulate.

**Approach:**
1. In `build.gradle.kts`, apply the Spotless plugin (`com.diffplug.spotless`) with:
   - `kotlin`: ktlint for all Kotlin source (`src/**/*.kt`).
   - `kotlinGradle`: ktlint for Gradle Kotlin DSL scripts (`*.gradle.kts`).
   - `format("web")`: pure-JVM formatting for HTML, CSS, and JS web assets (`src/main/resources/META-INF/resources/**/*.{html,css,js}`) with 2-space indentation, trailing whitespace trimming, and trailing newline enforcement.
2. In `build.gradle.kts`, configure pure-JVM JavaScript static analysis via Google Closure Compiler (`com.google.javascript:closure-compiler`) in a `lintJs` task wired into `./gradlew check`.
3. Apply baseline formatting across the project via `./gradlew spotlessApply`.
4. Ensure `./gradlew check` and `./gradlew spotlessCheck` pass cleanly on local and CI environments with zero Node.js dependencies.
5. Add automated tests in `DevOpsAssetTest.kt` verifying Spotless and lint configuration.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: Spotless and Closure Compiler Gradle configuration, baseline formatting application, and automated test assertions.

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | `index.html` & `spot.js` contained trailing whitespace | medium | patch | Applied `./gradlew spotlessApply` to entire codebase, establishing zero-violation baseline. |
| quick | `build.gradle.kts` redundant `implementation("io.quarkus:quarkus-rest")` | low | patch | Removed redundant dependency already brought in by `quarkus-rest-jackson`. |
| quick | `lintJs` task lacked output declarations and deterministic input ordering | medium | patch | Added incremental output marker (`reports/lintJs/marker.txt`), sorted input files deterministically, and guarded against empty file sets. |
| quick | `build.gradle.kts` used deprecated `indentWithSpaces(2)` | low | patch | Switched to `leadingTabsToSpaces(2)` per Spotless 7 recommendation. |

## Verification

**Commands:**
- `./gradlew check test` -- expected: All checks, Spotless validation, JS linting, and tests pass cleanly.
- `./gradlew spotlessCheck` -- expected: Zero formatting violations across Kotlin and web assets.
- `./gradlew lintJs` -- expected: Zero JavaScript static analysis errors on pure JVM.
