ALTER TABLE users ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT TRUE;
UPDATE users SET must_change_password = TRUE;
