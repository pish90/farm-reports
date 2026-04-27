-- Allow users without a farm attachment (e.g. Operations Manager role)
ALTER TABLE users ALTER COLUMN farm_id DROP NOT NULL;
