-- Reassign per-farm admin accounts to MANAGER role.
-- ADMIN (farm_id IS NULL) = sees everything.
-- MANAGER (farm_id IS NOT NULL) = sees only their own farm.
UPDATE users
SET role = 'MANAGER'
WHERE role = 'ADMIN'
  AND farm_id IS NOT NULL;
