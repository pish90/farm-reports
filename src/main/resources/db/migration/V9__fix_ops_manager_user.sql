-- Ensure the ops manager test user exists with the correct password hash and role.
-- Uses upsert to handle any existing row from a prior manual insert or failed V8.
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
