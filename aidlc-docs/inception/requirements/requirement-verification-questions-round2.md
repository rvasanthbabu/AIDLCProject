# Requirements Verification Questions — Round 2 (Resiliency Baseline)

You enabled the **Resiliency Baseline** extension, which requires a few architectural/process decisions that must be made by you (not assumed by AI-DLC). These answers drive the DR strategy, deployment topology, and operational design across all downstream stages.

Please answer each question by filling in the letter choice after the `[Answer]:` tag. Let me know when you're done.

---

## Question R1: RTO/RPO Goals and Disaster Recovery Strategy
What are your Recovery Time Objective (RTO) and Recovery Point Objective (RPO) goals? These determine the appropriate DR strategy and infrastructure redundancy level.

A) RPO/RTO: Hours — Backup & Restore strategy. Lowest cost ($). Data backed up, no standby services. Redeploy from IaC and restore from backups on failure.

B) RPO/RTO: 10s of minutes — Pilot Light strategy. Cost: $$. Data live, services idle until failover.

C) RPO/RTO: Minutes — Warm Standby strategy. Cost: $$$. Data live, services run at reduced capacity, scaled up on failover.

D) RPO/RTO: Near real-time — Multi-site Active/Active. Highest cost ($$$$). Live services in multiple regions simultaneously.

E) N/A — Single-region deployment acceptable, no cross-region DR needed. Rely on multi-zone availability within one region.

X) Other (please describe after [Answer]: tag below)

[Answer]: E

---

## Question R2: Regional Topology
Does this workload require multi-region deployment, or is single-region with multi-zone redundancy sufficient?

A) Single-region, multi-zone — tolerates zone failure, not full-region failure. Lower cost. (Aligns with R1 options A/B/E.)

B) Multi-region active-passive — survives region failure with failover. Higher cost. (Aligns with Warm Standby / Pilot Light cross-region.)

C) Multi-region active-active — survives region failure with no downtime. Highest cost. (Aligns with Active/Active.)

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question R3: Change Management Process
How should production changes for this workload be governed? AI-DLC will conform the design to your answer rather than inventing a process.

A) Use our existing organizational change management process — provide the name/tool after the tag (e.g., ServiceNow, Jira Change, internal CAB).

B) No formal process exists yet — AI-DLC should propose a lightweight change management process (change record + approval + rollback note).

C) N/A — this workload is exempt from formal change management. Document the exemption rationale.

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question R4: CI/CD and Deployment Tooling
What CI/CD tooling and deployment process should this workload use?

A) Use our existing CI/CD pipeline — provide the tool after the tag (e.g., GitHub Actions, GitLab CI, Jenkins).

B) No pipeline exists — AI-DLC should propose a CI/CD pipeline definition appropriate to the chosen stack and Docker packaging.

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question R5: Rollback Mechanism
How should a failed production deployment be rolled back?

A) Redeploy previous artifact/image version (version-pinned rollback)

B) Blue/green swap back to the previous environment

C) Canary auto-rollback on health/metric regression

D) Database-aware rollback required (schema/data migration reversal)

E) Use our organization's existing rollback procedure — provide reference after the tag

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Question R6: Deployment Style
What deployment strategy is acceptable for this workload's risk profile?

A) Direct / in-place (lowest cost, highest blast radius)

B) Rolling (gradual container replacement)

C) Blue/green (zero-downtime cutover, higher cost)

D) Canary (progressive traffic shift with automated rollback)

X) Other (please describe after [Answer]: tag below)

[Answer]: D

---

## Question R7: Incident Response Process
How are production incidents handled for this workload?

A) Use our existing incident response process — provide the reference after the tag (e.g., PagerDuty, internal on-call/IR process).

B) No formal process exists — AI-DLC should propose a lightweight incident response and Correction of Errors (COE) process.

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question R8: Availability Target (SLA)
What target availability should the platform be designed toward? (Drives redundancy and monitoring rigor.)

A) 99.9% ("three nines", ~8.8 hours downtime/year) — typical for internal enterprise apps

B) 99.95% (~4.4 hours/year)

C) 99.99% ("four nines", ~53 minutes/year) — higher cost and complexity

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Question R9: Resiliency Testing Approach
How will resiliency mechanisms (failover, recovery) be validated?

A) Use our existing DR testing / game day / chaos engineering practice — provide reference after the tag.

B) No practice exists — AI-DLC should propose a DR testing schedule and chaos experiment plan.

C) Defer to the Operations phase — capture test scenarios now, execute during Operations.

X) Other (please describe after [Answer]: tag below)

[Answer]: B
