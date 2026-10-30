---
id: 1
type: story
title: "Redesign Sighting Confirmation Modal with Full-Size Circular Accept Button"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R43"]
sources: []
companions: []
after: []
refined: true
hitl: false
risk: low
---

# Redesign Sighting Confirmation Modal with Full-Size Circular Accept Button

## Description

Redesigns the Sighting Confirmation Modal (`#sightingModal`) as a centered floating modal card where the primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) appears first as a 210px circular button matching the size, shape, and orange color palette of the main Spot button (`#btnSpotGiant`), followed below by the captured sighting details and stacked secondary action buttons (`View on Map` and `Undo`).

## Requirements

1. **Centered Floating Modal Card with 210px Circular Orange Accept Button**
   **Given** the user taps the Spot button and the Sighting Confirmation Modal (`#sightingModal`) opens
   **When** the modal renders
   **Then** `#sightingModal` renders as a centered floating modal card (`align-items: center`, rounded corners on all four sides)
   **And** the primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) appears first as a `210px` (`width: 210px; height: 210px; border-radius: 50%`) circular button matching the orange color palette and styling of the main Spot button (`#btnSpotGiant`) so both giant circles look identical

2. **Sighting Details and Stacked Secondary Buttons Positioned Below**
   **Given** the redesigned Sighting Confirmation Modal is open
   **When** the user views the modal layout from top to bottom
   **Then** the modal header and the giant `210px` circular accept button appear first, followed below by the captured sighting metadata box (`#sightingModalDetails`: `Time`, `Location`, `Spotted By`), and finally the stacked secondary action buttons (`#btnSightingViewMap` and `#btnSightingUndo`) at the bottom

3. **Responsive Viewport Fit**
   **Given** a standard mobile viewport (`height >= 600px`, `max-width: 480px`)
   **When** the redesigned Sighting Confirmation Modal is displayed
   **Then** the centered modal card, circular accept button, sighting details, and stacked secondary action buttons fit cleanly within the viewport without clipping or hiding the `Undo` and `View on Map` controls

## Boundaries

- Must not change: Sighting creation (`POST /api/v1/sightings`), `Undo` deletion (`DELETE /api/v1/sightings/{id}`), or `View on Map` tab switching and marker focus behavior.
- Must not change: Two-line right-aligned navigational coordinate formatting (`N XX° MM,MMM'` / `E XXX° MM,MMM'`) in the sighting details box.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md

## Notes

- Decision: 2026-10-30: `#sightingModal` uses a centered floating modal card layout with the `210px` circular `#btnSightingAwesome` button first, `#sightingModalDetails` below it, and stacked `#btnSightingViewMap` and `#btnSightingUndo` buttons at the bottom.
- Decision: 2026-10-30: `#btnSightingAwesome` matches the Spot button's orange palette so both giant `210px` circular buttons look visually consistent.
