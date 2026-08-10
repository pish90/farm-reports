-- V39__split_employee_names.sql
-- V29 migrated employees from the old workers/casual_labourers tables with their
-- full name stored entirely in first_name and no last_name. Split those on the
-- first space: text before the first space becomes first_name, the remainder
-- becomes last_name. Single-word names and employees that already have a
-- last_name are left untouched.

WITH split AS (
    SELECT id,
           trim(split_part(trim(first_name), ' ', 1)) AS new_first,
           trim(substring(trim(first_name) FROM position(' ' IN trim(first_name)) + 1)) AS new_last
    FROM employees
    WHERE (last_name IS NULL OR trim(last_name) = '')
      AND position(' ' IN trim(first_name)) > 0
)
UPDATE employees e
SET first_name = s.new_first,
    last_name  = s.new_last
FROM split s
WHERE e.id = s.id;
