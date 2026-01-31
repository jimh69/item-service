package com.example.itemservice.controller;

import com.example.itemservice.model.Item;
import com.example.itemservice.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing items.
 * 
 * <p>Provides RESTful endpoints for CRUD operations on items.
 * Handles HTTP requests, validates input, and returns appropriate
 * HTTP status codes and responses.</p>
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    private final ItemService itemService;
    
    /**
     * Constructs an ItemController with the specified service.
     *
     * @param itemService the service to use for business logic
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    
    /**
     * Retrieves all items.
     *
     * @return a list of all items
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    /**
     * Retrieves an item by its unique identifier.
     *
     * @param id the UUID of the item
     * @return the item if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable UUID id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves an item by its UPC code.
     *
     * @param upc the UPC code of the item
     * @return the item if found, 404 if not found
     */
    @GetMapping("/upc/{upc}")
    public ResponseEntity<Item> getItemByUpc(@PathVariable String upc) {
        return itemService.getItemByUpc(upc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Creates a new item.
     *
     * @param item the item to create (must be valid)
     * @return the created item with 201 Created status
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item createdItem = itemService.createItem(
                item.getDescription(),
                item.getWeight(),
                item.getVolume(),
                item.getUpc()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
    
    /**
     * Updates an existing item.
     *
     * @param id the UUID of the item to update
     * @param item the updated item data
     * @return the updated item if found, 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable UUID id, @Valid @RequestBody Item item) {
        try {
            Item updatedItem = itemService.updateItem(
                    id,
                    item.getDescription(),
                    item.getWeight(),
                    item.getVolume(),
                    item.getUpc()
            );
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Deletes an item by its unique identifier.
     *
     * @param id the UUID of the item to delete
     * @return 204 No Content if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        boolean deleted = itemService.deleteItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Searches for items by description (case-insensitive partial match).
     *
     * @param description the description to search for
     * @return a list of matching items
     */
    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String description) {
        List<Item> items = itemService.getAllItems();
        List<Item> matchingItems = items.stream()
                .filter(item -> item.getDescription().toLowerCase()
                        .contains(description.toLowerCase()))
                .toList();
        return ResponseEntity.ok(matchingItems);
    }
}