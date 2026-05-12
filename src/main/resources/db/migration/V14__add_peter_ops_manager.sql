-- Test ops manager account for Peter (same rights as ops@farmreports.local)
-- Password: 'changeme' (must be changed on first login)
INSERT INTO users (farm_id, name, email, password_hash, role, must_change_password, created_at)
SELECT NULL, 'Peter', 'peter@farmreports.local',
       password_hash,
       'OPERATIONS_MANAGER',
       true,
       NOW()
FROM users WHERE email = 'matunda@farmreports.local'
ON CONFLICT (email) DO NOTHING;
