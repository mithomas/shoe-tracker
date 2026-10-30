---
id: 9
type: story
title: "Remove Duplicate 'No shoes spotted yet!' Text from Second Line of List View Empty State"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R48"]
sources: []
companions: []
after: [1, 2, 3, 4, 5, 6, 7, 8]
refined: true
hitl: false
risk: low
---

# Remove Duplicate 'No shoes spotted yet!' Text from Second Line of List View Empty State

## Description

Removes the redundant `"No shoes spotted yet!"` (`"Noch keine Schuhe gemeldet!"`) prefix from the second line (`<p data-i18n="list.emptyBody">`) of `#listEmptyCard` in the List view (`#viewList`) so `"No shoes spotted yet!"` appears only once in the `<h3 data-i18n="list.emptyTitle">` heading, followed by `"Tap the Spot button to be the first."` (`"Tippe auf den Button für die erste Meldung."`) on the second line.

## Requirements

1. **Deduplicate List Empty-State Subtitle Copy (`list.emptyBody`)**
   **Given** the user opens the `[List]` tab (`#viewList`) when zero sightings exist
   **When** `#listEmptyCard` renders in English or German
   **Then** `<h3 data-i18n="list.emptyTitle">` displays `"No shoes spotted yet!"` (EN) / `"Noch keine Schuhe gemeldet!"` (DE)
   **And** `<p data-i18n="list.emptyBody">` displays `"Tap the Spot button to be the first."` (EN) / `"Tippe auf den Button für die erste Meldung."` (DE) without repeating `"No shoes spotted yet!"` / `"Noch keine Schuhe gemeldet!"`

2. **Automated Frontend Asset Test Alignment**
   **Given** `FrontendAssetTest.kt` verifies `index.html` and `js/i18n.js`
   **When** `./gradlew check test` runs
   **Then** tests assert that `list.emptyTitle` and `list.emptyBody` render without the duplicate `"No shoes spotted yet!"` / `"Noch keine Schuhe gemeldet!"` prefix on the second line

## Boundaries

- Must not change: `<h3 data-i18n="list.emptyTitle">` (`"No shoes spotted yet!"` / `"Noch keine Schuhe gemeldet!"`) or `#listEmptyOwnCard` (`list.emptyOwnBody`, which has no `<h3>` heading).

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
