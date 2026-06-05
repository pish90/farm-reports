-- V25__kenlet_goats.sql
-- Add GOATS livestock types for Kenlet farm

INSERT INTO livestock_types (farm_id, category, type)
SELECT f.id, 'GOATS', t.type
FROM farms f
CROSS JOIN (VALUES
    ('HE'),
    ('SHE'),
    ('TOTALS')
) AS t (type)
WHERE f.name = 'Kenlet';
