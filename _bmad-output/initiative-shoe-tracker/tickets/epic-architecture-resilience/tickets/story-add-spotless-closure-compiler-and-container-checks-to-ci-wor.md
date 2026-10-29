---
id: 6
type: story
title: "Add Spotless, Closure Compiler, and Container Checks to CI Workflow"
parent: epic-architecture-resilience
covers: [R22]
after: [5]
hitl: false
risk: low
---

# Add Spotless, Closure Compiler, and Container Checks to CI Workflow

## Description

Adds explicit CI steps for Spotless formatting verification, Closure Compiler JavaScript linting, and PR container build validation to .github/workflows/ci.yml.

## Requirements

Verify: GitHub Actions CI workflow includes spotlessCheck, lintJs, and container build steps, and is verified by DevOpsAssetTest and YAML validation.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-architecture-resilience/epic-architecture-resilience.md
