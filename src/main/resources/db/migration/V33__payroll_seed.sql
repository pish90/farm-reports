-- V33: Payroll seed data from 2026 Excel backups (Jan-May 2026)
-- Names matched via UPPER(first_name) against employees table.
-- Rows that don't match any employee are silently skipped.


-- ============================================================
-- Farm: Kenlet
-- ============================================================

-- Month 1
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABRAHAM KIBET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'AMBROSE OCHAKO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'BERNARD BOYO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EDWIN WALELA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL LOMULEN'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 13800, 0, 10800, 3000, 0, 10800, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ERICK TERER'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KIPLIMO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'FRANCIS C. MASAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISSAC M. CHENEKET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES R. CHESEBE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOAKIM WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3500, 0, 3500, 0, 0, 3500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KENNETH MATEP'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LEVI JUMA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MOSES LOTHORO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MASINDE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3500, 0, 2500, 1000, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MUTAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE NAFULA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMWEL YEGO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 9000, 0, 9000, 0, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TITUS YEGON'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 2
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABRAHAM KIBET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'AMBROSE OCHAKO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'BERNARD BOYO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID CHERENGES'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EDWIN WALELA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL LOMULEN'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 13800, 0, 10800, 3000, 0, 10800, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ERICK TERER'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KIPLIMO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'FRANCIS C. MASAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3500, 0, 3500, 0, 0, 3500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISSAC M. CHENEKET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES R. CHESEBE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOAKIM WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KENNETH MATEP'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LEVI JUMA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MOSES LOTHORO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MASINDE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3500, 0, 2500, 1000, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MUTAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT OKECH'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE NAFULA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMWEL YEGO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 9000, 0, 9000, 0, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TITUS YEGON'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 3
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABRAHAM KIBET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'AMBROSE OCHAKO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'BERNARD BOYO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID CHERENGES'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EDWIN WALELA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL LOMULEN'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 13800, 0, 10800, 3000, 0, 10800, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ERICK TERER'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KIPLIMO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'FRANCIS C. MASAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3500, 0, 3500, 0, 0, 3500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISSAC M. CHENEKET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES R. CHESEBE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOAKIM WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KENNETH MATEP'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LEVI JUMA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MOSES LOTHORO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MASINDE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3500, 0, 2500, 1000, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MUTAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT OKECH'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE NAFULA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMWEL YEGO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 9000, 0, 9000, 0, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TITUS YEGON'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 4
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABRAHAM KIBET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'AMBROSE OCHAKO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'BERNARD BOYO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID CHERENGES'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EDWIN WALELA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL LOMULEN'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 13800, 0, 10800, 3000, 0, 10800, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ERICK TERER'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KIPLIMO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'FRANCIS C. MASAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3500, 0, 3500, 0, 0, 3500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISSAC M. CHENEKET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES R. CHESEBE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOAKIM WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KENNETH MATEP'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LEVI JUMA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MOSES LOTHORO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MASINDE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3500, 0, 2500, 1000, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MUTAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT OKECH'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE NAFULA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMWEL YEGO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 9000, 0, 9000, 0, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TITUS YEGON'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 5
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABRAHAM KIBET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'AMBROSE OCHAKO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'BERNARD BOYO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID CHERENGES'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EDWIN WALELA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL LOMULEN'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 13800, 0, 10800, 3000, 0, 10800, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ERICK TERER'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KIPLIMO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'FRANCIS C. MASAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3500, 0, 3500, 0, 0, 3500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISSAC M. CHENEKET'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES R. CHESEBE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOAKIM WANJALA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KENNETH MATEP'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LEVI JUMA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MOSES LOTHORO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MASINDE'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3500, 0, 2500, 1000, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER MUTAI'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1200, 0, 1200, 0, 0, 1200, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT OKECH'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE NAFULA'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1700, 0, 1700, 0, 0, 1700, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMWEL YEGO'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 9000, 0, 9000, 0, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TITUS YEGON'
WHERE f.name = 'Kenlet'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- ============================================================
-- Farm: Les A
-- ============================================================

-- Month 1
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTINE KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KETER'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'IAN ROP'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISAAC NGENO'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACKSON KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEN MASIKA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEVIN WESONGA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 15000, 0, 15000, 0, 0, 15000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KIPRONO TESOT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LAXARO KPR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REGINA ROBERT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REUBEN EWOI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT LEPEYOK'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 2
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTINE KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KETER'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'IAN ROP'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISAAC NGENO'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACKSON KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSPHAT KORIR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEN MASIKA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEVIN WESONGA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 15000, 0, 15000, 0, 0, 15000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KIPRONO TESOT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LAXARO KPR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REGINA ROBERT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REUBEN EWOI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT LEPEYOK'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 3
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 1000, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTINE KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KETER'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'IAN ROP'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISAAC NGENO'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACKSON KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSPHAT KORIR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEN MASIKA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEVIN WESONGA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 15000, 0, 15000, 0, 0, 15000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KIPRONO TESOT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LAXARO KPR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REGINA ROBERT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 2000, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REUBEN EWOI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT LEPEYOK'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 4
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 1600, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTINE KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KETER'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'IAN ROP'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISAAC NGENO'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACKSON KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSPHAT KORIR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEN MASIKA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEVIN WESONGA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 15000, 0, 15000, 0, 0, 15000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KIPRONO TESOT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LAXARO KPR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REGINA ROBERT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REUBEN EWOI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT LEPEYOK'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 5
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 1600, 0, 1000, 0, 0, 1000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTINE KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EZEKIEL KETER'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'IAN ROP'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ISAAC NGENO'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACKSON KOGEI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSPHAT KORIR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEN MASIKA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KEVIN WESONGA'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 15000, 0, 15000, 0, 0, 15000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KIPRONO TESOT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LAXARO KPR'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REGINA ROBERT'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'REUBEN EWOI'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROBERT LEPEYOK'
WHERE f.name = 'Les A'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- ============================================================
-- Farm: Les B
-- ============================================================

-- Month 1
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 21200, 0, 20000, 0, 0, 20000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABEL MASOLO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CAROLYN JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTOPHER BIWOT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3800, 0, 2600, 0, 0, 2600, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID EDOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 8200, 0, 6500, 500, 0, 6500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID SOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 13200, 0, 12000, 0, 0, 12000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EVANS OTERO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 14200, 0, 13000, 0, 0, 13000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFREY NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 4500, 0, 3300, 0, 0, 3300, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACOB JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 13200, 0, 9000, 3000, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JANE KIPSAINA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 4550, 0, 3350, 0, 0, 3350, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN BARASA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN SAISI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 4300, 0, 3100, 0, 0, 3100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN WANYONYI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 21200, 0, 20000, 0, 0, 20000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOYCE KIGEN'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KELVIN MASAI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LUKA KHISA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MILCA OPICHO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 18200, 0, 17000, 0, 0, 17000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'OBADIAH BETT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 5300, 0, 4100, 0, 0, 4100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER EKENO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER ELIM'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SELINA ESOKON'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 11200, 0, 10000, 0, 0, 10000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SHIKUKU PATRICK'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SITIENEI KORIR'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SUSAN MUREI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 2
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 21200, 0, 20000, 0, 0, 20000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABEL MASOLO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CAROLYN JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTOPHER BIWOT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3800, 0, 2450, 0, 0, 2450, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID EDOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 8200, 0, 7000, 0, 0, 7000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID SOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 13200, 0, 12000, 0, 0, 12000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EVANS OTERO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 14200, 0, 13000, 0, 0, 13000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFREY NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 4700, 0, 3300, 0, 0, 3300, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACOB JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 13200, 0, 9000, 3000, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JANE KIPSAINA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 4550, 0, 3350, 0, 0, 3350, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN BARASA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN SAISI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 4300, 0, 3100, 0, 0, 3100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN WANYONYI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOYCE KIGEN'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'KELVIN MASAI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LUKA KHISA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MILCA OPICHO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 18200, 0, 17000, 0, 0, 17000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'OBADIAH BETT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 5300, 0, 4100, 0, 0, 4100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER EKENO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER ELIM'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SELINA ESOKON'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 11200, 0, 10000, 0, 0, 10000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SHIKUKU PATRICK'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SITIENEI KORIR'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SUSAN MUREI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 4500, 0, 3300, 0, 0, 3300, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TOPOS JOSEPH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 3
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 21200, 0, 20000, 0, 0, 20000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABEL MASOLO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CAROLYN JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTOPHER BIWOT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3800, 0, 2450, 0, 0, 2450, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID EDOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 8200, 0, 7000, 0, 0, 7000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID SOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 13200, 0, 12000, 0, 0, 12000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EVANS OTERO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 14200, 0, 13000, 0, 0, 13000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFREY NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 4700, 0, 3300, 0, 0, 3300, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACOB JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 13200, 0, 9000, 3000, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JANE KIPSAINA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 4550, 0, 3350, 0, 0, 3350, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN BARASA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN SAISI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 4300, 0, 3100, 0, 0, 3100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN WANYONYI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOYCE KIGEN'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LUKA KHISA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MILCA OPICHO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 18200, 0, 17000, 0, 0, 17000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'OBADIAH BETT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 5300, 0, 4100, 0, 0, 4100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER EKENO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER ELIM'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SELINA ESOKON'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 11200, 0, 10000, 0, 0, 10000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SHIKUKU PATRICK'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SITIENEI KORIR'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SUSAN MUREI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 5700, 0, 4050, 0, 0, 4050, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TOPOS JOSEPH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 4
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 21200, 0, 20000, 0, 0, 20000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABEL MASOLO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CAROLYN JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTOPHER BIWOT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3800, 0, 2450, 0, 0, 2450, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID EDOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 8200, 0, 7000, 0, 0, 7000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID SOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 13200, 0, 12000, 0, 0, 12000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EVANS OTERO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 14200, 0, 13000, 0, 0, 13000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFREY NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4700, 0, 3300, 0, 0, 3300, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACOB JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 13200, 0, 9000, 3000, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JANE KIPSAINA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4550, 0, 3350, 0, 0, 3350, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN BARASA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN SAISI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4300, 0, 3100, 0, 0, 3100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN WANYONYI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOYCE KIGEN'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LUKA KHISA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MILCA OPICHO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 18200, 0, 17000, 0, 0, 17000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'OBADIAH BETT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5300, 0, 4100, 0, 0, 4100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER EKENO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER ELIM'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SELINA ESOKON'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 11200, 0, 10000, 0, 0, 10000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SHIKUKU PATRICK'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SITIENEI KORIR'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SUSAN MUREI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5700, 0, 4050, 0, 0, 4050, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TOPOS JOSEPH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 5
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 21200, 0, 20000, 0, 0, 20000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ABEL MASOLO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CAROLYN JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'CHRISTOPHER BIWOT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3800, 0, 2450, 0, 0, 2450, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID EDOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 8200, 0, 7000, 0, 0, 7000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DAVID SOME'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EMMANUEL JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 13200, 0, 12000, 0, 0, 12000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'EVANS OTERO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 14200, 0, 13000, 0, 0, 13000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFREY NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4700, 0, 3300, 0, 0, 3300, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEORGE JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JACOB JUMA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 13200, 0, 9000, 3000, 0, 9000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JAMES NGETICH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JANE KIPSAINA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4550, 0, 3350, 0, 0, 3350, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN BARASA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN SAISI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4300, 0, 3100, 0, 0, 3100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOHN WANYONYI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 0, 0, 0, 0, 0, 0, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOYCE KIGEN'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3300, 0, 2100, 0, 0, 2100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'LUKA KHISA'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'MILCA OPICHO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 18200, 0, 17000, 0, 0, 17000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'OBADIAH BETT'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5300, 0, 4100, 0, 0, 4100, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER EKENO'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'PETER ELIM'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SELINA ESOKON'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 11200, 0, 10000, 0, 0, 10000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SHIKUKU PATRICK'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SITIENEI KORIR'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3200, 0, 2000, 0, 0, 2000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SUSAN MUREI'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5700, 0, 4050, 0, 0, 4050, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TOPOS JOSEPH'
WHERE f.name = 'Les B'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- ============================================================
-- Farm: Matunda
-- ============================================================

-- Month 1
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIRWA'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFFERY LANGAT'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JONAH KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 11000, 0, 11000, 0, 0, 11000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TIMOTHY KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 2
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIRWA'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFFERY LANGAT'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JONAH KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 11000, 0, 11000, 0, 0, 11000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TIMOTHY KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 3
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIRWA'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFFERY LANGAT'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JONAH KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 11000, 0, 11000, 0, 0, 11000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TIMOTHY KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 4
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIRWA'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4000, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'GEOFFERY LANGAT'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3000, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JONAH KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 11000, 0, 11000, 0, 0, 11000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'TIMOTHY KOECH'
WHERE f.name = 'Matunda'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- ============================================================
-- Farm: Siyoi
-- ============================================================

-- Month 1
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 9200, 0, 8000, 0, 0, 8000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIPLAGAT'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL MWASAME'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 6200, 0, 5000, 0, 0, 5000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DICKSON KIBET'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'HELLEN NDIEMA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSHUA WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'NELSON LIMO'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 1, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMMY KWEYU'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 2
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 9200, 0, 8000, 0, 0, 8000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIPLAGAT'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL MWASAME'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 6200, 0, 5000, 0, 0, 5000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DICKSON KIBET'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'HELLEN NDIEMA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSHUA WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'NELSON LIMO'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 2, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMMY KWEYU'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 3
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 9200, 0, 8000, 0, 0, 8000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIPLAGAT'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL MWASAME'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 6200, 0, 5000, 0, 0, 5000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DICKSON KIBET'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'HELLEN NDIEMA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSHUA WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'NELSON LIMO'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 3, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMMY KWEYU'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 4
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 9200, 0, 8000, 0, 0, 8000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIPLAGAT'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL MWASAME'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 6200, 0, 5000, 0, 0, 5000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DICKSON KIBET'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'FRANCIS JUMA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'HELLEN NDIEMA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSHUA WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'NELSON LIMO'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 4, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMMY KWEYU'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;

-- Month 5
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 9200, 0, 8000, 0, 0, 8000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL KIPLAGAT'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DANIEL MWASAME'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 6200, 0, 5000, 0, 0, 5000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'DICKSON KIBET'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5700, 0, 4500, 0, 0, 4500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'FRANCIS JUMA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'HELLEN NDIEMA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 4200, 0, 3000, 0, 0, 3000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'JOSHUA WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 7200, 0, 6000, 0, 0, 6000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'NELSON LIMO'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 3700, 0, 2500, 0, 0, 2500, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'ROSE WAFULA'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
INSERT INTO payroll_entries (farm_id, year, month, employee_id, salary_rate, days_worked, gross_salary, loans, amount_paid, amount_remaining, updated_at)
SELECT f.id, 2026, 5, e.id, 5200, 0, 4000, 0, 0, 4000, NOW()
FROM farms f
JOIN employees e ON e.farm_id = f.id AND e.employment_type = 'SALARIED' AND UPPER(TRIM(e.first_name)) = 'SAMMY KWEYU'
WHERE f.name = 'Siyoi'
ON CONFLICT (farm_id, year, month, employee_id) DO UPDATE SET
  salary_rate = EXCLUDED.salary_rate, gross_salary = EXCLUDED.gross_salary,
  loans = EXCLUDED.loans, amount_paid = EXCLUDED.amount_paid,
  amount_remaining = EXCLUDED.amount_remaining, updated_at = EXCLUDED.updated_at;
