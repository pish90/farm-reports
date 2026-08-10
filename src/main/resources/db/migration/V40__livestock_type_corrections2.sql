-- V40__livestock_type_corrections2.sql
-- Align livestock type names with the client's XLSX import template.

-- CATTLE: STEER -> COMPOUND (client term for animals not taken out to graze; replaces STEER tracking)
UPDATE livestock_types
SET type = 'COMPOUND'
WHERE category = 'CATTLE' AND type = 'STEER';

-- SHEEP: correct sex labels so RAMS = male, EWES = female everywhere.
-- Les B was mislabeled backwards by V28 (MALE->EWES, FEMALE->RAMS) — swap it back.
UPDATE livestock_types
SET type = CASE type WHEN 'EWES' THEN 'RAMS' WHEN 'RAMS' THEN 'EWES' END
WHERE category = 'SHEEP' AND type IN ('EWES', 'RAMS')
  AND farm_id IN (SELECT id FROM farms WHERE name = 'Les B');

-- Les A was never renamed from the original MALE/FEMALE seed values.
UPDATE livestock_types
SET type = 'RAMS'
WHERE category = 'SHEEP' AND type = 'MALE'
  AND farm_id IN (SELECT id FROM farms WHERE name = 'Les A');

UPDATE livestock_types
SET type = 'EWES'
WHERE category = 'SHEEP' AND type = 'FEMALE'
  AND farm_id IN (SELECT id FROM farms WHERE name = 'Les A');
