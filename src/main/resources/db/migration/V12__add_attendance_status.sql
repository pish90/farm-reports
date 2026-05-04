ALTER TABLE attendance ADD COLUMN IF NOT EXISTS status VARCHAR(3);
UPDATE attendance SET status = CASE WHEN present THEN 'P' ELSE 'A' END WHERE status IS NULL;
