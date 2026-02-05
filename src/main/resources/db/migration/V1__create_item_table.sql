-- V1__create_item_table.sql
-- Creates the item table for the Item Service API

-- Create table that matches JPA entity expectations
CREATE TABLE item (
    id UUID PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    weight DOUBLE PRECISION NOT NULL CHECK (weight > 0),
    volume DOUBLE PRECISION NOT NULL CHECK (volume > 0),
    upc VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

-- Indexes for performance
CREATE INDEX idx_item_description ON item(description);

-- Comments for documentation
COMMENT ON TABLE item IS 'JPA-managed item table';
COMMENT ON COLUMN item.id IS 'UUID managed by JPA';
COMMENT ON COLUMN item.upc IS 'UPC code (VARCHAR(255) as expected by JPA)';
COMMENT ON COLUMN item.created_at IS 'Managed by @CreationTimestamp';
COMMENT ON COLUMN item.updated_at IS 'Managed by @UpdateTimestamp';
