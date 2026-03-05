# Port Configuration Guide

This guide explains how to configure and override the server port for the Item Service API in different environments and scenarios.

## Default Port Configuration

The application is configured to run on port **8080** by default, as specified in `src/main/resources/application.properties`:

```properties
server.port=8080
```

## Manual Port Assignment Methods

### 1. Command Line Arguments (Recommended for Development)

Override the port when starting the application using Spring Boot's command line arguments:

```bash
# Start on port 8081
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Start on port 9090
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"

# Start with multiple arguments
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082 --spring.profiles.active=dev"
```

### 2. System Properties

Set the port using JVM system properties:

```bash
# Using -D flag
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8082"

# Using environment variable (Windows)
set SERVER_PORT=8083
mvn spring-boot:run

# Using environment variable (Linux/Mac)
export SERVER_PORT=8083
mvn spring-boot:run
```

### 3. Environment Variables

Set environment variables that Spring Boot will automatically detect:

```bash
# Linux/Mac
export SERVER_PORT=8084
mvn spring-boot:run

# Windows Command Prompt
set SERVER_PORT=8084
mvn spring-boot:run

# Windows PowerShell
$env:SERVER_PORT=8084
mvn spring-boot:run
```

### 4. Maven Profiles

Create environment-specific profiles in `pom.xml`:

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring-boot.run.arguments>--server.port=8080</spring-boot.run.arguments>
        </properties>
    </profile>
    <profile>
        <id>test</id>
        <properties>
            <spring-boot.run.arguments>--server.port=8081</spring-boot.run.arguments>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <spring-boot.run.arguments>--server.port=8082</spring-boot.run.arguments>
        </properties>
    </profile>
</profiles>
```

Then run with a specific profile:

```bash
mvn spring-boot:run -Pdev
mvn spring-boot:run -Ptest
mvn spring-boot:run -Pprod
```

### 5. Configuration Files

Create environment-specific configuration files:

- `application-dev.properties` (port 8081)
- `application-test.properties` (port 8082)
- `application-prod.properties` (port 8083)

Example `application-dev.properties`:
```properties
server.port=8081
```

Then specify the profile when running:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 6. Docker Environment Variables

When running in Docker, use environment variables:

```bash
docker run -e SERVER_PORT=8085 your-image-name
```

## Port Assignment Priority

Spring Boot follows this priority order (highest to lowest):

1. **Command line arguments** (`--server.port=8081`)
2. **System properties** (`-Dserver.port=8081`)
3. **Environment variables** (`SERVER_PORT=8081`)
4. **Application properties** (`application.properties`)
5. **Default value** (8080)

## Common Use Cases

### Development Environment
```bash
# Standard development port
mvn spring-boot:run

# Alternative port when 8080 is busy
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Testing Environment
```bash
# Run tests on different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

### Multiple Instances
```bash
# Instance 1
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"

# Instance 2 (in another terminal)
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### CI/CD Pipeline
```bash
# Use environment variable in CI
export SERVER_PORT=8080
mvn spring-boot:run
```

## Troubleshooting

### Port Already in Use
If you get "Port 8080 was already in use" error:

```bash
# Check what's using the port
# Windows:
netstat -ano | findstr :8080

# Linux/Mac:
lsof -i :8080

# Kill the process (replace PID with actual process ID)
# Windows:
taskkill /PID <PID> /F

# Linux/Mac:
kill -9 <PID>
```

### Alternative Solutions
```bash
# Use a different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Or configure to use any available port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=0"
```

### Verify Port Configuration
Check which port the application is actually using by looking at the startup logs:

```
Tomcat started on port(s): 8080 (http) with context path ''
```

## Best Practices

1. **Development**: Use command line arguments for quick port changes
2. **Testing**: Use environment variables or profiles for consistent test environments
3. **Production**: Use environment variables or configuration files
4. **Documentation**: Always document which port your service uses in each environment
5. **Monitoring**: Implement health checks to verify the service is running on the expected port

## Examples

### Quick Development Override
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Environment-Specific Configuration
```bash
# Development
export SERVER_PORT=8080
mvn spring-boot:run

# Staging
export SERVER_PORT=8081
mvn spring-boot:run

# Production
export SERVER_PORT=8082
mvn spring-boot:run
```

### Multiple Developer Setup
```bash
# Developer 1
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"

# Developer 2
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Developer 3
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"