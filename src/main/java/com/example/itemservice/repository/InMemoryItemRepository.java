package com.example.itemservice.repository;

import com.example.itemservice.model.Item;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the ItemRepository interface.
 * 
 * <p>This implementation uses a ConcurrentHashMap for thread-safe operations
 * and provides basic CRUD functionality for items. It's designed to be easily
 * replaceable with a database-backed implementation when needed.</p>
 * 
 * <p>Note: This implementation is not persistent across application restarts.</p>
 */
@Repository
public class InMemoryItemRepository implements ItemRepository {
    
    /**
     * Thread-safe map to store items with their UUID as the key.
     */
    private final Map<UUID, Item> items = new ConcurrentHashMap<>();
    
    /**
     * Thread-safe map to store items with their UPC as the key for fast lookup.
     */
    private final Map<String, Item> itemsByUpc = new ConcurrentHashMap<>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Item save(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        // For new items, generate ID and timestamps
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
            item.touch(); // Set both createdAt and updatedAt
        } else {
            // For existing items, only update the modification timestamp
            item.touch();
        }
        
        // Check for UPC conflicts (excluding the current item being updated)
        if (item.getUpc() != null && itemsByUpc.containsKey(item.getUpc()) 
                && !itemsByUpc.get(item.getUpc()).getId().equals(item.getId())) {
            throw new IllegalArgumentException("An item with UPC '" + item.getUpc() + "' already exists");
        }
        
        items.put(item.getId(), item);
        itemsByUpc.put(item.getUpc(), item);
        
        return item;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Item> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(items.get(id));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Item> findByUpc(String upc) {
        if (upc == null || upc.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(itemsByUpc.get(upc.trim()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteById(UUID id) {
        if (id == null) {
            return false;
        }
        
        Item removedItem = items.remove(id);
        if (removedItem != null) {
            itemsByUpc.remove(removedItem.getUpc());
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByUpc(String upc) {
        if (upc == null || upc.trim().isEmpty()) {
            return false;
        }
        return itemsByUpc.containsKey(upc.trim());
    }
}