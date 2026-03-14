package com.example.itemservice.service;

import com.example.itemservice.model.Item;
import com.example.itemservice.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing items.
 * 
 * <p>This service provides business logic for item operations and acts as an
 * intermediary between the controller and repository layers. It handles
 * validation, business rules, and orchestration of operations.</p>
 * 
 * @author Item Service
 * @version 1.0
 */
@Service
@Transactional
public class ItemService {
    
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    
    private final ItemRepository itemRepository;
    
    /**
     * Constructs an ItemService with the specified repository.
     *
     * @param itemRepository the repository for item data access (will use primary bean)
     */
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    /**
     * Creates a new item with the specified properties.
     *
     * @param description the item description
     * @param weight the item weight in kilograms
     * @param volume the item volume in cubic meters
     * @param upc the item's UPC code
     * @return the created item
     * @throws IllegalArgumentException if an item with the same UPC already exists
     */
    public Item createItem(String description, Double weight, Double volume, String upc) {
        log.info("Creating new item with description: {}, weight: {}, volume: {}, UPC: {}", 
                description, weight, volume, upc);
        
        if (itemRepository.existsByUpc(upc)) {
            log.warn("Attempted to create item with duplicate UPC: {}", upc);
            throw new IllegalArgumentException("An item with UPC '" + upc + "' already exists");
        }
        
        Item newItem = Item.builder()
                .description(description)
                .weight(weight)
                .volume(volume)
                .upc(upc)
                .build();
        Item savedItem = itemRepository.save(newItem);
        log.info("Successfully created item with ID: {}", savedItem.getId());
        return savedItem;
    }
    
    /**
     * Retrieves an item by its unique identifier.
     *
     * @param id the UUID of the item
     * @return an Optional containing the item if found
     */
    @Transactional(readOnly = true)
    public Optional<Item> getItemById(UUID id) {
        log.debug("Retrieving item by ID: {}", id);
        return itemRepository.findById(id);
    }
    
    /**
     * Retrieves an item by its UPC code.
     *
     * @param upc the UPC code of the item
     * @return an Optional containing the item if found
     */
    @Transactional(readOnly = true)
    public Optional<Item> getItemByUpc(String upc) {
        log.debug("Retrieving item by UPC: {}", upc);
        return itemRepository.findByUpc(upc);
    }
    
    /**
     * Retrieves all items from the repository.
     *
     * @return a list of all items
     */
    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Item> searchItems(String description) {
        return itemRepository.findByDescriptionContainingIgnoreCase(description);
    }
    
    /**
     * Updates an existing item with the specified properties.
     *
     * @param id the UUID of the item to update
     * @param description the new item description
     * @param weight the new item weight in kilograms
     * @param volume the new item volume in cubic meters
     * @param upc the new item's UPC code
     * @param quantity the new item quantity
     * @param cost the new item cost
     * @param price the new item price
     * @return the updated item
     * @throws IllegalArgumentException if the item doesn't exist or if the UPC conflicts with another item
     */
    public Item updateItem(UUID id, String description, Double weight, Double volume, String upc,
                          Integer quantity, BigDecimal cost, BigDecimal price) {
        Optional<Item> existingItemOpt = itemRepository.findById(id);
        if (existingItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item with ID " + id + " not found");
        }
        
        Item existingItem = existingItemOpt.get();
        
        // Check if UPC is being changed and if it conflicts with another item
        if (!upc.equals(existingItem.getUpc()) && itemRepository.existsByUpc(upc)) {
            throw new IllegalArgumentException("An item with UPC '" + upc + "' already exists");
        }
        
        existingItem.setDescription(description);
        existingItem.setWeight(weight);
        existingItem.setVolume(volume);
        existingItem.setUpc(upc);
        existingItem.setQuantity(quantity);
        existingItem.setCost(cost);
        existingItem.setPrice(price);
        
        return itemRepository.save(existingItem);
    }
    
    /**
     * Deletes an item by its unique identifier.
     *
     * @param id the UUID of the item to delete
     * @return true if no exception occurred, false otherwise
     */
    public void deleteItem(UUID id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item with ID " + id + " not found");
        }
        itemRepository.deleteById(id);
    }
}