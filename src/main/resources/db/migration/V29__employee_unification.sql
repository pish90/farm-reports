-- V29__employee_unification.sql
-- Merge workers and casual_labourers into a single employees table.
-- All dependent FK columns keep their names; only their target table changes.

-- ── 1. Employee ID sequences (one row per farm-name prefix) ──────────────────
CREATE TABLE employee_id_sequences (
    prefix      VARCHAR(10) PRIMARY KEY,
    last_number INTEGER     NOT NULL DEFAULT 0
);

INSERT INTO employee_id_sequences (prefix, last_number) VALUES
    ('LES', 0), ('KEN', 0), ('MAT', 0), ('SIY', 0), ('EMP', 0);

-- ── 2. Departments (per-farm) ────────────────────────────────────────────────
CREATE TABLE departments (
    id      SERIAL       PRIMARY KEY,
    farm_id INTEGER      NOT NULL REFERENCES farms(id),
    name    VARCHAR(100) NOT NULL,
    CONSTRAINT uq_department_farm_name UNIQUE (farm_id, name)
);

-- ── 3. Unified employees table ───────────────────────────────────────────────
CREATE TABLE employees (
    id                 SERIAL        PRIMARY KEY,
    farm_id            INTEGER       NOT NULL REFERENCES farms(id),
    employee_id        VARCHAR(20)   NOT NULL UNIQUE,
    first_name         VARCHAR(100)  NOT NULL,
    last_name          VARCHAR(100),
    phone              VARCHAR(50),
    employment_type    VARCHAR(20)   NOT NULL CHECK (employment_type IN ('SALARIED', 'CASUAL')),
    job_title          VARCHAR(100),
    department_id      INTEGER       REFERENCES departments(id),
    start_date         DATE,
    status             VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    photo_data         BYTEA,
    photo_mime_type    VARCHAR(50),
    default_daily_rate DECIMAL(10,2),
    created_at         TIMESTAMP     NOT NULL DEFAULT NOW(),
    -- Temporary migration columns (removed at end of this script)
    old_worker_id          INTEGER,
    old_casual_labourer_id INTEGER
);

-- ── 4. Migrate existing data ─────────────────────────────────────────────────
DO $$
DECLARE
    v_rec    RECORD;
    v_prefix VARCHAR(10);
    v_seq    INTEGER;
    v_eid    VARCHAR(20);
BEGIN
    -- Salaried workers
    FOR v_rec IN
        SELECT w.id, w.farm_id, w.name, w.active, f.name AS farm_name
        FROM workers w
        JOIN farms f ON f.id = w.farm_id
        ORDER BY f.name, w.id
    LOOP
        v_prefix := CASE v_rec.farm_name
            WHEN 'Les A'    THEN 'LES'
            WHEN 'Les B'    THEN 'LES'
            WHEN 'Kenlet'   THEN 'KEN'
            WHEN 'Matunda'  THEN 'MAT'
            WHEN 'Siyoi'    THEN 'SIY'
            ELSE                 'EMP'
        END;

        UPDATE employee_id_sequences
           SET last_number = last_number + 1
         WHERE prefix = v_prefix
        RETURNING last_number INTO v_seq;

        v_eid := v_prefix || LPAD(v_seq::TEXT, 4, '0');

        INSERT INTO employees (farm_id, employee_id, first_name, employment_type,
                               status, old_worker_id, created_at)
        VALUES (v_rec.farm_id, v_eid, v_rec.name, 'SALARIED',
                CASE WHEN v_rec.active THEN 'ACTIVE' ELSE 'INACTIVE' END,
                v_rec.id, NOW());
    END LOOP;

    -- Casual labourers
    FOR v_rec IN
        SELECT cl.id, cl.farm_id, cl.name, cl.phone,
               cl.photo_data, cl.photo_mime_type, cl.default_daily_rate, cl.active,
               f.name AS farm_name
        FROM casual_labourers cl
        JOIN farms f ON f.id = cl.farm_id
        ORDER BY f.name, cl.id
    LOOP
        v_prefix := CASE v_rec.farm_name
            WHEN 'Les A'    THEN 'LES'
            WHEN 'Les B'    THEN 'LES'
            WHEN 'Kenlet'   THEN 'KEN'
            WHEN 'Matunda'  THEN 'MAT'
            WHEN 'Siyoi'    THEN 'SIY'
            ELSE                 'EMP'
        END;

        UPDATE employee_id_sequences
           SET last_number = last_number + 1
         WHERE prefix = v_prefix
        RETURNING last_number INTO v_seq;

        v_eid := v_prefix || LPAD(v_seq::TEXT, 4, '0');

        INSERT INTO employees (farm_id, employee_id, first_name, phone,
                               employment_type, status,
                               photo_data, photo_mime_type, default_daily_rate,
                               old_casual_labourer_id, created_at)
        VALUES (v_rec.farm_id, v_eid, v_rec.name, v_rec.phone,
                'CASUAL',
                CASE WHEN v_rec.active THEN 'ACTIVE' ELSE 'INACTIVE' END,
                v_rec.photo_data, v_rec.photo_mime_type, v_rec.default_daily_rate,
                v_rec.id, NOW());
    END LOOP;
END;
$$;

-- ── 5. Re-point FK column values to the new employee IDs ────────────────────
UPDATE attendance a
   SET worker_id = e.id
  FROM employees e
 WHERE e.old_worker_id = a.worker_id;

UPDATE attendance_worker_notes awn
   SET worker_id = e.id
  FROM employees e
 WHERE e.old_worker_id = awn.worker_id;

UPDATE casual_attendance ca
   SET casual_labourer_id = e.id
  FROM employees e
 WHERE e.old_casual_labourer_id = ca.casual_labourer_id;

UPDATE casual_work_entries cwe
   SET casual_labourer_id = e.id
  FROM employees e
 WHERE e.old_casual_labourer_id = cwe.casual_labourer_id;

UPDATE casual_labourer_payments p
   SET casual_labourer_id = e.id
  FROM employees e
 WHERE e.old_casual_labourer_id = p.casual_labourer_id;

-- ── 6. Swap FK targets to employees ─────────────────────────────────────────
ALTER TABLE attendance
    DROP CONSTRAINT attendance_worker_id_fkey,
    ADD  CONSTRAINT attendance_employee_id_fkey
         FOREIGN KEY (worker_id) REFERENCES employees(id);

ALTER TABLE attendance_worker_notes
    DROP CONSTRAINT attendance_worker_notes_worker_id_fkey,
    ADD  CONSTRAINT attendance_worker_notes_employee_id_fkey
         FOREIGN KEY (worker_id) REFERENCES employees(id);

ALTER TABLE casual_attendance
    DROP CONSTRAINT casual_attendance_casual_labourer_id_fkey,
    ADD  CONSTRAINT casual_attendance_employee_id_fkey
         FOREIGN KEY (casual_labourer_id) REFERENCES employees(id);

ALTER TABLE casual_work_entries
    DROP CONSTRAINT casual_work_entries_casual_labourer_id_fkey,
    ADD  CONSTRAINT casual_work_entries_employee_id_fkey
         FOREIGN KEY (casual_labourer_id) REFERENCES employees(id);

ALTER TABLE casual_labourer_payments
    DROP CONSTRAINT casual_labourer_payments_casual_labourer_id_fkey,
    ADD  CONSTRAINT casual_labourer_payments_employee_id_fkey
         FOREIGN KEY (casual_labourer_id) REFERENCES employees(id);

-- ── 7. Drop temp migration columns ──────────────────────────────────────────
ALTER TABLE employees
    DROP COLUMN old_worker_id,
    DROP COLUMN old_casual_labourer_id;

-- ── 8. Drop old tables ───────────────────────────────────────────────────────
DROP TABLE workers;
DROP TABLE casual_labourers;
