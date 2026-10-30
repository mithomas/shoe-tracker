---
id: 8
type: story
title: "Increase Vertical Spacing Above and Below Sighting Modal Accept Button"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R43", "R44"]
sources: []
companions: []
after: [1, 2, 3, 7]
refined: true
hitl: false
risk: low
---

# Increase Vertical Spacing Above and Below Sighting Modal Accept Button

## Description

Increases the vertical spacing (margin/padding) above and below the giant `210px` circular accept button container (`.modal-accept-area`) inside the Sighting Confirmation Modal (`#sightingModal`) so the circular button, its `226px` SVG auto-close progress ring (`.modal-autoclose-ring`), and its ambient outer glow have generous visual breathing room separating them from `.modal-subtitle` above and `#sightingModalDetails` below.

## Requirements

1. **Generous Vertical Clearance Above and Below `.modal-accept-area`**
   **Given** the Sighting Confirmation Modal (`#sightingModal`) is open
   **When** the `210px` circular `#btnSightingAwesome` button and its `226px` `.modal-autoclose-ring` SVG progress outline are rendered inside `.modal-accept-area`
   **Then** `.modal-accept-area` provides increased top and bottom vertical spacing (`margin: 24px 0 28px 0`) so neither the SVG ring nor the button's ambient glow crowds `.modal-subtitle` above or `#sightingModalDetails` below

2. **Scrollable Modal Card Resilience on Compact Viewports**
   **Given** the increased vertical spacing around `.modal-accept-area`
   **When** the modal is viewed on compact mobile viewports
   **Then** `.modal-sheet.modal-card-centered` remains cleanly contained within `max-height: calc(100vh - 32px)` with `overflow-y: auto`

## Boundaries

- Must not change: `210px` circular dimensions of `#btnSightingAwesome`, `226px` `.modal-autoclose-ring` geometry, or button order inside `#sightingModal`.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md

## Notes

- Decision: 2026-10-30: Increases vertical spacing above and below `.modal-accept-area` (`margin: 24px 0 28px 0`) in `css/style.css` so the `210px` accept button and its `226px` progress ring have clear visual separation from the subtitle and details box.
