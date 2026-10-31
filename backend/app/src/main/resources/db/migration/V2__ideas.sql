-- U2 Ideas schema

CREATE TABLE ideas (
    id                UUID PRIMARY KEY,
    submitter_id      UUID NOT NULL REFERENCES users (id),
    cycle_id          UUID REFERENCES cycles (id),
    title             VARCHAR(150),
    problem_statement VARCHAR(4000),
    proposed_solution VARCHAR(4000),
    expected_impact   VARCHAR(4000),
    category          VARCHAR(30),
    state             VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    submitted_at      TIMESTAMPTZ,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version           BIGINT       NOT NULL DEFAULT 0
);

-- Submitter "my ideas" queries.
CREATE INDEX ix_ideas_submitter ON ideas (submitter_id);

-- Dashboard/leaderboard aggregation reuse (U4): ideas in a cycle by state.
CREATE INDEX ix_ideas_cycle_state ON ideas (cycle_id, state);
