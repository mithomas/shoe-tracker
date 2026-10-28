---
type: story
title: "Render Existing Shoe Sightings on Spot Screen Mini-Map"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Render Existing Shoe Sightings on Spot Screen Mini-Map

## Description

Renders existing shoe sighting markers on the upper-half mini-map of the Spot screen in addition to the live user GPS position dot. As the passenger travels along the highway, nearby logged shoes appear on the live HUD radar map so travelers can see previously logged shoes in their vicinity.

## Background & Rationale

Epic 2 introduces the live tracking mini-map in the upper half of the Spot screen, primarily centered on the user's real-time position. Adding nearby historical shoe pins directly to this mini-map enhances the "road-trip radar" experience, letting passengers watch for shoes before passing them.

## Acceptance Criteria

- The `#spotMiniMap` fetches and renders nearby shoe sighting pins from `/api/v1/sightings`.
- Sighting markers on the mini-map use compact dot icons (Signal Blue `#3B82F6` for user's own shoes, Slate `#94A3B8` for other users) to keep the HUD radar clean and uncluttered.
- Tapping a shoe pin on the mini-map displays a concise summary or provides a shortcut to open the full Map view.
- Newly spotted shoes appear on the mini-map immediately upon logging.
