# System Patterns: Item Service API

## Architecture Overview

The Item Service API follows a layered architecture pattern with clear separation of concerns and production-ready configuration management:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│                    (REST Controllers)                   │
├─────────────────────────────────────────────────────────┤
│                     Service Layer                       │
│                    (Business Logic)                     │
├─────────────────────────────────────────────────────────┤
│                    Data Access Layer                    │
│                   (Repository Pattern)                  │
├─────────────────────────────────────────────────────────┤
│                      Data Layer                         │
│                   (In-Memory Storage)                   │
├─────────────────────────────────────────────────────────┤
│                   Configuration Layer                   │
│              (Spring Cloud Config Client)               │
└─────────────────────────────────────────────────────────┘
```

## Key Design Patterns

### 1. Repository Pattern
- **Interface**: `ItemRepository` defines the contract for data operations
- **Implementation**: `InMemoryItemRepository` provides thread-safe in-memory storage
- **Benefits**: Easy to replace with database implementation, testable, decoupled from business logic

### 2. Service Layer Pattern
- **Class**: `ItemService` encapsulates business logic and orchestration
- **Responsibilities**: Validation, business rules, transaction management
- **Benefits**: Centralized business logic, reusable across multiple controllers

### 3. DTO Pattern (Data Transfer Object)
- **Class**: `Item` serves as both entity and DTO
- **Features**: Lombok annotations reduce boilerplate code
- **Validation**: Jakarta Bean Validation ensures data integrity

### 4. REST Controller Pattern
- **Class**: `ItemController` handles HTTP requests and responses
- **Features**: Proper HTTP status codes, error handling, input validation
- **Benefits**: Clean separation between web layer and business logic

### 5. Configuration Management Pattern
- **ConfigPoller**: Automatic configuration polling every 30 seconds
- **ConfigServerConnectionMonitor**: Connection status monitoring and logging
- **Benefits**: Externalized configuration with automatic refresh capabilities

## Component Relationships

### Core Dependencies
```
ItemController → ItemService → ItemRepository → InMemoryItemRepository
ItemController → ConfigPoller → ContextRefresher
ConfigPoller → ConfigServerConnectionMonitor
```

### Spring Framework Integration
- **@RestController**: Exposes REST endpoints
- **@Service**: Business logic layer
- **@Repository**: Data access layer
- **@Configuration**: Configuration components
- **@Autowired**: Dependency injection
- **@Valid**: Input validation
- **@Scheduled**: Automatic configuration polling
- **@EventListener**: Application lifecycle monitoring

## Thread Safety Patterns

### ConcurrentHashMap Usage
- **Primary Storage**: `Map<UUID, Item>` for ID-based lookups
- **Secondary Index**: `Map<String, Item>` for UPC-based lookups
- **Benefits**: Thread-safe operations without explicit synchronization

### Immutable Operations
- **ID Generation**: UUID.randomUUID() for new items
- **Timestamps**: LocalDateTime.now() for created/updated times
- **Validation**: Immutable validation rules prevent data corruption

## Error Handling Strategy

### HTTP Status Codes
- **200 OK**: Successful GET, PUT operations
- **201 Created**: Successful POST operations
- **204 No Content**: Successful DELETE operations
- **400 Bad Request**: Validation errors
- **404 Not Found**: Resource not found

### Exception Handling
- **IllegalArgumentException**: Business rule violations
- **ValidationException**: Input validation failures
- **Graceful Degradation**: Return appropriate error responses

## Configuration Management Patterns

### Spring Cloud Config Integration
- **Automatic Polling**: Every 30 seconds via @Scheduled
- **Manual Refresh**: POST /api/items/config/refresh endpoint
- **Status Monitoring**: GET /api/items/config/status endpoint
- **Connection Monitoring**: Detailed logging of config server connection attempts

### Configuration Properties
- **config.polling.enabled**: Enable/disable automatic polling
- **config.polling.interval**: Polling interval in milliseconds
- **spring.cloud.config.uri**: Config server URL
- **spring.cloud.config.fail-fast**: Fail startup if config unavailable

## Future Database Integration Pattern

### Repository Interface Contract
The `ItemRepository` interface is designed for easy database integration:

```java
// Current in-memory implementation
@Repository
public class InMemoryItemRepository implements ItemRepository

// Future database implementation
@Repository
public class DatabaseItemRepository implements ItemRepository
```

### Migration Strategy
1. Add Spring Data JPA dependency
2. Create JPA entity mapping
3. Implement database-backed repository
4. Update application configuration
5. Maintain API compatibility

## Performance Considerations

### Memory Management
- **ConcurrentHashMap**: Efficient concurrent access
- **Object Pooling**: Reuse objects where possible
- **Lazy Loading**: Load data only when needed

### Search Optimization
- **Secondary Index**: UPC lookups in O(1) time
- **Stream API**: Efficient filtering for search operations
- **Pagination**: Future consideration for large datasets

## Security Patterns

### Input Validation
- **Jakarta Bean Validation**: Comprehensive field validation
- **Null Safety**: Prevent null pointer exceptions
- **Type Safety**: Strong typing prevents data corruption

### Data Integrity
- **UPC Uniqueness**: Prevent duplicate UPC codes
- **Business Rules**: Enforce positive weight/volume values
- **Audit Trail**: Created/updated timestamps for tracking

## Testing Strategy

### Unit Testing
- **Service Layer**: Business logic validation
- **Repository Layer**: Data access operations
- **Controller Layer**: HTTP endpoint testing

### Integration Testing
- **End-to-End**: Full API workflow testing
- **Error Scenarios**: Validation and error handling
- **Performance**: Response time and throughput

## Monitoring and Observability

### Logging Strategy
- **Structured Logging**: Consistent log format with Logback
- **Level Configuration**: INFO for normal operations, DEBUG for troubleshooting
- **Correlation IDs**: Track requests across components
- **Configuration Logging**: Detailed config server connection monitoring

### Metrics Collection
- **Response Times**: Monitor API performance
- **Error Rates**: Track system reliability
- **Resource Usage**: Monitor memory and CPU utilization
- **Configuration Changes**: Track configuration refresh events

### Production Readiness
- **Health Checks**: Ready for Spring Boot Actuator integration
- **Configuration Status**: Manual and automatic status endpoints
- **Error Handling**: Comprehensive error responses with appropriate HTTP codes
- **Documentation**: Swagger/OpenAPI integration for API documentation
