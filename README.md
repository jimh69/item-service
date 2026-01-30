# Item Service API

A RESTful API built with Spring Boot for managing a collection of items. This service provides basic CRUD operations for items with in-memory storage that can be easily replaced with a database.

## Features

- **RESTful API** with standard HTTP methods (GET, POST, PUT, DELETE)
- **In-memory data storage** using ConcurrentHashMap for thread safety
- **Input validation** using Jakarta Bean Validation
- **Lombok integration** to reduce boilerplate code
- **Comprehensive error handling** with appropriate HTTP status codes
- **Search functionality** by item description
- **Thread-safe operations** for concurrent access

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.2**
- **Spring Web** (REST controllers)
- **Spring Validation** (input validation)
- **Lombok** (code generation)
- **Maven** (build tool)

## Project Structure

```
src/main/java/com/example/itemservice/
├── ItemServiceApplication.java          # Main application class
├── controller/
│   └── ItemController.java              # REST endpoints
├── model/
│   └── Item.java                        # Lombok-annotated entity
├── service/
│   └── ItemService.java                 # Business logic
└── repository/
    ├── ItemRepository.java              # Repository interface
    └── InMemoryItemRepository.java      # In-memory implementation

src/main/resources/
└── application.properties               # Configuration
```

## API Endpoints

### Items

- `GET /api/items` - Get all items
- `GET /api/items/{id}` - Get item by ID
- `GET /api/items/upc/{upc}` - Get item by UPC
- `POST /api/items` - Create new item
- `PUT /api/items/{id}` - Update existing item
- `DELETE /api/items/{id}` - Delete item
- `GET /api/items/search?description={text}` - Search items by description

### Item Model

```json
{
  "id": "uuid",
  "description": "string (required)",
  "weight": "number (required, positive)",
  "volume": "number (required, positive)",
  "upc": "string (required, unique)",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```bash
   mvn clean compile
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Usage

The API will be available at `http://localhost:8080`

#### Example: Create an item

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Laptop",
    "weight": 2.5,
    "volume": 0.005,
    "upc": "123456789012"
  }'
```

#### Example: Get all items

```bash
curl http://localhost:8080/api/items
```

#### Example: Search items

```bash
curl "http://localhost:8080/api/items/search?description=laptop"
```

## Validation Rules

- **Description**: Required, cannot be empty
- **Weight**: Required, must be positive
- **Volume**: Required, must be positive
- **UPC**: Required, must be unique across all items

## Error Handling

The API returns appropriate HTTP status codes:

- `200 OK` - Successful request
- `201 Created` - Resource successfully created
- `204 No Content` - Resource successfully deleted
- `400 Bad Request` - Invalid input data
- `404 Not Found` - Resource not found

## Future Database Integration

The repository interface is designed to be easily replaceable with Spring Data JPA when you're ready to add a relational database. Simply:

1. Add Spring Data JPA dependency
2. Create a database-backed implementation of `ItemRepository`
3. Configure database connection in `application.properties`

## Testing

Use the provided `test-api.bat` script to test the API endpoints:

```bash
./test-api.bat
```

## License

This project is open source and available under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact

For questions or support, please open an issue in this repository.