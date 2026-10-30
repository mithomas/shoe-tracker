---
type: story
title: "Fix iOS WebKit Modal Center Button Elliptical Distortion"
parent: ""
covers: []
sources: []
companions: []
after: []
refined: true
hitl: false
risk: low
---

# Fix iOS WebKit Modal Center Button Elliptical Distortion

## Description

Fixes a layout distortion on iOS WebKit browsers (Safari and Brave on iPhone) where the giant centered confirmation button (`#btnSightingAwesome` / `.btn-modal-accept-giant`) inside the `"Shoe Spotted!"` modal renders with the correct `210px` height but a narrower width, distorting the circular button into a vertical ellipse.

## Requirements

1. **Strict 1:1 Circular Geometry on iOS WebKit (`210px` x `210px`)**
   **Given** the user spots a shoe on an iPhone using Safari or Brave (iOS WebKit) and the `"Shoe Spotted!"` confirmation modal opens
   **When** `.modal-accept-area`, `.btn-spot-container`, and `#btnSightingAwesome` (`.btn-spot-giant.btn-modal-accept-giant`) are rendered inside `#sightingModal`
   **Then** both `.btn-spot-container` and `.btn-spot-giant` enforce explicit non-shrinking `210px` x `210px` dimensions (`flex: 0 0 210px`, `min-width: 210px`, `max-width: 210px`, `min-height: 210px`, `max-height: 210px`, `aspect-ratio: 1 / 1`, `padding: 0`, `-webkit-appearance: none`, `appearance: none`) so the button is always a true `210px` circle and never compresses horizontally into an ellipse
   **And** the `226px` x `226px` SVG auto-close progress ring (`.modal-autoclose-ring`) remains concentric around the circular button

## Boundaries

- Must not change: The `210px` diameter, the `48px` top / `56px` bottom margin on `.modal-accept-area`, the `5s` SVG auto-close ring animation, or the subdued rusty-orange gradient styling of the button.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
