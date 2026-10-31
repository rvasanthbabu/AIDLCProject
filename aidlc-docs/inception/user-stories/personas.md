# Personas — Ideation Portal

Standard-depth personas (role, goals, needs, pain points, scenario) for the four system roles.

---

## Persona 1: Priya — The Submitter (Employee)

- **Role**: Employee / Idea Submitter
- **Goals**: Share improvement ideas, get them fairly evaluated, and gain recognition for good ones.
- **Needs**:
  - A simple, structured form to capture an idea without friction.
  - Ability to save a draft and finish later.
  - Visibility into the status of submitted ideas.
  - Confidence that evaluation is transparent and fair.
- **Pain Points**:
  - Ideas lost in email threads or informal channels.
  - No feedback on whether an idea was reviewed.
  - Clunky tools that discourage participation.
- **Tech Comfort**: Comfortable with everyday web apps.
- **Scenario**: During the Q3 innovation cycle, Priya drafts an idea to automate a manual report, saves it mid-week, completes it the next day, submits it, and later checks the dashboard to see it progress to "Scored".

---

## Persona 2: Raj — The Panel Member (Evaluator)

- **Role**: Evaluation Panel Member
- **Goals**: Fairly and efficiently score the ideas assigned to him across the defined dimensions.
- **Needs**:
  - A clear list of ideas assigned to him for the current cycle.
  - A focused scoring interface for feasibility, impact, and innovation (1-5) with space for comments.
  - Assurance that his scores are independent (not influenced by other evaluators).
  - Ability to revise scores until the cycle closes.
- **Pain Points**:
  - Unclear which ideas are his responsibility.
  - Bias from seeing others' scores before deciding.
  - Losing work if he can't edit before the deadline.
- **Tech Comfort**: High — uses business tools daily.
- **Scenario**: Raj logs in, sees 8 ideas assigned to him, scores each on the three metrics with brief justifications, and adjusts two scores before the cycle closes. He never sees other panelists' scores while evaluating.

---

## Persona 3: Dana — The Administrator

- **Role**: Platform Administrator / Innovation Program Manager
- **Goals**: Run innovation cycles smoothly, ensure fair evaluation coverage, and recognize top ideas.
- **Needs**:
  - Manage users and assign roles.
  - Configure evaluation criteria and their weights.
  - Create, open, and close cycles.
  - Assign submitted ideas to specific panel members.
  - Trigger recognition for the top ideas at cycle close.
- **Pain Points**:
  - Manual tracking of who should evaluate what.
  - Inconsistent scoring rules across cycles.
  - Difficulty determining fair winners, especially on ties.
- **Tech Comfort**: High — power user and configurator.
- **Scenario**: Dana opens the Q3 cycle, sets innovation weight higher than feasibility, assigns incoming ideas to panelists, monitors progress on the dashboard, and after close triggers recognition for the top 3 (ties resolved by earliest submission).

---

## Persona 4: Victor — The Viewer (Leadership)

- **Role**: Leadership / Read-only Stakeholder
- **Goals**: Understand which ideas and themes are performing well to inform decisions.
- **Needs**:
  - Read-only access to dashboards, leaderboards, and analytics.
  - Multi-dimensional comparative views across criteria.
  - Visibility of recognized ideas.
- **Pain Points**:
  - No consolidated view of innovation activity.
  - Hard to compare ideas objectively.
- **Tech Comfort**: Moderate — consumes dashboards, does not configure.
- **Scenario**: Victor reviews the leaderboard near cycle end, compares top ideas on innovation vs feasibility, and notes the recognized winners for a leadership meeting.

---

## Persona-to-Role Mapping

| Persona | System Role | Primary Epics |
|---|---|---|
| Priya | Submitter | Idea Submission, Dashboards (view own), Recognition (recipient) |
| Raj | Panel Member | Evaluation, Access/Assignment (assigned view) |
| Dana | Administrator | Admin (users/criteria/cycles/assignment), Recognition (trigger), Evaluation config |
| Victor | Viewer | Dashboards, Analytics, Recognition (view) |
