---
id: 2
type: story
title: "Eager Prepared Statements & Startup Fail-Fast Validation in SightingRepository"
parent: epic-architecture-resilience
covers: [R19]
after: [1]
hitl: false
risk: low
---

# Eager Prepared Statements & Startup Fail-Fast Validation in SightingRepository

## Description

Replaces lazy JDBC connection and PreparedStatement properties in SightingRepository with eager @PostConstruct initialization and fail-fast SELECT 1 validation.

## Requirements

Verify: SightingRepository compiles all SQL statements upon CDI initialization and fails fast at startup if connection or queries are invalid.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-architecture-resilience/epic-architecture-resilience.md
