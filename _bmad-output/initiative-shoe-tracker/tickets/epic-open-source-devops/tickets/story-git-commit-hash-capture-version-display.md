---
id: 2
type: story
title: "Git Commit Hash Capture & Version Display"
parent: epic-open-source-devops
covers: [R13]
after: [1]
hitl: false
risk: low
---

# Git Commit Hash Capture & Version Display

## Description

Captures Git short hash during Gradle and Docker builds, exposes it via /api/v1/version, and renders it in the web UI footer.

## Requirements

Verify: Footer badge and /api/v1/version output exact 7-character commit hash.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-open-source-devops/epic-open-source-devops.md
