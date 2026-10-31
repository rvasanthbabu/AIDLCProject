# Shared Infrastructure — Ideation Portal

This infrastructure is shared across all units because the backend is a single deployable modular monolith plus a React frontend. Per-unit infrastructure docs reference this file instead of redefining it.

## Deployment Model
- **Packaging**: Two Docker images — `backend` (Spring Boot fat jar) and `frontend` (React static build served by a hardened web server / reverse proxy). Base images pinned (no `latest`), SBOM produced (SECURITY-10/13).
- **Topology**: Single region, multi-zone (NFR-R2/R3; decisions R1=E, R2=A). No cross-region DR.
- **Orchestration**: Container platform across ≥2 availability zones. Specific platform (e.g., Kubernetes or ECS) to be confirmed by the ops team; design is platform-agnostic and expressed as "container service + load balancer + managed PostgreSQL".

## Compute
- **Backend**: stateless containers, ≥2 replicas spread across AZs; horizontal auto-scaling on CPU/request metrics with min/max limits (RESILIENCY-09).
- **Frontend**: static assets behind a CDN or the load balancer; multiple replicas or CDN-cached.

## Networking
- **Load balancer**: public, TLS termination (TLS 1.2+), access logging enabled (SECURITY-01/02). Routes `/api/*` to backend, everything else to frontend.
- **Security headers**: set by the application (and optionally reinforced at the proxy) (SECURITY-04).
- **Private subnets**: backend and database in private subnets; DB not publicly reachable; egress via NAT; deny-by-default security groups (SECURITY-07).
- **CORS**: restricted to the known frontend origin (no wildcard on authenticated endpoints) (SECURITY-08).

## Data
- **PostgreSQL**: managed, multi-zone (primary + standby in a second AZ) for HA (RESILIENCY-08). Encryption at rest + TLS in transit (SECURITY-01).
- **Backups**: automated encrypted snapshots, defined retention, periodic restore validation — Backup & Restore DR within region (NFR-R2/R6; RESILIENCY-12).
- **Session/cache store** (if distributed cache chosen): managed, in-transit encryption.

## Secrets & Config
- Secrets manager for DB credentials, MFA secrets, SMTP credentials (SECURITY-12). No secrets in images or env files committed to VCS.
- Externalized configuration per environment.

## Observability
- Centralized structured logs (correlation IDs), metrics, and tracing; dashboards and alarms (RESILIENCY-05; SECURITY-03/14).
- Log retention ≥90 days; app roles cannot delete their own logs (SECURITY-14).
- Alerts route to the organization's existing incident-response process (NFR-R11; R7=A).

## CI/CD & Deployment Strategy
- Existing organizational CI/CD pipeline (R4=A; tool to be named by ops).
- **Deployment style**: Canary (R6=D) with **auto-rollback on health/metric regression** (R5=C).
- Pipeline: build → dependency vulnerability scan → pinned image build → SBOM → deploy canary → promote or auto-rollback (SECURITY-10).
- Change management conforms to the existing org process (R3=A).

## Resiliency Testing
- A DR testing / chaos experiment plan to be proposed (R9=B); execution deferred to Operations (RESILIENCY-14).

## Local Development
- `docker-compose.yml` runs `frontend`, `backend`, and `postgres` for local end-to-end testing.
