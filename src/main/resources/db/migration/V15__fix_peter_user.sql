-- Upsert Peter's ops manager account with a confirmed-working BCrypt hash for 'changeme'
INSERT INTO users (farm_id, name, email, password_hash, role, must_change_password, created_at)
VALUES (
  NULL,
  'Peter',
  'peter@farmreports.local',
  '$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92x50IlSlYz8CSLlXe',
  'OPERATIONS_MANAGER',
  true,
  NOW()
)
ON CONFLICT (email) DO UPDATE SET
  password_hash       = '$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92x50IlSlYz8CSLlXe',
  role                = 'OPERATIONS_MANAGER',
  must_change_password = true,
  farm_id             = NULL;
