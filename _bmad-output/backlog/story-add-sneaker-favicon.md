---
type: story
title: "Add Sneaker Favicon and Web Application Icon"
parent: "epic-spotting-polish"
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Add Sneaker Favicon and Web Application Icon

## Description

Adds a sneaker favicon (`favicon.ico` and vector `favicon.svg`) in `src/main/resources/META-INF/resources/` and declares corresponding `<link rel="icon">` tags in `index.html` to eliminate browser 404 errors and provide a branded application icon in browser tabs and mobile bookmarks.

## Requirements

- Add `/favicon.svg` (vector sneaker icon matching logo aesthetic) and `/favicon.ico` (standard fallback) into `src/main/resources/META-INF/resources/`.
- Include `<link rel="icon" type="image/svg+xml" href="/favicon.svg">` and `<link rel="alternate icon" href="/favicon.ico">` in `<head>` of `index.html`.
- Automated test asserts `/favicon.ico` and `/favicon.svg` are served by Quarkus with HTTP 200 OK.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-spotting-polish/epic-spotting-polish.md
