-- V4__extend_reports.sql

-- Extend expenses with new fields
ALTER TABLE expenses ADD COLUMN description       VARCHAR(500);
ALTER TABLE expenses ADD COLUMN category_id       INT REFERENCES expense_categories(id);
ALTER TABLE expenses ADD COLUMN business_unit_id  INT REFERENCES business_units(id);
ALTER TABLE expenses ADD COLUMN receipt_no        VARCHAR(100);

-- Cost apportionment for CC-900 Shared expenses
CREATE TABLE expense_apportionments (
    id               SERIAL PRIMARY KEY,
    expense_id       INT            NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    business_unit_id INT            NOT NULL REFERENCES business_units(id),
    percentage       DECIMAL(5,2)   NOT NULL,
    amount           DECIMAL(10,2)  NOT NULL
);

-- One note per worker per report (end-of-month)
CREATE TABLE attendance_worker_notes (
    id        SERIAL PRIMARY KEY,
    report_id INT  NOT NULL REFERENCES monthly_reports(id) ON DELETE CASCADE,
    worker_id INT  NOT NULL REFERENCES workers(id),
    note      TEXT NOT NULL,
    UNIQUE (report_id, worker_id)
);

-- One note per livestock category per report
CREATE TABLE livestock_category_notes (
    id        SERIAL PRIMARY KEY,
    report_id INT          NOT NULL REFERENCES monthly_reports(id) ON DELETE CASCADE,
    category  VARCHAR(50)  NOT NULL,
    note      TEXT         NOT NULL,
    UNIQUE (report_id, category)
);
