---
id: 9
type: bug
title: "Fix GitHub Source Repository Link URL to Point to mithomas/shoe-tracker"
parent: "epic-versions-and-build"
covers: ["R39"]
after: [4]
assignee: ""
refined: true
hitl: false
risk: low
severity: P2
---

# Fix GitHub Source Repository Link URL to Point to mithomas/shoe-tracker

## Description

The `AGPL-3.0 Source` links in the web application footer (`index.html` in both the Spot view and List view) and the GitHub repository link in the `## License` section of `README.md` currently point to `https://github.com/mitch/shoe-tracker`. They should point to the official repository at `https://github.com/mithomas/shoe-tracker`.

## Reproduction

1. Open the Shoe Tracker web interface (`/` in Spot view or List view) and inspect the `AGPL-3.0 Source` link in `.app-footer`, or inspect the `## License` section at the bottom of `README.md`.
2. **Actual:** The links point to `https://github.com/mitch/shoe-tracker`.
3. **Expected:** All source repository links point to `https://github.com/mithomas/shoe-tracker`.

## Cause Hypothesis

When Story `7.4` updated Docker Hub image references from `mitch/shoe-tracker` to `mithomas/shoe-tracker`, the GitHub repository URLs in `index.html` (lines 84 and 135) and `README.md` (line 213) were left pointing to `https://github.com/mitch/shoe-tracker`.

## Acceptance Criteria

1. **Web Footer and README Source Links Point to `https://github.com/mithomas/shoe-tracker`**
   **Given** the user views the `AGPL-3.0 Source` footer links in `index.html` (Spot view and List view) or the `Shoe Tracker GitHub Repository` link in `README.md`
   **When** the link `href` / Markdown URL is inspected
   **Then** every source link points to `https://github.com/mithomas/shoe-tracker` and zero occurrences of `mitch/shoe-tracker` remain in `index.html` or `README.md`
2. **Tests Cover the Updated Source Link URL**
   **Given** the automated test suite (`FrontendAssetTest.kt` and `DevOpsAssetTest.kt`)
   **When** `./gradlew check test` runs
   **Then** tests assert that `index.html` and `README.md` contain `https://github.com/mithomas/shoe-tracker` and do not contain `mitch/shoe-tracker`
3. **Or: no change is needed, with proof**
   **Given** the reproduction
   **When** it is run on the current code
   **Then** the expected behavior already holds, or the report was mistaken, with the evidence recorded in Notes - this supersedes 1 and 2

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md, section Requirements

## Notes

- Decision: 2026-10-30: Both GitHub repository (`https://github.com/mithomas/shoe-tracker`) and Docker Hub repository (`mithomas/shoe-tracker`) use the `mithomas` owner namespace.
