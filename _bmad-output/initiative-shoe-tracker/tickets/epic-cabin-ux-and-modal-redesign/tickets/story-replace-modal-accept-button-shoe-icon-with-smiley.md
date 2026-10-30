---
id: 7
type: story
title: "Replace Shoe Icon in Sighting Confirmation Modal Accept Button with Smiley"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R43"]
sources: []
companions: []
after: [1]
refined: true
hitl: false
risk: low
---

# Replace Shoe Icon in Sighting Confirmation Modal Accept Button with Smiley

## Description

Replaces the shoe icon (`👟`) inside the Sighting Confirmation Modal's primary `210px` circular accept button (`#btnSightingAwesome`) with a smiley icon (`😊`) so the confirmation button visually celebrates the logged sighting while keeping the main `#btnSpotGiant` button's shoe icon unchanged.

## Requirements

1. **Smiley Icon on Sighting Confirmation Modal Accept Button**
   **Given** the Sighting Confirmation Modal (`#sightingModal`) opens after a sighting is logged
   **When** the `210px` circular `#btnSightingAwesome` accept button renders
   **Then** its `.icon` span displays a smiley emoji (`😊`) above the localized `#btnSightingAwesomeLabel` (`"Awesome!"` / `"Super!"`) instead of `👟`

2. **Preserve Main Spot Button Shoe Icon and Modal Title**
   **Given** the user views the Spot screen or the Sighting Confirmation Modal header
   **When** `#btnSpotGiant` and `.modal-title` render
   **Then** `#btnSpotGiant` continues to display `👟` and `.modal-title` continues to display `👟 Shoe Spotted!` / `👟 Schuh gesehen!`

3. **Automated Frontend Asset Test Alignment**
   **Given** `FrontendAssetTest.kt` verifies the `#sightingModal` accept button markup
   **When** `./gradlew check test` runs
   **Then** tests assert that `#btnSightingAwesome` renders `<span class="icon">😊</span>`

## Boundaries

- Must not change: `#btnSpotGiant` shoe icon (`👟`), `.modal-title` text (`👟 Shoe Spotted!`), or `#btnSightingAwesome` `210px` circular dimensions and orange styling.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements

## Notes

- Decision: 2026-10-30: Uses `😊` inside `#btnSightingAwesome .icon` while keeping `👟` on `#btnSpotGiant .icon`.
