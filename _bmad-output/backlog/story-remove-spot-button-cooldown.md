---
type: story
title: "Remove Spot Button Cooldown in Favor of Confirmation Modal"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Remove Spot Button Cooldown in Favor of Confirmation Modal

## Description

With the introduction of the dedicated Sighting Confirmation Modal immediately following a tap, accidental double-tapping is already blocked by the modal overlay. This story evaluates and removes the 3-second countdown cooldown ring, allowing the Spot Button to return to the active ready state as soon as the confirmation modal is dismissed.

## Background & Rationale

In the original MVP design, the 3-second cooldown countdown was designed to prevent duplicate submissions when the button was tapped multiple times in rapid succession. Since the Sighting Confirmation Modal now intercepts the screen instantly on tap and stays active until dismissed ("Awesome!" / "View on Map" / "Undo"), the modal itself serves as the natural duplicate barrier, making the 3-second button cooldown redundant.

## Acceptance Criteria

- Remove the 3-second timer and countdown ring animation from `spot.js`.
- Tapping the 210px Spot Button opens the Sighting Confirmation Modal immediately.
- Dismissing the modal restores the Spot Button to its ready state with no remaining timer lockout.
