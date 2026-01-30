@echo off
echo Testing Item Service API...
echo.

echo 1. Testing GET all items (should return empty array):
curl -X GET http://localhost:8080/api/items
echo.
echo.

echo 2. Creating a new item:
curl -X POST http://localhost:8080/api/items ^
  -H "Content-Type: application/json" ^
  -d "{\"description\":\"Laptop\",\"weight\":2.5,\"volume\":0.005,\"upc\":\"123456789012\"}"
echo.
echo.

echo 3. Getting all items again (should show the created item):
curl -X GET http://localhost:8080/api/items
echo.
echo.

echo 4. Getting item by ID (replace ID with actual ID from step 2):
echo "Use: curl -X GET http://localhost:8080/api/items/{id}"
echo.

echo 5. Testing validation (creating item with empty description):
curl -X POST http://localhost:8080/api/items ^
  -H "Content-Type: application/json" ^
  -d "{\"description\":\"\",\"weight\":1.0,\"volume\":0.001,\"upc\":\"987654321098\"}"
echo.
echo.

echo API testing complete!