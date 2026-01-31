# Tech Context: Item Service API

## Technologies Used

### Core Framework
- **Java 21**: Modern Java version with latest features and performance improvements
- **Spring Boot 3.2.2**: Framework for building production-ready applications
- **Spring Web**: RESTful web services support
- **Spring Validation**: Input validation using Jakarta Bean Validation

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
- **Server Port**: 8080 (configurable)
- **Logging**: INFO level for application, UTC timezone for timestamps
- **Validation**: Enabled with exception throwing on validation failures

## Technical Constraints

### Performance Constraints
- **Memory Usage**: In-memory storage limited by JVM heap size
- **Concurrent Access**: Thread-safe operations using ConcurrentHashMap
- **Response Time**: Target <100ms for basic CRUD operations
- **Scalability**: Single instance, stateless design for horizontal scaling

### Security Constraints
- **Input Validation**: All inputs validated using Jakarta Bean Validation
- **Data Integrity**: UPC uniqueness enforced at repository level
- **Error Handling**: Graceful error responses without exposing internal details
- **No Authentication**: Basic implementation without security layer

### Compatibility Constraints
- **Java Version**: Minimum Java 21 required
- **Spring Boot**: Version 3.2.2 with Jakarta EE 9+ support
- **Maven**: Compatible with Maven 3.6+
- **Database**: Currently in-memory only, designed for future database integration

## Dependencies

### Runtime Dependencies
- **Spring Boot Starter Web**: Web MVC framework and embedded Tomcat
- **Spring Boot Starter Validation**: Bean validation support
- **Lombok**: Runtime dependency for generated code

### Test Dependencies
- **Spring Boot Starter Test**: Testing utilities and frameworks
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for unit tests

### Optional Dependencies (Future)
- **Spring Data JPA**: For database integration
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

## Deployment Considerations

### Local Development
- **Embedded Server**: Tomcat server included in JAR
- **Configuration**: Environment-specific properties
- **Logging**: Console output with structured format

### Production Deployment
- **JAR Packaging**: Self-contained executable JAR
- **Environment Variables**: Externalize configuration
- **Health Checks**: Ready for Spring Boot Actuator integration
- **Monitoring**: Logging and metrics ready for production monitoring

### Future Enhancements
- **Docker Support**: Containerization for consistent deployment
- **Cloud Native**: Kubernetes and cloud platform support
- **CI/CD**: GitHub Actions or similar for automated deployment
- **Monitoring**: Integration with monitoring tools (Prometheus, Grafana)