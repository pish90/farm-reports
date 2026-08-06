-- V37's password hash for "changeme" did not actually verify (the hash copied from
-- CLAUDE.md's "confirmed valid" note was itself wrong). Replaced with a hash generated
-- and round-trip verified via BCryptPasswordEncoder at migration-authoring time.
UPDATE users
SET password_hash = '$2a$10$Jo5jGv4K781lpnmvNTMuOelw2K22RenTJD3MFl22XBdAqoFoY7Srm',
    must_change_password = true
WHERE email = 'skhayundi@gmail.com';
