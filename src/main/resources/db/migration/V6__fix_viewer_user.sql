-- V6__fix_viewer_user.sql — Fix viewer user: invalid role enum + invalid password hash
-- Valid BCrypt hash for 'changeme'
UPDATE users
SET role          = 'WORKER',
    password_hash = '$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92x50IlSlYz8CSLlXe'
WHERE email = 'viewer@farmreports.local';
