---
type: story
title: "Global RFC 7807 Exception Mapper for Unhandled Server Errors"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Global RFC 7807 Exception Mapper for Unhandled Server Errors

## Description

Implements a catch-all `ExceptionMapper<Throwable>` in `ExceptionMappers.kt` returning `application/problem+json` formatted RFC 7807 problem details with status 500, a unique correlation error ID, and safe non-leaking error detail for unexpected server failures.

## Background & Rationale

Architecture decision AD-4 mandates that all error responses follow RFC 7807 problem details JSON (`application/problem+json`). While client-side syntax errors, bad JSON, and validation exceptions currently produce RFC 7807 responses, unexpected runtime exceptions (such as database timeouts or unexpected I/O failures) fall back to default Quarkus text/HTML error responses.

## Acceptance Criteria

- Add `GeneralExceptionMapper : ExceptionMapper<Throwable>` producing `application/problem+json`.
- Unhandled server exceptions return HTTP 500 with RFC 7807 JSON:
  ```json
  {
    "type": "about:blank",
    "title": "Internal Server Error",
    "status": 500,
    "detail": "An unexpected error occurred. Please try again later.",
    "error_id": "<uuid>"
  }
  ```
- Stack traces are logged to Quarkus server logs with the corresponding `error_id` but are never leaked into the client response.
- Automated tests verify that unhandled exceptions produce status 500 with RFC 7807 JSON headers and payload.
