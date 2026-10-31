# NFR Requirements — Unit U2: Ideas

Inherits platform NFRs (99.95% availability, single-region multi-zone, enterprise scale). U2-specific points:

## Performance & Scalability
- **U2-NFR-PERF-1**: Submission spikes near cycle open/close; write path (draft save, submit) p95 < 300 ms.
- **U2-NFR-SCALE-1**: Stateless; scales with backend replicas. Idea listing is paginated.

## Security (Security Baseline)
- **U2-NFR-SEC-1**: Object-level ownership checks on every idea access (prevent IDOR — SECURITY-08).
- **U2-NFR-SEC-2**: Input validation + length bounds on all fields; parameterized persistence (SECURITY-05).
- **U2-NFR-SEC-3**: Role check (SUBMITTER) for create/submit (SECURITY-08).

## Reliability
- **U2-NFR-REL-1**: Transactional writes; fail closed on validation/state errors (SECURITY-15).
- **U2-NFR-REL-2**: Optimistic locking (version) to avoid lost updates on concurrent draft edits.

## Testing
- **U2-NFR-TEST-1**: PBT (partial): Idea DTO round-trip (PBT-02); example-based tests for state transitions and ownership.

## Tech Stack
- Inherits the platform stack (Java 21 / Spring Boot 3 / PostgreSQL / Maven / Docker; jqwik for PBT). No U2-specific additions.
