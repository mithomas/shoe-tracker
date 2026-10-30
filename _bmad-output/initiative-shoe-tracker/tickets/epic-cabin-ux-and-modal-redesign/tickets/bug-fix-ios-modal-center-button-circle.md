---
id: 10
type: bug
title: "Fix Distorted Elliptical Center Button in Sighting Modal on iOS Safari and Brave"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R43", "R44"]
sources: []
companions: []
after: [1, 2, 3, 7, 8]
refined: true
hitl: false
risk: low
---

# Fix Distorted Elliptical Center Button in Sighting Modal on iOS Safari and Brave

## Description

When opening the `"Shoe Spotted!"` confirmation modal (`#sightingModal`) after spotting a shoe on iPhone (Safari and Brave / iOS WebKit), the center accept button (`#btnSightingAwesome`) is distorted into a vertical ellipse - the height (`210px`) is correct, but the horizontal width shrinks narrower than `210px`. This fix enforces a rigid `1:1` (`210px x 210px`) non-shrinking circular geometry on `.btn-spot-container`, `.btn-spot-giant`, and `#btnSightingAwesome` across iOS WebKit browsers while keeping the `226px x 226px` `.modal-autoclose-ring` SVG progress outline concentrically aligned.

## Requirements

1. **Non-Shrinking 1:1 Circular Button Geometry on iOS WebKit**
   **Given** the user opens `#sightingModal` after spotting a shoe in Safari or Brave on iPhone (iOS WebKit)
   **When** `.modal-accept-area`, `.btn-spot-container`, and `#btnSightingAwesome` (`.btn-spot-giant.btn-modal-accept-giant`) are rendered inside the flex layout
   **Then** `.btn-spot-container` and `#btnSightingAwesome` enforce explicit `width: 210px; min-width: 210px; max-width: 210px; height: 210px; min-height: 210px; max-height: 210px; flex: 0 0 210px; aspect-ratio: 1 / 1; padding: 0; box-sizing: border-box; -webkit-appearance: none; appearance: none; border-radius: 50%;` so the button is a true `210px x 210px` circle and never compresses horizontally into an ellipse

2. **Concentric Alignment of the 226px Auto-Close SVG Ring on iOS WebKit**
   **Given** `<svg class="modal-autoclose-ring" viewBox="0 0 226 226" width="226" height="226">` is positioned inside `.btn-spot-container` around `#btnSightingAwesome`
   **When** `#sightingModal` opens on iOS WebKit
   **Then** `.modal-autoclose-ring` uses explicit `top: -8px; left: -8px; width: 226px; height: 226px;` with `pointer-events: none` so the SVG never participates in flex width calculation or distorts the sibling `<button>`

3. **Consistency Between Primary Spot Button and Modal Accept Button**
   **Given** `#btnSpotGiant` in the main Spot view and `#btnSightingAwesome` in `#sightingModal` share `.btn-spot-container` and `.btn-spot-giant`
   **When** rendered on iOS Safari, iOS Brave, Android Chrome, and desktop browsers in both English (`"Awesome!"`) and German (`"Super!"`)
   **Then** both buttons maintain identical `210px x 210px` circular dimensions (`offsetWidth === offsetHeight === 210`)

## Boundaries

- Must not change: `210px` diameter target, `20-second` auto-close countdown behavior, or toned-down orange palette and vertical spacing (`margin: 48px 0 56px 0`) around `.modal-accept-area`.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md

## Notes

- Decision: 2026-10-30: Root cause on iOS WebKit (used by both Safari and Brave on iPhone) had two compounding parts:
  1. **Stale Static Asset Cache + 2-Child Flex Container in Modal:** On the normal Spot page, `.btn-spot-container` only has 1 child (`#btnSpotGiant`). In `#sightingModal`, `.btn-spot-container` has 2 children (`<svg class="modal-autoclose-ring">` and `<button id="btnSightingAwesome">`). Because `/css/style.css` and `/js/modal.js` had no cache-busting query string (`?v=8.10`) or `Cache-Control: no-cache, must-revalidate` HTTP header, iOS Safari/Brave fetched the updated `index.html` while serving cached pre-Epic-8 `style.css` and `modal.js`. Without `.modal-autoclose-ring { position: absolute }`, the `<svg>` was `position: static` with `fill="none"` and no inline `stroke`, acting as an invisible 2nd flex item in a `210px` row and squashing `<button id="btnSightingAwesome">` horizontally into an ~`103px x 210px` ellipse while showing no timer ring.
  2. **iOS WebKit SVG CSS Transition Skip on Visibility Change:** Even with updated CSS/JS, iOS WebKit skips CSS `transition: stroke-dashoffset 20000ms linear` on an `<svg><circle>` when triggered in the same frame that `#sightingModal` transitions from `visibility: hidden` to `visibility: visible`. Fixed by changing `.btn-spot-container` to `display: block`, adding inline `style` and SVG presentation attributes (`stroke="#F97316"`, `stroke-width="6"`, `position:absolute`), driving `stroke-dashoffset` via a `requestAnimationFrame` loop in `modal.js`, and adding `?v=8.10` asset versioning + `Cache-Control: no-cache, must-revalidate` in `application.properties`.
