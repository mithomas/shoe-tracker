---
id: 2
type: story
title: "Unified SQLState Contention ExceptionMapper and Dynamic Health Reporting"
parent: epic-database-modernization
covers: [R24, R25]
after: [1]
hitl: false
risk: low
---

# Unified SQLState Contention ExceptionMapper and Dynamic Health Reporting

## Description

Replaces SQLiteExceptionMapper with DatabaseContentionExceptionMapper translating SQLState 40001/57P01 to RFC 7807 503, and updates DatabaseHealthCheck to report live databaseProductName.

## Requirements

Verify: Simulated lock contention returns HTTP 503 with Retry-After: 1 header, and /q/health/ready dynamically reports databaseProductName.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
