-- JPA-Compatible Item Table Schema
-- This matches exactly what JPA/Hibernate expects

-- Drop existing table and objects to ensure clean JPA schema
DROP TABLE IF EXISTS item CASCADE;
DROP TRIGGER IF EXISTS update_item_updated_at ON item;
DROP FUNCTION IF EXISTS update_updated_at_column();

-- Create table that matches JPA expectations exactly
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
