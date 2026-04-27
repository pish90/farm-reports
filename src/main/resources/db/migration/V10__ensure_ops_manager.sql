-- Robust ops-manager fix: handles any combination of V7/V8/V9 failure states.
-- 1. Makes farm_id nullable if it still isn't (guards against V7 never running).
-- 2. Upserts the ops manager user so any prior bad row is corrected.

DO $$
BEGIN
  IF (SELECT is_nullable
      FROM information_schema.columns
      WHERE table_name = 'users' AND column_name = 'farm_id') = 'NO' THEN
    ALTER TABLE users ALTER COLUMN farm_id DROP NOT NULL;
  END IF;
END $$;

INSERT INTO users (farm_id, name, email, password_hash, role, created_at)
VALUES (
  NULL,
  'Ops Manager',
  'ops@farmreports.local',
  '$2a$10$rDkPvvAFV8BtmrILYHohkuKhiLByB8p7LBN0cPbkYQQXNJ/nmhO9K',
  'OPERATIONS_MANAGER',
  NOW()
)
ON CONFLICT (email) DO UPDATE SET
  farm_id       = NULL,
  name          = 'Ops Manager',
  password_hash = '$2a$10$rDkPvvAFV8BtmrILYHohkuKhiLByB8p7LBN0cPbkYQQXNJ/nmhO9K',
  role          = 'OPERATIONS_MANAGER';
