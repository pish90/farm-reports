-- V5__add_test_viewer_user.sql — Non-admin user for testing role-based UI
-- Password: 'changeme' (same BCrypt hash as admin seed users)
INSERT INTO users (farm_id, name, email, password_hash, role, created_at)
SELECT f.id,
       'Matunda Viewer',
       'viewer@farmreports.local',
       '$2a$10$rDkPvvAFV8BtmrILYHohkuKhiLByB8p7LBN0cPbkYQQXNJ/nmhO9K',
       'USER',
       NOW()
FROM farms f
WHERE f.name = 'Matunda'
ON CONFLICT (email) DO NOTHING;
