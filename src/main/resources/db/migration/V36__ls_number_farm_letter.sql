-- V36__ls_number_farm_letter.sql
-- Append a single-letter farm tag to existing LS numbers, e.g. LS2001K for Kenlet.
-- New employees get the letter at creation time (see EmployeeIdService.generateLsNumber).
-- The WHERE clause only matches numbers that don't already carry a letter, so this is idempotent.

UPDATE employees e
SET ls_number = e.ls_number || CASE f.name
        WHEN 'Les A'   THEN 'A'
        WHEN 'Les B'   THEN 'B'
        WHEN 'Kenlet'  THEN 'K'
        WHEN 'Siyoi'   THEN 'S'
        WHEN 'Matunda' THEN 'M'
        ELSE ''
    END
FROM farms f
WHERE f.id = e.farm_id
  AND e.ls_number ~ '^LS[0-9]+$';
