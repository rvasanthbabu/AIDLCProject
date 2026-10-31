# Requirements Verification Questions — Ideation Portal

Please answer each question by filling in the letter choice after the `[Answer]:` tag. If none of the options match your needs, choose the last option (Other) and describe your preference after the tag. Let me know when you're done.

---

## Question 1
Who are the user roles in the system? (select the closest match)

A) Two roles: Submitter (employee) and Panel Member (evaluator)

B) Three roles: Submitter, Panel Member, and Administrator (manages users, criteria, cycles)

C) Four roles: Submitter, Panel Member, Administrator, and Viewer (read-only dashboards)

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Question 2
How should the evaluation scoring work across the three metrics (feasibility, impact, innovation)?

A) Each metric scored 1-5, final score = simple average of all panel members' scores

B) Each metric scored 1-10, final score = weighted average (weights configurable per metric)

C) Each metric scored 1-5, final score = sum, with configurable weights per metric

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Question 3
How is a submitted idea assigned to panel members for evaluation?

A) All panel members can evaluate every submitted idea

B) An administrator assigns specific ideas to specific panel members

C) Panel members self-select ideas from a pool

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Question 4
What is the lifecycle/workflow of an idea?

A) Draft → Submitted → Under Evaluation → Scored → Closed

B) Draft → Submitted → Under Evaluation → Scored → Recognized/Rejected

C) Simple: Draft → Submitted → Scored (no explicit intermediate states)

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Question 5
Should evaluations run in time-boxed "cycles/campaigns" (e.g., quarterly innovation rounds), or be continuous?

A) Time-boxed cycles with defined open/close dates; recognition awarded per cycle

B) Continuous submission and evaluation; leaderboard is always live

C) Both — continuous submission with periodic recognition snapshots

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question 6
What authentication approach should be used for secure, role-based access?

A) Email/password with application-managed accounts and role assignment

B) Corporate SSO / SAML / OIDC (integrate with existing identity provider)

C) Email/password now, designed to add SSO later

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Question 7
What does "real-time" mean for the dashboards and leaderboards?

A) Live push updates (scores reflected within seconds via websockets/streaming)

B) Near real-time (refresh on page load / short polling, e.g., every 30-60s)

C) Periodic refresh is acceptable (manual refresh or scheduled updates)

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Question 8
What technology stack preference do you have? (this drives code generation)

A) Web app: React (frontend) + Python/FastAPI (backend) + PostgreSQL

B) Web app: React (frontend) + Node.js/Express (backend) + PostgreSQL

C) Web app: React (frontend) + Java/Spring Boot (backend) + PostgreSQL

D) No preference — recommend a modern, well-supported stack

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Question 9
Where will this be deployed?

A) Cloud (AWS)

B) Cloud (other — Azure/GCP)

C) On-premises / self-hosted

D) Not decided yet / local development only for now

X) Other (please describe after [Answer]: tag below)

[Answer]: Containerized using docker

---

## Question 10
What is the expected scale?

A) Small — a single team/department (tens to low hundreds of users)

B) Medium — a division (hundreds to low thousands of users)

C) Large — enterprise-wide (thousands+ users, high concurrency)

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Question 11
For the recognition system (top 3 ideas), how should ties be handled and what does "reward" entail?

A) Ties broken by highest innovation score; recognition = digital badge + leaderboard highlight only

B) Ties broken by earliest submission; recognition = badge + notification to submitter and management

C) Ties shown as shared rank; recognition = badge + configurable reward notes entered by admin

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Question 12: Security Extensions
Should security extension rules be enforced for this project?

A) Yes — enforce all SECURITY rules as blocking constraints (recommended for production-grade applications)

B) No — skip all SECURITY rules (suitable for PoCs, prototypes, and experimental projects)

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question 13: Resiliency Extensions
Should the resiliency baseline be applied to this project?

**What this extension is.** Enabling it applies a set of directional, design-time best practices for building resilient systems, derived from the AWS Well-Architected Framework (Reliability Pillar). It steers requirements, design, and code toward fault tolerance, high availability, observability, and recoverability.

**What this extension is NOT.** It does not make your workload production-ready, nor certify any availability/RTO/RPO target. It is a starting point, not a substitute for a formal AWS Well-Architected Review.

A) Yes — apply the resiliency baseline as directional best practices and design-time guidance (recommended for business-critical workloads)

B) No — skip the resiliency baseline (suitable for PoCs, prototypes, and experimental projects)

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question 14: Property-Based Testing Extension
Should property-based testing (PBT) rules be enforced for this project?

A) Yes — enforce all PBT rules as blocking constraints (recommended for projects with business logic, data transformations, serialization, or stateful components)

B) Partial — enforce PBT rules only for pure functions and serialization round-trips (suitable for projects with limited algorithmic complexity)

C) No — skip all PBT rules (suitable for simple CRUD applications, UI-only projects, or thin integration layers with no significant business logic)

X) Other (please describe after [Answer]: tag below)

[Answer]: B
