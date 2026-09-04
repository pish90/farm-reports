-- Replaces the single employment_type enum column with two independent booleans so an
-- employee can be both salaried and casual at once.
ALTER TABLE employees ADD COLUMN is_salaried BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE employees ADD COLUMN is_casual BOOLEAN NOT NULL DEFAULT false;

UPDATE employees SET is_salaried = true WHERE employment_type = 'SALARIED';
UPDATE employees SET is_casual = true WHERE employment_type = 'CASUAL';

ALTER TABLE employees DROP COLUMN employment_type;
ALTER TABLE employees ADD CONSTRAINT employees_employment_type_chk CHECK (is_salaried OR is_casual);
