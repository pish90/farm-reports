-- V23__casual_payments.sql

ALTER TABLE casual_attendance ADD COLUMN task_description VARCHAR(255);

CREATE TABLE casual_labourer_payments (
    id                 SERIAL PRIMARY KEY,
    casual_labourer_id INT            NOT NULL REFERENCES casual_labourers (id),
    farm_id            INT            NOT NULL REFERENCES farms (id),
    payment_date       DATE           NOT NULL,
    amount             DECIMAL(10, 2) NOT NULL,
    note               VARCHAR(500),
    paid_by            VARCHAR(255),
    created_at         TIMESTAMP      NOT NULL DEFAULT NOW()
);
