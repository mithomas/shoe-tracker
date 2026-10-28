---
type: story
title: "Dedicated User Profiles, Accounts, and Authentication"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Dedicated User Profiles, Accounts, and Authentication

## Description

Evolves the MVP's lightweight `user_id` string model into a dedicated user profile and authentication system (such as LDAP or OAuth).

## Background & Architecture Comment

During the architecture review for the MVP, the following decisions were locked:
1. **MVP Simplicity:** `user_id` is used everywhere on the wire and in SQLite, storing the entered name directly. The frontend maps this to "Enter your name".
2. **Payload Asymmetry for Extensibility:** `POST /api/v1/sightings` accepts a flat `user_id`, while `GET /api/v1/sightings` and responses embed user identity inside a `user: { id: "..." }` object.
3. **Evolution Path:** When implementing this backlog item:
   - Expand the embedded `user` object with profile fields (`display_name`, `email`, `role`, `avatar_url`) without altering the root sighting record schema.
   - Decouple the persistent UUID/LDAP `user_id` from the changeable display name.
   - Introduce dedicated user management endpoints (e.g. `/api/v1/users`) only when dedicated profile management or authentication is integrated.

## Boundaries

- Sighting ingestion and map rendering must remain backward-compatible when the user object is enriched.
