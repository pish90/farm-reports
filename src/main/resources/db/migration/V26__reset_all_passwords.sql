-- V26__reset_all_passwords.sql
-- Reset all user passwords to 'changeme' (BCrypt hash, cost 10).
-- Farm manager accounts created in V2 had an invalid hash that never matched
-- 'changeme', locking all users out after any fresh database migration run.
-- Accounts with must_change_password=true that had been changed are also reset.

UPDATE users
SET password_hash      = '$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92x50IlSlYz8CSLlXe',
    must_change_password = TRUE;
