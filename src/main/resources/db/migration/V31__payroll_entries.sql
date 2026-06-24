CREATE TABLE payroll_entries (
    id               SERIAL PRIMARY KEY,
    farm_id          INTEGER        NOT NULL REFERENCES farms(id),
    year             INTEGER        NOT NULL,
    month            INTEGER        NOT NULL,
    employee_id      INTEGER        NOT NULL REFERENCES employees(id),
    salary_rate      NUMERIC(12,2),
    days_worked      INTEGER,
    gross_salary     NUMERIC(12,2),
    loans            NUMERIC(12,2)  DEFAULT 0,
    amount_paid      NUMERIC(12,2)  DEFAULT 0,
    amount_remaining NUMERIC(12,2),
    notes            VARCHAR(500),
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (farm_id, year, month, employee_id)
);
CREATE INDEX idx_payroll_farm_year_month ON payroll_entries(farm_id, year, month);

CREATE VIEW summary_payroll AS
SELECT  pe.farm_id,
        pe.year,
        pe.month,
        SUM(pe.gross_salary)     AS total_gross,
        SUM(pe.loans)            AS total_loans,
        SUM(pe.amount_paid)      AS total_paid,
        SUM(pe.amount_remaining) AS total_remaining
FROM payroll_entries pe
GROUP BY pe.farm_id, pe.year, pe.month;
