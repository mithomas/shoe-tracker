---
id: 2
type: story
title: "Add 20-Second Auto-Close Timer with Progress Outline to Sighting Confirmation Modal Accept Button"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R44"]
sources: []
companions: []
after: [1]
refined: true
hitl: false
risk: low
---

# Add 20-Second Auto-Close Timer with Progress Outline to Sighting Confirmation Modal Accept Button

## Description

Adds a 20-second automatic dismissal timer to the Sighting Confirmation Modal's primary accept button, visually indicated by an animated outline/progress ring around the main accept button that automatically accepts and closes the modal when the countdown completes.

## Requirements

1. **20-Second Auto-Close Countdown**
   **Given** a shoe sighting is logged and the Sighting Confirmation Modal (`#sightingModal`) opens
   **When** 20 seconds elapse without user interaction
   **Then** the modal automatically closes (equivalent to tapping the accept button) and returns the user to the active Spot screen

2. **Animated Outline Progress Indicator Around Accept Button**
   **Given** the Sighting Confirmation Modal is open and the 20-second auto-close timer is running
   **When** the user views the primary accept button
   **Then** a visible outline/ring around the main accept button animates smoothly over the 20-second duration to show the remaining time before auto-close

3. **Timer Cancellation and Reset on Manual Action or Re-Open**
   **Given** the 20-second auto-close timer is active
   **When** the user manually taps the accept button, `View on Map`, or `Undo` before 20 seconds elapse (or logs a subsequent sighting right after)
   **Then** the active timer and outline animation are immediately cleared and reset so a stale timer never closes a newly opened modal early

## Boundaries

- Must not change: Immediate manual dismissal on tapping Accept, immediate deletion on tapping `Undo`, or map navigation on tapping `View on Map`.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md

## Notes

- Decision: 2026-10-30: Designed to pair with the circular accept button redesign (`story-redesign-spotted-modal-with-giant-accept-button.md`), using an SVG circular stroke or CSS outline ring around the circular button perimeter.
