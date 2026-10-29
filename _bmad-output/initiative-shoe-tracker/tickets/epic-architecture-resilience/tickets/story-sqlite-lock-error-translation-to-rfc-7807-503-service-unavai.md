---
id: 3
type: story
title: "SQLite Lock Error Translation to RFC 7807 503 Service Unavailable"
parent: epic-architecture-resilience
covers: [R20]
after: [2]
hitl: false
risk: low
---

# SQLite Lock Error Translation to RFC 7807 503 Service Unavailable

## Description

Adds SQLiteExceptionMapper translating org.sqlite.SQLiteException (such as SQLITE_BUSY or write contention timeouts) into RFC 7807 503 Service Unavailable with Retry-After header.

## Requirements

Verify: Simulated or real SQLite lock timeouts return HTTP 503 application/problem+json with Retry-After: 1 header.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-architecture-resilience/epic-architecture-resilience.md
