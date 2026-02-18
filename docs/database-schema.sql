-- Item Service Database Schema
-- PostgreSQL DDL for the item table

-- Create the item table
CREATE TABLE item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255) NOT NULL,
    weight DOUBLE PRECISION NOT NULL CHECK (weight > 0),
    volume DOUBLE PRECISION NOT NULL CHECK (volume > 0),
    upc VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on description for faster search operations
CREATE INDEX idx_item_description ON item(description);

-- Create index on UPC for faster lookups (though unique constraint already creates one)
-- This is redundant but explicit for documentation purposes
CREATE INDEX idx_item_upc ON item(upc);

-- Create function to automatically update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- add new columns
ALTER TABLE item
ADD COLUMN quantity INT DEFAULT 0,
ADD COLUMN cost DECIMAL(10,2) DEFAULT 0,
ADD COLUMN price DECIMAL(10,2) DEFAULT 0;   

-- Create trigger to automatically update the updated_at timestamp
CREATE TRIGGER update_item_updated_at 
    BEFORE UPDATE ON item 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE item IS 'Table storing inventory items with their metadata';
COMMENT ON COLUMN item.id IS 'Unique identifier for the item (UUID)';
COMMENT ON COLUMN item.description IS 'Human-readable description of the item';
COMMENT ON COLUMN item.weight IS 'Weight of the item in kilograms (must be positive)';
COMMENT ON COLUMN item.volume IS 'Volume of the item in cubic meters (must be positive)';
COMMENT ON COLUMN item.upc IS 'Universal Product Code (unique across all items)';
COMMENT ON COLUMN item.created_at IS 'Timestamp when the item was created';
COMMENT ON COLUMN item.updated_at IS 'Timestamp when the item was last modified';

-- Insert sample data for testing (optional)
-- INSERT INTO item (description, weight, volume, upc) VALUES
-- ('Sample Item 1', 1.5, 0.05, 'UPC001'),
-- ('Sample Item 2', 2.0, 0.1, 'UPC002'),
-- ('Sample Item 3', 0.5, 0.02, 'UPC003');