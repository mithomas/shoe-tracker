---
id: 3
type: story
title: "Configurable Multi-Target Datasource (MySQL, H2 File, or H2 In-Memory)"
parent: epic-database-modernization
covers: [R26]
after: [1]
hitl: false
risk: low
---

# Configurable Multi-Target Datasource (MySQL, H2 File, or H2 In-Memory)

## Description

Adds quarkus-jdbc-mariadb driver and enables runtime environment variable selection between external MySQL/MariaDB, H2 file persistence (/data), or H2 in-memory.

## Requirements

Verify: Application boots cleanly against default H2 file, H2 in-memory, and simulated external MySQL/MariaDB datasource configurations.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
