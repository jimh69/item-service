# Project Brief: Item Service API

## Project Overview
A RESTful API built with Spring Boot for managing a collection of items. This service provides basic CRUD operations for items with in-memory storage that can be easily replaced with a database.

## Core Requirements
- **RESTful API** with standard HTTP methods (GET, POST, PUT, DELETE)
- **Item Management** with fields: description, weight, volume, and UPC
- **In-memory data storage** using thread-safe ConcurrentHashMap
- **Input validation** using Jakarta Bean Validation
- **Lombok integration** to reduce boilerplate code
- **Future database integration** capability

## Project Goals
1. Provide a clean, well-documented REST API for item management
2. Implement thread-safe operations for concurrent access
3. Use modern Java practices with Spring Boot 3.x
4. Design for easy database integration in the future
5. Include comprehensive input validation and error handling

## Success Criteria
- ✅ API successfully compiles and runs on Java 21
- ✅ All CRUD operations work correctly with proper HTTP status codes
- ✅ Input validation prevents invalid data entry
- ✅ Thread-safe operations handle concurrent requests
- ✅ Code follows Google Java Style Guide
- ✅ Comprehensive documentation and README provided

## Technical Constraints
- **Java Version**: 21 (minimum)
- **Spring Boot**: 3.2.2
- **Build Tool**: Maven
- **Storage**: In-memory (ConcurrentHashMap)
- **Validation**: Jakarta Bean Validation
- **Code Generation**: Lombok

## Future Considerations
- Database integration (Spring Data JPA)
- Authentication and authorization
- API versioning
- Caching layer
- Monitoring and metrics
- Containerization with Docker