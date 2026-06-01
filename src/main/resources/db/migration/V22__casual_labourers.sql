-- V22__casual_labourers.sql

CREATE TABLE casual_labourers (
    id                 SERIAL PRIMARY KEY,
    farm_id            INT            NOT NULL REFERENCES farms (id),
    name               VARCHAR(255)   NOT NULL,
    phone              VARCHAR(50),
    photo_data         BYTEA,
    photo_mime_type    VARCHAR(50),
    default_daily_rate DECIMAL(10, 2) NOT NULL,
    active             BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE casual_attendance (
    id                 SERIAL PRIMARY KEY,
    report_id          INT            NOT NULL REFERENCES monthly_reports (id),
    casual_labourer_id INT            NOT NULL REFERENCES casual_labourers (id),
    day_of_month       INT            NOT NULL,
    present            BOOLEAN        NOT NULL DEFAULT FALSE,
    status             VARCHAR(3)     NOT NULL DEFAULT 'A',
    rate_override      DECIMAL(10, 2),
    CONSTRAINT uq_casual_att UNIQUE (report_id, casual_labourer_id, day_of_month)
);
