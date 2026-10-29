---
id: 4
type: story
title: "Quarkus SmallRye Health Liveness & Readiness Probes"
parent: epic-architecture-resilience
covers: [R21]
after: [3]
hitl: false
risk: low
---

# Quarkus SmallRye Health Liveness & Readiness Probes

## Description

Adds quarkus-smallrye-health dependency and configures /q/health/live and /q/health/ready probes with SQLite database connectivity checks for container orchestration.

## Requirements

Verify: GET /q/health/live and /q/health/ready return HTTP 200 UP with database check status.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-architecture-resilience/epic-architecture-resilience.md
