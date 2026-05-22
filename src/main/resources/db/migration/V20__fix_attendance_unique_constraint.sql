-- Remove duplicate attendance rows, keeping the latest entry per (report_id, worker_id, day_of_month)
DELETE FROM attendance
WHERE id NOT IN (
    SELECT MAX(id)
    FROM attendance
    GROUP BY report_id, worker_id, day_of_month
);

-- Prevent duplicates going forward
ALTER TABLE attendance
ADD CONSTRAINT uq_attendance_report_worker_day
UNIQUE (report_id, worker_id, day_of_month);
