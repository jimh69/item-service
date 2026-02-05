package com.example.itemservice.repository;

import com.example.itemservice.model.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Item entities.
 * 
 * <p>This interface defines the contract for item persistence operations.
 * It can be easily implemented with different storage mechanisms
 * (in-memory, database, etc.) while maintaining the same API.</p>
 */
@Repository
public interface ItemRepository {
    
    /**
     * Saves an item to the repository.
     * If the item already exists (by ID), it will be updated.
     * If the item is new, it will be created.
     *
     * @param item the item to save
     * @return the saved item with updated timestamps
     */
    Item save(Item item);
    
    /**
     * Finds an item by its unique identifier.
     *
     * @param id the UUID of the item
     * @return an Optional containing the item if found, empty otherwise
     */
    Optional<Item> findById(UUID id);
    
    /**
     * Finds an item by its UPC code.
     *
     * @param upc the UPC code of the item
     * @return an Optional containing the item if found, empty otherwise
     */
    Optional<Item> findByUpc(String upc);
    
    /**
     * Retrieves all items from the repository.
     *
     * @return a list of all items, empty list if none exist
     */
    List<Item> findAll();
    
    /**
     * Deletes an item by its unique identifier.
     *
     * @param id the UUID of the item to delete
     */
    void deleteById(UUID id);
    
    /**
     * Checks if an item with the given UPC already exists.
     *
     * @param upc the UPC code to check
     * @return true if an item with this UPC exists, false otherwise
     */
    boolean existsByUpc(String upc);
}