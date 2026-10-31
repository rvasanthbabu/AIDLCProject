# NFR Design Patterns — Unit U2: Ideas

## Security
- Ownership guard in service layer on every idea read/mutate (object-level authz; SECURITY-08).
- SUBMITTER role guard at the API layer (`@PreAuthorize`) plus service-layer check (defense in depth).
- Bean Validation on DTOs; entity-level length limits; parameterized JPA queries (SECURITY-05).

## Reliability / Consistency
- Transactional service methods; fail-closed on state/ownership violations.
- Optimistic locking via JPA `@Version` to handle concurrent draft edits safely.
- Idea state transitions implemented as guarded mutators (legal-source-state enforced).

## Scalability / Performance
- Stateless; pagination on `listMyIdeas`.
- Indexed lookups by submitter and by (cycle, state) for dashboard reads (U4 reuses these).

## Observability
- Reuses common-core structured logging + correlation IDs; audit entries for submit via AuditLogWriter.

## Reuse
- All cross-cutting concerns (headers, error handling, logging, events) come from common-core; U2 adds no new infra components.
