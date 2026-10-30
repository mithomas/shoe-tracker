---
id: 3
type: story
title: "Tone Down Spot Button Brightness and Glow"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R45"]
sources: []
companions: []
after: [1, 2]
refined: true
hitl: false
risk: low
---

# Tone Down Spot Button Brightness and Glow

## Description

Reduces the luminance and outer glow intensity of the giant 210px Spot button so it is less glaring during evening and night Autobahn travel while remaining clearly distinguishable as the primary action target.

## Requirements

1. **Subdued Ready-State Button Gradient and Ambient Glow**
   **Given** GPS is ready and the 210px Spot button is in its active ready state
   **When** the Spot screen is displayed
   **Then** the button uses a warmer, toned-down orange/amber fill gradient and a softer outer `box-shadow` halo with reduced opacity and spread compared to the current high-glare styling

2. **Preserved Contrast and State Differentiation**
   **Given** the toned-down Spot button styling is active in both dark and light themes
   **When** the button transitions between Ready, Pressed, Waiting for GPS, and No GPS states
   **Then** the white shoe icon and label maintain WCAG AA contrast (at least 4.5:1) against the button background, and the Ready state remains visually distinct from the muted slate disabled states

## Boundaries

- Must not change: 210px circular button dimensions, label localization (`SHOE SPOTTED!` / `Schuh!`), or tap-to-log interaction behavior.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
