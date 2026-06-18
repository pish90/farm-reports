-- V27__lesb_goats_and_sheep.sql
-- Add GOATS and SHEEP livestock types for Les B farm

INSERT INTO livestock_types (farm_id, category, type)
SELECT f.id, 'GOATS', t.type
FROM farms f
CROSS JOIN (VALUES ('HE'), ('SHE'), ('TOTALS')) AS t (type)
WHERE f.name = 'Les B';

INSERT INTO livestock_types (farm_id, category, type)
SELECT f.id, 'SHEEP', t.type
FROM farms f
CROSS JOIN (VALUES ('MALE'), ('FEMALE')) AS t (type)
WHERE f.name = 'Les B';
