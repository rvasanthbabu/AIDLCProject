# Repository Layer Summary — U1 Platform & Identity

## Generated
- `identity/repository/UserRepository.java` — `findByEmailIgnoreCase`, `existsByEmailIgnoreCase` (parameterized; SECURITY-05)
- `identity/repository/CycleRepository.java` — `findFirstByState`, `existsByState`, `findByStateAndCloseDateLessThanEqual`
- `identity/repository/CriteriaWeightsRepository.java` — singleton weights row
- `core/audit/AuditLogRepository.java` — audit entries (common-core)

## Design notes
- All queries are Spring Data derived/parameterized — no string concatenation (SECURITY-05).
- HikariCP pool bounded via `application.yml` (connection timeout 5s; no unbounded waits — RESILIENCY-10).
- Repository integration tests with Testcontainers PostgreSQL are planned to run in the Build and Test stage (Testcontainers requires Docker, unavailable in the authoring environment).
