---
title: 'Global RFC 7807 500 ExceptionMapper with Error ID Correlation & Sanitized Payload'
type: 'feature'
ticket: '1'
created: '2026-10-29'
status: 'built'
baseline_revision: '1dc8e79c4601c0cbc6beacfa8cff04e7bc7ae0b1'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran:
  - 'quick'
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-architecture-resilience/epic-architecture-resilience.md'
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** Currently, `ExceptionMappers.kt` only maps client-side 400 Bad Request exceptions (`IllegalArgumentException`, `JsonProcessingException`, `ProcessingException`, `WebApplicationException`). Unhandled server runtime exceptions (e.g., unexpected database failures, null pointer errors) result in non-standard error payloads or raw internal server errors, leaking stack traces and failing the RFC 7807 uniform API contract.

**Approach:**
1. Extend `ProblemDetails` DTO to support an optional `@JsonProperty("error_id") errorId: String? = null` field.
2. Implement `@Provider class ThrowableExceptionMapper : ExceptionMapper<Throwable>` returning HTTP 500 `application/problem+json`.
3. In `ThrowableExceptionMapper`, generate a unique UUID `error_id`, log the full exception stack trace internally tagged with `error_id`, and sanitize the client-facing `detail` field so internal implementation details are not leaked.
4. Add comprehensive unit and integration tests verifying 500 RFC 7807 response status, content type, error ID presence, and sanitized message.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: Surgical addition of global catch-all RFC 7807 500 exception mapper and ProblemDetails DTO extension.
- Extended `ProblemDetails` in `SightingDtos.kt` with `@JsonProperty("error_id") @JsonInclude(NON_NULL) val errorId: String? = null`.
- Added `ThrowableExceptionMapper` in `ExceptionMappers.kt` logging stack traces tagged with UUID `error_id` and responding with sanitized 500 ProblemDetails.
- Added test fault controller `TestFaultResource.kt` under `src/test/kotlin` to simulate unhandled server exceptions without altering production endpoints.
- Added tests in `SightingResourceTest.kt` verifying RFC 7807 JSON shape, UUID matching, and suppression of raw exception leakage.

## Plan Change Log

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | Error sanitization verification | pass | - | Verified that `problem.detail` does not leak internal exception message and references `error_id`. |
| quick | Regression across existing 400 mappers | pass | - | Null `errorId` omitted cleanly via `@JsonInclude(NON_NULL)`; existing 400 tests pass without modification. |

## Verification

**Commands:**
- `./gradlew test --tests org.shoetracker.api.SightingResourceTest` -- expected: All API resource tests pass including 500 mapper verification.
- `./gradlew check test spotlessCheck lintJs` -- expected: Full test and hygiene suite passes with zero violations.
