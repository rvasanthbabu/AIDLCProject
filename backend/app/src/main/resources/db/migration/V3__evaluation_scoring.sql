-- U3 Evaluation & Scoring schema

CREATE TABLE assignments (
    id              UUID PRIMARY KEY,
    idea_id         UUID NOT NULL REFERENCES ideas (id),
    panel_member_id UUID NOT NULL REFERENCES users (id),
    cycle_id        UUID NOT NULL REFERENCES cycles (id),
    assigned_by     UUID NOT NULL REFERENCES users (id),
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ux_assignment_idea_member UNIQUE (idea_id, panel_member_id)
);

CREATE INDEX ix_assignments_member_cycle ON assignments (panel_member_id, cycle_id);
CREATE INDEX ix_assignments_idea ON assignments (idea_id);

CREATE TABLE evaluations (
    id               UUID PRIMARY KEY,
    idea_id          UUID NOT NULL REFERENCES ideas (id),
    panel_member_id  UUID NOT NULL REFERENCES users (id),
    cycle_id         UUID NOT NULL REFERENCES cycles (id),
    feasibility      INTEGER NOT NULL,
    impact           INTEGER NOT NULL,
    innovation       INTEGER NOT NULL,
    normalized_score NUMERIC(12,4) NOT NULL,
    comment          VARCHAR(2000),
    submitted_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    version          BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ux_evaluation_idea_member UNIQUE (idea_id, panel_member_id),
    CONSTRAINT ck_eval_metric_ranges CHECK (
        feasibility BETWEEN 1 AND 5 AND
        impact BETWEEN 1 AND 5 AND
        innovation BETWEEN 1 AND 5)
);

CREATE INDEX ix_evaluations_idea ON evaluations (idea_id);
CREATE INDEX ix_evaluations_cycle ON evaluations (cycle_id);
