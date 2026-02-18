-- V2__add_quantity_cost_price_columns.sql
-- Adds quantity, cost, and price columns to the item table

ALTER TABLE item
    ADD COLUMN quantity INT          DEFAULT 0,
    ADD COLUMN cost     DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN price    DECIMAL(10,2) DEFAULT 0;

COMMENT ON COLUMN item.quantity IS 'Stock quantity (non-negative integer)';
COMMENT ON COLUMN item.cost     IS 'Unit cost in currency (up to 10 digits, 2 decimal places)';
COMMENT ON COLUMN item.price    IS 'Unit price in currency (up to 10 digits, 2 decimal places)';
