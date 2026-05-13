INSERT INTO users (farm_id, name, email, password_hash, role, must_change_password, created_at)
SELECT f.id,
       'Peter Viewer',
       'peter.viewer@farmreports.local',
       '$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92x50IlSlYz8CSLlXe',
       'WORKER',
       false,
       NOW()
FROM farms f
WHERE f.name = 'Matunda'
ON CONFLICT (email) DO UPDATE SET
  farm_id       = EXCLUDED.farm_id,
  password_hash = EXCLUDED.password_hash,
  role          = 'WORKER';
