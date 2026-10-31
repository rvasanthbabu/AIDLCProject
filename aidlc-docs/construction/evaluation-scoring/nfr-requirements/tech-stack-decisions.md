# Tech Stack Decisions — Unit U3: Evaluation & Scoring

Reuses the platform stack (Java 21 / Spring Boot 3 / PostgreSQL / Maven / Docker; jqwik). U3-specific choices:

- **BigDecimal** for normalizedScore and aggregateScore — deterministic arithmetic, avoids float drift, and makes PBT equality assertions reliable.
- **SQL-side aggregation** (AVG/SUM/COUNT with GROUP BY) via Spring Data JPA `@Query` for dashboard aggregates — keeps on-read aggregation performant.
- No new external dependencies.
