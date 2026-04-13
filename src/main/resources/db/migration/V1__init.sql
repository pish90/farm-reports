-- V1__init.sql — Core schema

CREATE TABLE farms (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id            SERIAL PRIMARY KEY,
    farm_id       INT          NOT NULL REFERENCES farms (id),
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE workers (
    id      SERIAL PRIMARY KEY,
    farm_id INT          NOT NULL REFERENCES farms (id),
    name    VARCHAR(255) NOT NULL,
    active  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE livestock_types (
    id       SERIAL PRIMARY KEY,
    farm_id  INT         NOT NULL REFERENCES farms (id),
    category VARCHAR(50) NOT NULL,
    type     VARCHAR(100) NOT NULL
);

CREATE TABLE monthly_reports (
    id           SERIAL PRIMARY KEY,
    farm_id      INT         NOT NULL REFERENCES farms (id),
    user_id      INT         NOT NULL REFERENCES users (id),
    year         INT         NOT NULL,
    month        INT         NOT NULL,
    status       VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_farm_year_month UNIQUE (farm_id, year, month)
);

CREATE TABLE attendance (
    id           SERIAL PRIMARY KEY,
    report_id    INT          NOT NULL REFERENCES monthly_reports (id),
    worker_id    INT          NOT NULL REFERENCES workers (id),
    day_of_month INT          NOT NULL,
    present      BOOLEAN      NOT NULL,
    notes        VARCHAR(500)
);

CREATE TABLE livestock_returns (
    id                SERIAL PRIMARY KEY,
    report_id         INT NOT NULL REFERENCES monthly_reports (id),
    livestock_type_id INT NOT NULL REFERENCES livestock_types (id),
    count             INT NOT NULL
);

CREATE TABLE milk_production (
    id           SERIAL PRIMARY KEY,
    report_id    INT            NOT NULL REFERENCES monthly_reports (id),
    day_of_month INT            NOT NULL,
    litres       DECIMAL(10, 2) NOT NULL
);

CREATE TABLE expenses (
    id                  SERIAL PRIMARY KEY,
    report_id           INT            NOT NULL REFERENCES monthly_reports (id),
    entry_no            INT            NOT NULL,
    date                DATE           NOT NULL,
    supplier_contractor VARCHAR(255),
    ref_no              VARCHAR(100),
    cost                DECIMAL(10, 2) NOT NULL
);
