---
id: 1
type: story
title: "Global RFC 7807 500 ExceptionMapper with Error ID Correlation & Sanitized Payload"
parent: epic-architecture-resilience
covers: [R18]
after: []
hitl: false
risk: low
---

# Global RFC 7807 500 ExceptionMapper with Error ID Correlation & Sanitized Payload

## Description

Adds catch-all ExceptionMapper<Throwable> mapping unhandled 500 server errors to application/problem+json with unique UUID error_id, internal stack trace logging, and sanitized client error messages.

## Requirements

Verify: Unhandled server exceptions return HTTP 500 RFC 7807 problem details with error_id and sanitized message without leaking internal stack traces.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-architecture-resilience/epic-architecture-resilience.md
