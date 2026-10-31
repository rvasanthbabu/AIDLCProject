# Tech Stack Decisions — Unit U2: Ideas

U2 adds no new technology; it reuses the platform stack established in U1:
- Java 21, Spring Boot 3 (Spring MVC, Spring Data JPA, Bean Validation)
- PostgreSQL (shared, multi-zone)
- jqwik for property-based tests (PBT-09), JUnit 5, Testcontainers for repository tests

Rationale: U2 is CRUD + lifecycle logic over the shared database; consistency with U1 keeps the single deployable coherent.
