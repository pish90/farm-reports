-- V34__employee_profile.sql
-- Add LS numbers, extended profile fields, and salaried employee payments.

-- ── 1. Global LS number sequence ────────────────────────────────────────────
INSERT INTO employee_id_sequences (prefix, last_number) VALUES ('LS', 2000);

-- ── 2. New columns on employees ─────────────────────────────────────────────
ALTER TABLE employees ADD COLUMN ls_number  VARCHAR(10);
ALTER TABLE employees ADD COLUMN date_of_birth DATE;
ALTER TABLE employees ADD COLUMN national_id   VARCHAR(50);

-- ── 3. Backfill LS numbers for all existing employees (ordered by id) ────────
DO $$
DECLARE
    v_rec RECORD;
    v_seq INTEGER;
BEGIN
    FOR v_rec IN SELECT id FROM employees ORDER BY id LOOP
        UPDATE employee_id_sequences
           SET last_number = last_number + 1
         WHERE prefix = 'LS'
        RETURNING last_number INTO v_seq;

        UPDATE employees
           SET ls_number = 'LS' || v_seq::TEXT
         WHERE id = v_rec.id;
    END LOOP;
END;
$$;

-- Now safe to enforce NOT NULL + UNIQUE
ALTER TABLE employees ALTER COLUMN ls_number SET NOT NULL;
ALTER TABLE employees ADD CONSTRAINT uq_employees_ls_number UNIQUE (ls_number);

-- ── 4. Salaried employee payments ────────────────────────────────────────────
CREATE TABLE employee_payments (
    id           SERIAL         PRIMARY KEY,
    employee_id  INT            NOT NULL REFERENCES employees(id),
    farm_id      INT            NOT NULL REFERENCES farms(id),
    payment_date DATE           NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL,
    note         VARCHAR(500),
    paid_by      VARCHAR(255),
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);
