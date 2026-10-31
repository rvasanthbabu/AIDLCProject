# Deployment Summary — U2 Ideas

- New Maven module `backend/ideas` added to parent `backend/pom.xml` and wired into `app` dependencies — compiled into the single backend deployable (no new image/service).
- Flyway `V2__ideas.sql` adds the ideas table + indexes; runs automatically on app start.
- No new infrastructure, networking, or deployment artifacts (reuses shared-infrastructure.md).
- Same canary pipeline + auto-rollback as the platform.
