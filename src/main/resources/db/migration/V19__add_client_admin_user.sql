INSERT INTO users (farm_id, name, email, password_hash, role, must_change_password, created_at)
VALUES (
  NULL,
  'Admin',
  'admin@farmreports.local',
  '$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92x50IlSlYz8CSLlXe',
  'ADMIN',
  true,
  NOW()
)
ON CONFLICT (email) DO UPDATE SET
  password_hash        = '$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92x50IlSlYz8CSLlXe',
  role                 = 'ADMIN',
  farm_id              = NULL,
  must_change_password = true;
