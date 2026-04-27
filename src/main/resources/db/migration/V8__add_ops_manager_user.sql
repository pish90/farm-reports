-- Add Operations Manager test user (no farm attachment)
-- Password: 'changeme'
INSERT INTO users (farm_id, name, email, password_hash, role, created_at)
VALUES (
  NULL,
  'Ops Manager',
  'ops@farmreports.local',
  '$2a$10$rDkPvvAFV8BtmrILYHohkuKhiLByB8p7LBN0cPbkYQQXNJ/nmhO9K',
  'OPERATIONS_MANAGER',
  NOW()
);
