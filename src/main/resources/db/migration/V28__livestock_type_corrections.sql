-- V28__livestock_type_corrections.sql
-- Align livestock type names with spreadsheet source of truth

-- Rename GOATS: HE→HES, SHE→SHES for Kenlet and Les B
UPDATE livestock_types
SET type = 'HES'
WHERE category = 'GOATS' AND type = 'HE'
  AND farm_id IN (SELECT id FROM farms WHERE name IN ('Kenlet', 'Les B'));

UPDATE livestock_types
SET type = 'SHES'
WHERE category = 'GOATS' AND type = 'SHE'
  AND farm_id IN (SELECT id FROM farms WHERE name IN ('Kenlet', 'Les B'));

-- Rename Les B SHEEP: MALE→EWES, FEMALE→RAMS
UPDATE livestock_types
SET type = 'EWES'
WHERE category = 'SHEEP' AND type = 'MALE'
  AND farm_id IN (SELECT id FROM farms WHERE name = 'Les B');

UPDATE livestock_types
SET type = 'RAMS'
WHERE category = 'SHEEP' AND type = 'FEMALE'
  AND farm_id IN (SELECT id FROM farms WHERE name = 'Les B');

-- Add SEGERO cattle type for Les B
INSERT INTO livestock_types (farm_id, category, type)
SELECT f.id, 'CATTLE', 'SEGERO'
FROM farms f
WHERE f.name = 'Les B'
  AND NOT EXISTS (
    SELECT 1 FROM livestock_types lt
    WHERE lt.farm_id = f.id AND lt.category = 'CATTLE' AND lt.type = 'SEGERO'
  );
