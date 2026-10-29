---
type: story
title: "Display Total and Personal Sighting Counters"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Display Total and Personal Sighting Counters

## Description

Displays prominent counters showing the total number of recorded shoe sightings across the network and the number of sightings spotted by the active user (e.g., "12 Total | 4 Mine"), keeping passengers engaged with their personal contribution and the collective community tally.

## Background & Rationale

While the List view displays a count badge (`listCountBadge`), users currently lack quick visibility into their overall spotting statistics while on the Spot screen or navigating the Map. Displaying both the total community count and the user's personal count reinforces gamification and gives immediate feedback after tapping the spot button.

## Acceptance Criteria

- Add a stats/counter widget (e.g. in the header or persistent status area) showing both total sightings count and personal sightings count.
- Counter dynamically increments immediately upon logging a new sighting (`sighting-added` event) or when undoing a sighting (`sighting-deleted` event).
- Counter updates immediately when the user changes their name in onboarding or user chip (`user-changed` event).
- Sighting counts are calculated accurately from the API payload and cached local telemetry.
- Automated tests verify counter formatting, edge cases (0 sightings, 1 sighting, plural), and live event reactivity.
