package com.example.itemservice.service;

import com.example.itemservice.model.Item;
import com.example.itemservice.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing item business logic.
 * 
 * <p>This service provides the business logic for item operations,
 * including validation, error handling, and orchestration of
 * repository operations. It acts as an intermediary between
 * the controller and repository layers.</p>
 */
@Service
public class ItemService {
    
    private final ItemRepository itemRepository;
    
    /**
     * Constructs an ItemService with the specified repository.
     *
     * @param itemRepository the repository to use for data operations
     */
    @Autowired
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
        if (itemRepository.existsByUpc(upc)) {
            throw new IllegalArgumentException("An item with UPC '" + upc + "' already exists");
        }
        
        Item newItem = Item.create(description, weight, volume, upc);
        return itemRepository.save(newItem);
    }
    
    /**
     * Retrieves an item by its unique identifier.
     *
     * @param id the UUID of the item
     * @return an Optional containing the item if found
     */
    public Optional<Item> getItemById(UUID id) {
        return itemRepository.findById(id);
    }
    
    /**
     * Retrieves an item by its UPC code.
     *
     * @param upc the UPC code of the item
     * @return an Optional containing the item if found
     */
    public Optional<Item> getItemByUpc(String upc) {
        return itemRepository.findByUpc(upc);
    }
    
    /**
     * Retrieves all items from the repository.
     *
     * @return a list of all items
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    /**
     * Updates an existing item with the specified properties.
     *
     * @param id the UUID of the item to update
     * @param description the new item description
     * @param weight the new item weight in kilograms
     * @param volume the new item volume in cubic meters
     * @param upc the new item's UPC code
     * @return the updated item
     * @throws IllegalArgumentException if the item doesn't exist or if the UPC conflicts with another item
     */
    public Item updateItem(UUID id, String description, Double weight, Double volume, String upc) {
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
        
        return itemRepository.save(existingItem);
    }
    
    /**
     * Deletes an item by its unique identifier.
     *
     * @param id the UUID of the item to delete
     * @return true if the item was found and deleted, false otherwise
     */
    public boolean deleteItem(UUID id) {
        return itemRepository.deleteById(id);
    }
}