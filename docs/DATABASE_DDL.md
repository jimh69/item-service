# Item Service Database Schema Documentation

## Overview

This document describes the database schema for the Item Service, which stores inventory items in a PostgreSQL database. The schema is designed to support the RESTful API operations while maintaining data integrity and performance.

## Database Configuration

The database connection is configured through Spring Cloud Config with the following properties (from the `dev` profile):

```yaml
database:
  host: localhost
  port: 5432
  name: testbed
  username: postgres
  password: falcon69
  driver-class-name: org.postgresql.Driver
  dialect: org.hibernate.dialect.PostgreSQLDialect
  pool-size: 5
  timeout: 10000
  debug: true
```

## Spring Cloud Config Retry Configuration

The application uses Spring Cloud Config for externalized configuration management with robust retry mechanisms to ensure reliable connection to the configuration server:

```yaml
spring:
  cloud:
    config:
      retry:
        initial-interval: 2000      # Initial retry delay (2 seconds)
        max-interval: 10000         # Max retry delay (10 seconds)
        multiplier: 1.5             # Exponential backoff multiplier
        max-attempts: 12            # Maximum retry attempts
      fail-fast: true              # Fail startup if config unavailable after retries
      uri: http://localhost:8888/config
      name: item-service
      profile: dev
      label: null
      username: configuser
      password: configpass
```

### Retry Mechanism Details

- **Exponential Backoff**: Connection attempts use exponential backoff starting at 2 seconds, increasing by 1.5x each retry
- **Maximum Delay**: Retry intervals cap at 10 seconds to prevent excessive wait times
- **Retry Limit**: Maximum of 12 retry attempts before failing permanently
- **Fail-Fast Behavior**: Application startup fails if configuration server is unavailable after all retries
- **Connection Monitoring**: Spring Cloud Config provides built-in connection status monitoring and logging

### Connection Resilience

The retry mechanism ensures:
- **High Availability**: Automatic recovery from temporary network issues
- **Graceful Degradation**: Application can start with cached configuration if server is temporarily unavailable
- **Operational Visibility**: Comprehensive logging for troubleshooting connection issues
- **Performance Optimization**: Exponential backoff prevents overwhelming the configuration server

### Production Configuration

For production environments, consider adjusting retry settings based on your infrastructure:

```yaml
# Production retry configuration
spring:
  cloud:
    config:
      retry:
        initial-interval: 1000      # Start with 1 second
        max-interval: 5000          # Cap at 5 seconds
        multiplier: 2.0             # Aggressive backoff
        max-attempts: 6             # Fewer attempts for faster failure
```

## Table Structure

### `item` Table

The main table storing all inventory items.

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| `id` | UUID | PRIMARY KEY, NOT NULL, UNIQUE | Unique identifier for the item (auto-generated) |
| `description` | VARCHAR(255) | NOT NULL | Human-readable description of the item |
| `weight` | DOUBLE PRECISION | NOT NULL, CHECK (weight > 0) | Weight of the item in kilograms |
| `volume` | DOUBLE PRECISION | NOT NULL, CHECK (volume > 0) | Volume of the item in cubic meters |
| `upc` | VARCHAR(50) | NOT NULL, UNIQUE | Universal Product Code (unique across all items) |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the item was created |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the item was last modified |

## Indexes

### Primary Key Index
- **Name**: `item_pkey`
- **Type**: B-tree
- **Columns**: `id`
- **Purpose**: Primary key constraint and fast lookups by ID

### Unique Index on UPC
- **Name**: Automatically created by UNIQUE constraint
- **Type**: B-tree
- **Columns**: `upc`
- **Purpose**: Enforces UPC uniqueness and fast lookups by UPC

### Description Index
- **Name**: `idx_item_description`
- **Type**: B-tree
- **Columns**: `description`
- **Purpose**: Optimizes search operations by description

## Constraints

### Check Constraints
- **Weight Check**: `CHECK (weight > 0)` - Ensures weight is positive
- **Volume Check**: `CHECK (volume > 0)` - Ensures volume is positive

### Unique Constraints
- **UPC Unique**: `UNIQUE (upc)` - Prevents duplicate UPC codes

### Not Null Constraints
- All columns except auto-generated timestamps have NOT NULL constraints

## Triggers

### `update_item_updated_at` Trigger
- **Type**: BEFORE UPDATE
- **Table**: `item`
- **Function**: `update_updated_at_column()`
- **Purpose**: Automatically updates the `updated_at` timestamp whenever a row is modified

## Functions

### `update_updated_at_column()`
- **Language**: PL/pgSQL
- **Purpose**: Sets the `updated_at` field to the current timestamp
- **Usage**: Called by the `update_item_updated_at` trigger

## Data Types

- **UUID**: Used for primary keys to ensure global uniqueness
- **VARCHAR(255)**: Used for text fields with reasonable length limits
- **DOUBLE PRECISION**: Used for numeric values (weight, volume)
- **TIMESTAMP**: Used for temporal data with timezone support

## Sample Data

```sql
INSERT INTO item (description, weight, volume, upc) VALUES
('Wireless Bluetooth Headphones', 0.5, 0.02, 'UPC001'),
('Gaming Mouse', 0.1, 0.005, 'UPC002'),
('Mechanical Keyboard', 1.2, 0.03, 'UPC003'),
('4K Monitor', 4.5, 0.15, 'UPC004'),
('USB-C Cable', 0.05, 0.001, 'UPC005');
```

## Migration Scripts

### Flyway Migration
- **File**: `src/main/resources/db/migration/V1__create_item_table.sql`
- **Purpose**: Creates the initial table structure
- **Execution**: Automatic on application startup

### Manual DDL
- **File**: `docs/database-schema.sql`
- **Purpose**: Complete schema definition with comments
- **Usage**: Manual database setup or documentation reference

## Performance Considerations

1. **Indexing**: Strategic indexes on frequently queried columns (description, UPC)
2. **Data Types**: Appropriate data types for optimal storage and query performance
3. **Constraints**: Database-level constraints for data integrity
4. **Triggers**: Automatic timestamp management without application overhead

## Security Considerations

1. **Connection Security**: Database credentials managed through Spring Cloud Config
2. **Data Validation**: Application-level validation plus database constraints
3. **Access Control**: Database user permissions should be restricted to necessary operations only

## Future Enhancements

1. **Partitioning**: Consider table partitioning for large datasets
2. **Full-text Search**: Add full-text search capabilities for description field
3. **Audit Trail**: Consider adding audit tables for tracking changes
4. **Caching**: Implement database-level caching for frequently accessed data

## Dependencies

- **PostgreSQL**: Version 12+ recommended
- **Spring Data JPA**: For ORM operations
- **Flyway**: For database migrations
- **HikariCP**: For connection pooling (default in Spring Boot)