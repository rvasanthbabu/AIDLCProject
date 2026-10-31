-- U1 Platform & Identity schema
-- Encryption at rest and TLS in transit are enforced at the infrastructure layer (SECURITY-01).

CREATE TABLE users (
    id                 UUID PRIMARY KEY,
    email              VARCHAR(254) NOT NULL,
    password_hash      VARCHAR(255) NOT NULL,
    display_name       VARCHAR(120) NOT NULL,
    role               VARCHAR(20)  NOT NULL,
    mfa_enabled        BOOLEAN      NOT NULL DEFAULT FALSE,
    mfa_secret         VARCHAR(255),
    status             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    failed_login_count INTEGER      NOT NULL DEFAULT 0,
    locked_until       TIMESTAMPTZ,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version            BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX ux_users_email_lower ON users (LOWER(email));

CREATE TABLE cycles (
    id          UUID PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    state       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    open_date   TIMESTAMPTZ  NOT NULL,
    close_date  TIMESTAMPTZ  NOT NULL,
    closed_at   TIMESTAMPTZ,
    created_by  UUID         NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version     BIGINT       NOT NULL DEFAULT 0
);

-- Enforce at most one OPEN cycle at a time (BR-U1-15).
CREATE UNIQUE INDEX ux_cycles_single_open ON cycles ((TRUE)) WHERE state = 'OPEN';

CREATE TABLE criteria_weights (
    id                 UUID PRIMARY KEY,
    feasibility_weight INTEGER NOT NULL,
    impact_weight      INTEGER NOT NULL,
    innovation_weight  INTEGER NOT NULL,
    updated_by         UUID,
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    version            BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_weights_sum CHECK (feasibility_weight + impact_weight + innovation_weight = 100),
    CONSTRAINT ck_weights_range CHECK (
        feasibility_weight BETWEEN 0 AND 100 AND
        impact_weight BETWEEN 0 AND 100 AND
        innovation_weight BETWEEN 0 AND 100)
);

-- Seed a default global weights row (even split-ish summing to 100).
INSERT INTO criteria_weights (id, feasibility_weight, impact_weight, innovation_weight)
VALUES ('00000000-0000-0000-0000-000000000001', 34, 33, 33);

CREATE TABLE audit_log (
    id            UUID PRIMARY KEY,
    actor_user_id UUID,
    action        VARCHAR(100) NOT NULL,
    target_ref    VARCHAR(200),
    details       VARCHAR(2000),
    timestamp     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX ix_audit_log_action ON audit_log (action);
CREATE INDEX ix_audit_log_timestamp ON audit_log (timestamp);

-- ShedLock table for the single-instance scheduled cycle close.
CREATE TABLE shedlock (
    name       VARCHAR(64)  NOT NULL PRIMARY KEY,
    lock_until TIMESTAMPTZ  NOT NULL,
    locked_at  TIMESTAMPTZ  NOT NULL,
    locked_by  VARCHAR(255) NOT NULL
);
