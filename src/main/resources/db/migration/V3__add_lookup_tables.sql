-- V3__add_lookup_tables.sql

CREATE TABLE expense_categories (
    id           SERIAL PRIMARY KEY,
    account_code VARCHAR(20)  NOT NULL,
    account_name VARCHAR(100) NOT NULL
);

INSERT INTO expense_categories (account_code, account_name) VALUES
('1000', 'Milk Sales'),
('1001', 'Pig Sales'),
('1002', 'Maize Sales'),
('1003', 'Coffee Sales'),
('1004', 'Sugarcane Sales'),
('1008', 'Garden Sales'),
('1009', 'Management Income'),
('2000', 'Purchased Feed - Dairy'),
('2001', 'Purchased Feed - Pigs'),
('2002', 'Fertilizers'),
('2005', 'Crop Maintenance'),
('2006', 'Seeds'),
('3000', 'Wages - Direct'),
('3001', 'Wages - Admin'),
('3002', 'Office Expenses'),
('3003', 'Casuals'),
('4000', 'Fuel'),
('4001', 'Repairs & Maintenance'),
('4002', 'Electricity'),
('4003', 'Vehicles & Tractor Expenses'),
('4004', 'Other Supplies'),
('4005', 'Transport'),
('4006', 'Kitchen'),
('6000', 'Veterinary & Medicine'),
('6001', 'Crop Protection'),
('9300', 'Fixed Assets');

CREATE TABLE business_units (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(20)  NOT NULL,
    name VARCHAR(100) NOT NULL
);

INSERT INTO business_units (code, name) VALUES
('CC-100', 'Dairy'),
('CC-200', 'Maize'),
('CC-300', 'Pigs'),
('CC-400', 'Coffee'),
('CC-500', 'Gardens'),
('CC-900', 'Shared');
