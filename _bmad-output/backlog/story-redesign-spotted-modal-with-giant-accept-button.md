---
type: story
title: "Redesign Sighting Confirmation Modal with Full-Size Circular Accept Button"
parent: ""
covers: []
sources: []
companions: []
after: []
refined: true
hitl: false
risk: low
---

# Redesign Sighting Confirmation Modal with Full-Size Circular Accept Button

## Description

Redesigns the Sighting Confirmation Modal (`#sightingModal`) so the primary accept button matches the 210px circular size and form factor of the main Spot button (ideally aligned in the same screen position for effortless single-thumb confirmation), with the captured sighting details and secondary action buttons (`View on Map` and `Undo`) positioned below it.

## Requirements

1. **Full-Size Circular Primary Accept Button**
   **Given** the user taps the Spot button and the Sighting Confirmation Modal opens
   **When** the modal renders
   **Then** the primary accept button (`Awesome!` / `Super!`) is rendered as a 210px circular button matching the size and shape of the main Spot button, positioned in the upper/main action area of the modal (ideally aligned with the underlying Spot button position so the user's thumb does not need to move)

2. **Sighting Details and Secondary Buttons Positioned Below**
   **Given** the redesigned Sighting Confirmation Modal is open
   **When** the user views the modal layout from top to bottom
   **Then** the modal header and the giant circular accept button appear first, followed below by the captured sighting metadata box (`Time`, `Location`, `Spotted By`), and finally the secondary action buttons (`View on Map` and `Undo`) at the bottom

3. **Responsive Viewport Fit**
   **Given** a standard mobile viewport (`height >= 600px`, `max-width: 480px`)
   **When** the redesigned Sighting Confirmation Modal is displayed
   **Then** the circular accept button, sighting details, and secondary action buttons fit cleanly within the viewport without clipping or hiding the `Undo` and `View on Map` controls

## Boundaries

- Must not change: Sighting creation (`POST /api/v1/sightings`), `Undo` deletion (`DELETE /api/v1/sightings/{id}`), or `View on Map` tab switching and marker focus behavior.
- Must not change: Two-line right-aligned navigational coordinate formatting (`N XX° MM,MMM'` / `E XXX° MM,MMM'`) in the sighting details box.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md

## Notes

- Decision: 2026-10-30: Aligning the modal container so the 210px accept button sits at the same vertical offset as `#btnSpotGiant` allows a second tap in the exact same spot to dismiss the modal immediately.
