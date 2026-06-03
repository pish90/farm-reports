-- V24__casual_work_sessions.sql
-- Replace per-casual daily rate with per-session (activity-level) rate.
-- Keep default_daily_rate column as nullable so historical rows are preserved.

ALTER TABLE casual_labourers ALTER COLUMN default_daily_rate DROP NOT NULL;

CREATE TABLE casual_work_sessions (
    id                 SERIAL PRIMARY KEY,
    farm_id            INT            NOT NULL REFERENCES farms (id),
    session_date       DATE           NOT NULL,
    activity           VARCHAR(255)   NOT NULL,
    default_daily_rate DECIMAL(10, 2) NOT NULL
);

CREATE TABLE casual_work_entries (
    id                 SERIAL PRIMARY KEY,
    session_id         INT            NOT NULL REFERENCES casual_work_sessions (id) ON DELETE CASCADE,
    casual_labourer_id INT            NOT NULL REFERENCES casual_labourers (id),
    rate_override      DECIMAL(10, 2),
    CONSTRAINT uq_work_entry UNIQUE (session_id, casual_labourer_id)
);
