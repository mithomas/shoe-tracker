---
id: 3
type: story
title: "Tone Down Spot Button Brightness and Glow"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R45"]
sources: []
companions: []
after: [1, 2, 7]
refined: true
hitl: false
risk: low
---

# Tone Down Spot Button Brightness and Glow

## Description

Reduces the luminance and outer glow intensity of the giant `210px` circular orange buttons (`#btnSpotGiant` and `#btnSightingAwesome`) identically across both Dark and Light themes so they are less glaring during evening and night Autobahn travel while remaining clearly distinguishable as the primary action target.

## Requirements

1. **Subdued Ready-State Button Gradient and Ambient Glow Across Both Themes**
   **Given** GPS is ready and the `210px` circular button (`.btn-spot-giant` / `#btnSpotGiant` and `#btnSightingAwesome`) is in its active state
   **When** the Spot screen or Sighting Confirmation Modal is displayed in either Dark or Light theme
   **Then** the button uses a warmer, toned-down orange/amber fill gradient and a softer outer `box-shadow` halo with reduced opacity and spread applied identically across both Dark and Light themes

2. **Preserved Contrast and State Differentiation**
   **Given** the toned-down orange button styling is active in both Dark and Light themes
   **When** `#btnSpotGiant` transitions between Ready, Pressed, Waiting for GPS, and No GPS states
   **Then** the white shoe icon and label maintain WCAG AA contrast (at least 4.5:1) against the button background, and the Ready state remains visually distinct from the muted slate disabled states

## Boundaries

- Must not change: 210px circular button dimensions, label localization (`SHOE SPOTTED!` / `Schuh!`), or tap-to-log interaction behavior.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md

## Notes

- Decision: 2026-10-30: The toned-down orange gradient and reduced outer `box-shadow` glow are applied identically across both Dark and Light themes and shared by `#btnSpotGiant` and `#btnSightingAwesome`.
