-- Copy the confirmed-working password hash from an existing admin user.
-- Both use 'changeme'; this guarantees the stored value is byte-for-byte correct.
UPDATE users
SET password_hash = (
    SELECT password_hash FROM users WHERE email = 'matunda@farmreports.local'
)
WHERE email = 'ops@farmreports.local';
