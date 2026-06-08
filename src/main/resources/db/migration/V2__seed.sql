-- V2__seed.sql — Reference data and initial admin users

-- ── Farms ────────────────────────────────────────────────────────────────────
INSERT INTO farms (name, created_at) VALUES
    ('Matunda', NOW()),
    ('Les A',   NOW()),
    ('Les B',   NOW()),
    ('Kenlet',  NOW()),
    ('Siyoi',   NOW());

-- ── Admin users (one per farm) ───────────────────────────────────────────────
-- Password: 'changeme'
-- Hash generated with BCryptPasswordEncoder(10).encode("changeme")
-- To regenerate: new BCryptPasswordEncoder(10).encode("changeme")
INSERT INTO users (farm_id, name, email, password_hash, role, created_at)
SELECT f.id,
       f.name || ' Admin',
       lower(replace(f.name, ' ', '')) || '@farmreports.local',
       '$2a$10$rDkPvvAFV8BtmrILYHohkuKhiLByB8p7LBN0cPbkYQQXNJ/nmhO9K',
       'ADMIN',
       NOW()
FROM farms f;

-- ── Livestock types ───────────────────────────────────────────────────────────

-- CATTLE: every farm
INSERT INTO livestock_types (farm_id, category, type)
SELECT f.id, 'CATTLE', t.type
FROM farms f
CROSS JOIN (VALUES
    ('MILKING'),
    ('DRY'),
    ('MATERNITY'),
    ('WEANERS_HEIFERS'),
    ('WEANERS_BULLS'),
    ('STEER'),
    ('CALVES_HEIFERS'),
    ('CALVES_BULLS')
) AS t (type);

-- PIGS: Matunda only
INSERT INTO livestock_types (farm_id, category, type)
SELECT f.id, 'PIGS', t.type
FROM farms f
CROSS JOIN (VALUES
    ('BOARS'),
    ('SOWS'),
    ('PIGLETS'),
    ('WEANERS'),
    ('PORKERS'),
    ('BACONERS')
) AS t (type)
WHERE f.name = 'Matunda';

-- SHEEP: Les A only
INSERT INTO livestock_types (farm_id, category, type)
SELECT f.id, 'SHEEP', t.type
FROM farms f
CROSS JOIN (VALUES
    ('MALE'),
    ('FEMALE')
) AS t (type)
WHERE f.name = 'Les A';
