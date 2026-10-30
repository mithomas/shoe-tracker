---
id: 2
type: story
title: "Add 20-Second Auto-Close Timer with Progress Outline to Sighting Confirmation Modal Accept Button"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R44"]
sources: []
companions: []
after: [1, 7]
refined: true
hitl: false
risk: low
---

# Add 20-Second Auto-Close Timer with Progress Outline to Sighting Confirmation Modal Accept Button

## Description

Adds a 20-second automatic dismissal timer to the Sighting Confirmation Modal's primary accept button (`#btnSightingAwesome`), visually indicated by an SVG circular progress outline ring around the `210px` circular button perimeter that drains clockwise (`100% -> 0%`) over 20 seconds and automatically accepts and closes the modal when the countdown completes.

## Requirements

1. **20-Second Auto-Close Countdown**
   **Given** a shoe sighting is logged and the Sighting Confirmation Modal (`#sightingModal`) opens
   **When** 20 seconds (`20000ms`) elapse without user interaction
   **Then** the modal automatically closes (equivalent to tapping `#btnSightingAwesome`) and returns the user to the active Spot screen

2. **Clockwise Draining SVG Circular Progress Outline Around Accept Button**
   **Given** the Sighting Confirmation Modal is open and the 20-second auto-close timer is running
   **When** the user views the `210px` circular `#btnSightingAwesome` button
   **Then** an SVG `<circle>` progress ring around the `210px` button perimeter animates smoothly clockwise from `100%` to `0%` over the 20-second duration to show the remaining time before auto-close

3. **Timer Cancellation and Reset on Manual Action, Undo Failure, or Re-Open**
   **Given** the 20-second auto-close timer is active
   **When** the user manually taps `#btnSightingAwesome`, `#btnSightingViewMap`, or `#btnSightingUndo` before 20 seconds elapse (or opens a new sighting modal right after)
   **Then** the active timer and SVG outline animation are immediately cleared and reset so a stale timer never closes a newly opened modal early
   **And** if `Undo` fails due to a network error, the modal remains open with the timer cancelled so the error state stays visible

## Boundaries

- Must not change: Immediate manual dismissal on tapping Accept, immediate deletion on tapping `Undo`, or map navigation on tapping `View on Map`.
- Must not change: Tapping the dark modal backdrop outside `.modal-sheet` does not dismiss the modal.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md

## Notes

- Decision: 2026-10-30: Uses an SVG circular progress ring around the `210px` `#btnSightingAwesome` perimeter that drains clockwise (`100% -> 0%`) over 20 seconds, cancelling on any modal button tap and keeping the modal open if `Undo` fails.
