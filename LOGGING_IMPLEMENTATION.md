# Logging Implementation

## Overview

The Item Service API implements a comprehensive logging system that provides detailed visibility into API operations, business logic, and system behavior. The logging system includes separate log files, JSON content logging, versioned logging, and enhanced readability features.

## Architecture

The logging system consists of four specialized log files:

- **`logs/application-0.0.1-SNAPSHOT.log`** - Spring Boot startup and application events
- **`logs/requests-0.0.1-SNAPSHOT.log`** - All inbound HTTP requests with headers and JSON content
- **`logs/responses-0.0.1-SNAPSHOT.log`** - All outbound HTTP responses with status and timing
- **`logs/service-0.0.1-SNAPSHOT.log`** - Service layer operations and business logic

## Separate Logging Implementation

### Request Logging (`RequestLoggingFilter.java`)

Intercepts all incoming HTTP requests and logs:
- HTTP method, URI, and query parameters
- Client IP address (handles proxy headers)
- Important headers (Content-Type, User-Agent, Authorization)
- Request body content (when JSON content logging is enabled)

**Enhanced Readability Feature:**
```java
// 2-space indent for improved human readability
logMessage.append("\n  Headers: ").append(getHeadersInfo(requestWrapper));
logMessage.append("\n  Body: ").append(requestBody);
```

**Example Output:**
```
2026-01-30 21:58:39.393 [http-nio-8080-exec-3] INFO  REQUEST: POST /api/items from 0:0:0:0:0:0:0:1
  Headers: Content-Type: application/json, User-Agent: curl/8.13.0,
  Body: {"description":"Test Item","weight":1.5,"volume":0.001,"upc":"123456789012"}
```

### Response Logging (`ResponseLoggingFilter.java`)

Intercepts all outgoing HTTP responses and logs:
- HTTP status code and response time
- Content type and response size
- Response body content (when JSON content logging is enabled)

**Example Output:**
```
2026-01-30 21:58:39.473 [http-nio-8080-exec-3] INFO  RESPONSE: POST /api/items - Status: 201 - Time: 72ms - Content-Type: application/json - Size: 204 bytes
  Body: {"id":"caa6ef98-1411-441e-a50e-3932bbd6fb9b","description":"Test Item","weight":1.5,"volume":0.001,"upc":"123456789012","createdAt":"2026-01-30T21:58:39.4590634","updatedAt":"2026-01-30T21:58:39.4600633"}
```

### Service Logging (`ItemService.java`)

Logs business logic operations using `@Slf4j` annotation:
- Item creation, retrieval, and business operations
- Debug-level logging for detailed operations
- Warnings for business rule violations (duplicate UPC)

**Example Output:**
```
2026-01-30 21:58:39.459 [http-nio-8080-exec-3] INFO  c.e.itemservice.service.ItemService - Creating new item with description: Test Item, weight: 1.5, volume: 0.001, UPC: 123456789012
2026-01-30 21:58:39.460 [http-nio-8080-exec-3] INFO  c.e.itemservice.service.ItemService - Successfully created item with ID: caa6ef98-1411-441e-a50e-3932bbd6fb9b
```

## JSON Content Logging

### Configuration

Enable or disable JSON content logging via `application.properties`:

```properties
# JSON Content Logging Configuration
# Set to true to include request/response JSON content in logs
# Set to false to log only headers and metadata (recommended for production)
logging.json-content.enabled=true
```

### Features

- **Request Body Capture**: Uses `RequestWrapper` to capture and store request body
- **Response Body Capture**: Uses `ResponseWrapper` to capture response content
- **Multiple Read Support**: HTTP requests can only be read once by default; wrappers enable multiple reads
- **UTF-8 Encoding**: Ensures proper character encoding for JSON content

### Development vs Production

**Development Environment:**
```properties
logging.json-content.enabled=true
```
- Complete visibility into API data flow
- Enhanced debugging capabilities
- Larger log files and potential sensitive data exposure

**Production Environment:**
```properties
logging.json-content.enabled=false
```
- Smaller log files and improved performance
- Security-focused (no sensitive data in logs)
- Only headers and metadata logged

## Versioned Logging

### Jar File Versioning

Spring Boot automatically includes version in jar name:
- **File**: `item-service-0.0.1-SNAPSHOT.jar`
- **Collision Safety**: Multiple versions can coexist

### Log File Versioning

Uses Spring Boot's `<springProperty>` element for proper property resolution:

**Configuration in `logback-spring.xml`:**
```xml
<!-- Define Spring properties -->
<springProperty name="appVersion" source="spring.application.version" defaultValue="0.0.1-SNAPSHOT"/>

<!-- Define log file paths using Spring properties -->
<property name="LOG_PATH" value="logs" />
<property name="LOG_FILE_REQUESTS" value="${LOG_PATH}/requests-${appVersion}.log" />
<property name="LOG_FILE_RESPONSES" value="${LOG_PATH}/responses-${appVersion}.log" />
<property name="LOG_FILE_SERVICE" value="${LOG_PATH}/service-${appVersion}.log" />
<property name="LOG_FILE_APPLICATION" value="${LOG_PATH}/application-${appVersion}.log" />
```

**Maven Configuration in `pom.xml`:**
```xml
<properties>
    <resource.delimiter>@</resource.delimiter>
</properties>
```

**Application Properties:**
```properties
spring.application.version=@project.version@
```

### Benefits

- **Version-Specific Logs**: Each version has unique log files
- **Deployment Safety**: Multiple versions can run simultaneously
- **Clear Version Tracking**: Easy to identify which version generated logs
- **Cross-Platform**: Works correctly on Windows, Linux, and macOS

## Configuration Reference

### application.properties
```properties
# Application Configuration
spring.application.name=item-service

# Server Configuration
server.port=8080

# Logging Configuration
logging.level.com.example.itemservice=INFO
logging.level.org.springframework.web=INFO
logging.level.com.example.itemservice.logging=INFO
logging.level.com.example.itemservice.service=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Validation Configuration
spring.web.resources.add-mappings=false

# Jackson Configuration
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=UTC

# Log file paths (referenced in logback-spring.xml)
logging.path=logs

# JSON Content Logging Configuration
logging.json-content.enabled=true

# Project version for log file naming
spring.application.version=@project.version@
```

### logback-spring.xml
```xml
<configuration>
    <!-- Define Spring properties -->
    <springProperty name="appVersion" source="spring.application.version" defaultValue="0.0.1-SNAPSHOT"/>
    
    <!-- Define log file paths using Spring properties -->
    <property name="LOG_PATH" value="logs" />
    <property name="LOG_FILE_REQUESTS" value="${LOG_PATH}/requests-${appVersion}.log" />
    <property name="LOG_FILE_RESPONSES" value="${LOG_PATH}/responses-${appVersion}.log" />
    <property name="LOG_FILE_SERVICE" value="${LOG_PATH}/service-${appVersion}.log" />
    <property name="LOG_FILE_APPLICATION" value="${LOG_PATH}/application-${appVersion}.log" />
    
    <!-- Console appender for development -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Application logs appender -->
    <appender name="FILE_APPLICATION" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_APPLICATION}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_APPLICATION}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Requests log appender -->
    <appender name="FILE_REQUESTS" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_REQUESTS}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_REQUESTS}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Responses log appender -->
    <appender name="FILE_RESPONSES" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_RESPONSES}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_RESPONSES}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Service log appender -->
    <appender name="FILE_SERVICE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_SERVICE}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_SERVICE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Logger for request logging -->
    <logger name="com.example.itemservice.logging.RequestLoggingFilter" level="INFO" additivity="false">
        <appender-ref ref="FILE_REQUESTS" />
        <appender-ref ref="CONSOLE" />
    </logger>
    
    <!-- Logger for response logging -->
    <logger name="com.example.itemservice.logging.ResponseLoggingFilter" level="INFO" additivity="false">
        <appender-ref ref="FILE_RESPONSES" />
        <appender-ref ref="CONSOLE" />
    </logger>
    
    <!-- Logger for service layer -->
    <logger name="com.example.itemservice.service" level="INFO" additivity="false">
        <appender-ref ref="FILE_SERVICE" />
        <appender-ref ref="CONSOLE" />
    </logger>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE_APPLICATION" />
    </root>
</configuration>
```

## Testing and Verification

### API Request Examples

**GET Request:**
```bash
curl -X GET http://localhost:8080/api/items
```

**POST Request:**
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{"description":"Laptop","weight":2.5,"volume":0.005,"upc":"123456789012"}'
```

### Log File Verification

**Check Request Logs:**
```bash
type logs/requests-0.0.1-SNAPSHOT.log
```

**Check Response Logs:**
```bash
type logs/responses-0.0.1-SNAPSHOT.log
```

**Check Service Logs:**
```bash
type logs/service-0.0.1-SNAPSHOT.log
```

**Check Application Logs:**
```bash
type logs/application-0.0.1-SNAPSHOT.log
```

### Expected Output Formats

**Request Log:**
```
2026-01-30 21:58:39.393 [http-nio-8080-exec-3] INFO  REQUEST: POST /api/items from 0:0:0:0:0:0:0:1
  Headers: Content-Type: application/json, User-Agent: curl/8.13.0,
  Body: {"description":"Test Item","weight":1.5,"volume":0.001,"upc":"123456789012"}
```

**Response Log:**
```
2026-01-30 21:58:39.473 [http-nio-8080-exec-3] INFO  RESPONSE: POST /api/items - Status: 201 - Time: 72ms - Content-Type: application/json - Size: 204 bytes
  Body: {"id":"caa6ef98-1411-441e-a50e-3932bbd6fb9b","description":"Test Item","weight":1.5,"volume":0.001,"upc":"123456789012","createdAt":"2026-01-30T21:58:39.4590634","updatedAt":"2026-01-30T21:58:39.4600633"}
```

## Production Considerations

### Security Best Practices

- **Sensitive Data Protection**: Use `logging.json-content.enabled=false` in production
- **Log Rotation**: Automatic cleanup prevents accumulation
- **Access Control**: Log files should have appropriate permissions
- **Audit Trail**: Full audit trail of API interactions for compliance

### Performance Optimization

- **Minimal Overhead**: Request/response body capture is efficient
- **Memory Usage**: Request bodies stored temporarily in memory
- **Disk Usage**: JSON content increases log file size
- **Async Logging**: Consider asynchronous logging for high-throughput scenarios

### Log Rotation and Retention

- **Daily Rotation**: Logs rotated daily with compression
- **Size Limits**: Maximum file size of 10MB per file
- **Retention Period**: 30-day retention for historical analysis
- **Compression**: Gzip compression for older log files

### Monitoring Integration

- **Log Aggregation**: Send logs to centralized logging systems
- **Alerting**: Set up alerts based on log patterns
- **Analytics**: Analyze logs for usage patterns and performance metrics
- **Dashboards**: Create monitoring dashboards for real-time visibility

## Benefits

### Enhanced Debugging
- **Complete Data Visibility**: See exact JSON being sent/received
- **Data Validation**: Verify JSON structure and content
- **Integration Testing**: Confirm API contracts are met
- **Error Investigation**: Identify data-related issues quickly

### Improved Monitoring
- **Data Flow Tracking**: Monitor actual data being processed
- **Performance Analysis**: Correlate response times with data size
- **Business Intelligence**: Analyze usage patterns from logs

### Audit Trail
- **Complete Record**: Full audit trail of API interactions
- **Compliance**: Support for regulatory requirements
- **Troubleshooting**: Historical data for issue resolution

### Development Experience
- **Clean Organization**: Versioned log files clearly identified
- **Debugging**: Clear correlation between jar version and log files
- **Maintenance**: Easier to manage logs across different deployments
- **Readability**: Enhanced formatting with 2-space indents for better human readability

## Conclusion

The logging implementation provides comprehensive visibility into API operations while maintaining flexibility through configuration. The system is production-ready with proper security considerations, performance optimization, and enhanced readability features. All logging features work together to provide a robust, version-aware logging system that supports development, monitoring, and compliance requirements.