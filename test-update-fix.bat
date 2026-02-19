@echo off
echo Testing the fix for quantity, cost, and price update issue...
echo.

echo 1. Creating a test item...
curl -X POST http://localhost:8080/api/v1/items \
  -H "Content-Type: application/json" \
  -d "{\"description\":\"Test Item\",\"weight\":1.5,\"volume\":0.5,\"upc\":\"TEST12345678\",\"quantity\":10,\"cost\":25.50,\"price\":35.99}" \
  -w "Status: %{http_code}\n"

echo.
echo 2. Retrieving the created item to get its ID...
curl -X GET http://localhost:8080/api/v1/items \
  -H "Content-Type: application/json" \
  -w "Status: %{http_code}\n"

echo.
echo 3. Updating the item with new quantity, cost, and price values...
curl -X PUT http://localhost:8080/api/v1/items/REPLACE_WITH_ITEM_ID \
  -H "Content-Type: application/json" \
  -d "{\"description\":\"Updated Test Item\",\"weight\":2.0,\"volume\":0.8,\"upc\":\"TEST12345678\",\"quantity\":25,\"cost\":30.75,\"price\":45.50}" \
  -w "Status: %{http_code}\n"

echo.
echo 4. Retrieving the updated item to verify the changes...
curl -X GET http://localhost:8080/api/v1/items/REPLACE_WITH_ITEM_ID \
  -H "Content-Type: application/json" \
  -w "Status: %{http_code}\n"

echo.
echo Test completed. Check the responses to verify that quantity, cost, and price are now being updated correctly.
echo Note: Replace REPLACE_WITH_ITEM_ID with the actual item ID from step 2.