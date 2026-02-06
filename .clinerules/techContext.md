# Tech Context: Item Service API

## Technologies Used

### Core Framework
- **Java 21**: Modern Java version with latest features and performance improvements
- **Spring Boot 4.0.2**: Framework for building production-ready applications
- **Spring Web**: RESTful web services support
- **Spring Validation**: Input validation using Jakarta Bean Validation
- **Spring Cloud 2025.1.1**: Configuration management and cloud-native features

### Build & Dependencies
- **Maven 3.9.9**: Build automation and dependency management
- **Lombok 1.18.32**: Code generation to reduce boilerplate (getters, setters, constructors)
- **Spring Boot Starter Parent**: Parent POM for dependency management

### Development Tools
- **IntelliJ IDEA / VS Code**: IDE support with Spring Boot integration
- **Maven Wrapper**: Consistent build environment
- **Git**: Version control with GitHub integration

## Development Setup

### Prerequisites
- Java Development Kit (JDK) 21 or higher
- Apache Maven 3.6 or higher
- Git for version control

### Local Development
1. **Clone Repository**:
   ```bash
   git clone https://github.com/jimh69/item-service.git
   cd item-service
   ```

2. **Build Project**:
   ```bash
   mvn clean compile
   ```

3. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access API**: http://localhost:8080

### Configuration
- **Application Properties**: `src/main/resources/application.properties`
- **Bootstrap Configuration**: `src/main/resources/bootstrap.yml`
- **Server Port**: 8080 (configurable)
- **Logging**: INFO level for application, UTC timezone for timestamps
- **Validation**: Enabled with exception throwing on validation failures
- **Spring Cloud Config**: Externalized configuration with automatic polling

## Technical Constraints

### Performance Constraints
- **Memory Usage**: Database storage with efficient connection pooling
- **Concurrent Access**: Thread-safe operations using JPA transactions
- **Response Time**: Target <100ms for basic CRUD operations
- **Scalability**: Single instance, stateless design for horizontal scaling

### Security Constraints
- **Input Validation**: All inputs validated using Jakarta Bean Validation
- **Data Integrity**: UPC uniqueness enforced at repository level
- **Error Handling**: Graceful error responses without exposing internal details
- **No Authentication**: Basic implementation without security layer

### Compatibility Constraints
- **Java Version**: Minimum Java 21 required
- **Spring Boot**: Version 4.0.2 with Jakarta EE 9+ support
- **Spring Cloud**: Version 2025.1.1 for configuration management
- **Maven**: Compatible with Maven 3.6+
- **Database**: PostgreSQL with Spring Data JPA integration

## Dependencies

### Runtime Dependencies
- **Spring Boot Starter Web**: Web MVC framework and embedded Tomcat
- **Spring Boot Starter Validation**: Bean validation support
- **Spring Cloud Starter Config**: Configuration management client
- **Spring Boot Starter Data JPA**: JPA repository support with PostgreSQL
- **Lombok**: Runtime dependency for generated code
- **Springdoc OpenAPI**: Swagger/OpenAPI documentation

### Test Dependencies
- **Spring Boot Starter Test**: Testing utilities and frameworks
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for unit tests

### Optional Dependencies (Future)
- **Spring Security**: For authentication and authorization
- **Spring Boot Actuator**: For monitoring and metrics
- **Docker**: For containerization

## Tool Usage Patterns

### Maven Commands
- **Build**: `mvn clean compile`
- **Test**: `mvn test`
- **Package**: `mvn package`
- **Run**: `mvn spring-boot:run`
- **Install**: `mvn install`

### Development Workflow
1. **Code Changes**: Edit source files in `src/main/java/`
2. **Compile**: `mvn compile` to check for compilation errors
3. **Test**: `mvn test` to run unit tests
4. **Run**: `mvn spring-boot:run` for local development
5. **Package**: `mvn package` for deployment artifacts

### IDE Integration
- **Spring Boot Dashboard**: View and manage Spring Boot applications
- **Lombok Plugin**: Required for proper IDE support
- **Maven Integration**: Automatic dependency resolution and build management
- **Hot Reload**: Spring Boot DevTools for faster development cycles

## Configuration Management

### Spring Cloud Config Integration
- **Config Server URL**: http://localhost:8888/config
- **Environment**: dev profile
- **Branch**: main
- **Authentication**: Basic auth with configuser/configpass
- **Retry Configuration**: 3 attempts with exponential backoff
- **Fail Fast**: Disabled for graceful degradation

### Configuration Polling
- **Automatic Polling**: Enabled by default, every 30 seconds
- **Manual Refresh**: POST /api/items/config/refresh endpoint
- **Status Monitoring**: GET /api/items/config/status endpoint
- **Connection Monitoring**: Detailed logging of config server connection attempts

## Deployment Considerations

### Local Development
- **Embedded Server**: Tomcat server included in JAR
- **Configuration**: Environment-specific properties with Spring Cloud Config
- **Logging**: Console output with structured format using Logback
- **Monitoring**: Configuration status endpoints for operational visibility

### Production Deployment
- **JAR Packaging**: Self-contained executable JAR
- **Environment Variables**: Externalize configuration via Spring Cloud Config
- **Health Checks**: Ready for Spring Boot Actuator integration
- **Monitoring**: Comprehensive logging and configuration status endpoints
- **Configuration Management**: Externalized configuration with automatic refresh

### Production Readiness Features
- **Structured Logging**: Logback configuration with separate appenders for requests, responses, and service logs
- **Configuration Management**: Spring Cloud Config with automatic polling and manual refresh
- **Error Handling**: Comprehensive error responses with appropriate HTTP status codes
- **Documentation**: Swagger/OpenAPI integration for API documentation and testing
- **Health Monitoring**: Ready for Spring Boot Actuator integration for health checks and metrics

### Future Enhancements
- **Docker Support**: Containerization for consistent deployment
- **Cloud Native**: Kubernetes and cloud platform support
- **CI/CD**: GitHub Actions or similar for automated deployment
- **Monitoring**: Integration with monitoring tools (Prometheus, Grafana)
- **Authentication**: Spring Security integration for API protection
