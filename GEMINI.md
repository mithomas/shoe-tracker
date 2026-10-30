# Project Instructions - Shoe Tracker

## Change Management & Ticket Governance
- **Never make direct code edits for new ideas, features, or UI adjustments.**
- Any new feature request, behavioral change, or idea must first be captured as a story ticket (either in `_bmad-output/backlog/` or in the active epic's `tickets.toml`) via Sofia (`bmad-agent-po`).
- Code modifications may only proceed after the corresponding story has been explicitly planned and approved.
- **README Roadmap Maintenance:** Keep the `## Roadmap & Backlog` section in `README.md` up to date in a concise, high-level form reflecting only the current open backlog items, and always remove completed items once implemented.

## Tooling & BMad Scripts
- Always pass `--system-certs` to `uv run` commands (e.g., `uv run --system-certs ...`) so `uv` trusts the system TLS certificate store.
