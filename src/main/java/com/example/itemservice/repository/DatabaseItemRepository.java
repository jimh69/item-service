package com.example.itemservice.repository;

import com.example.itemservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA-based repository implementation for managing Item entities in PostgreSQL.
 * 
 * <p>This repository extends JpaRepository to provide standard CRUD operations
 * and custom query methods for item management. It serves as the database-backed
 * implementation of the ItemRepository interface.</p>
 * 
 * <p>Uses Spring Data JPA with custom JPQL queries for efficient database operations.</p>
 */
@Repository
public interface DatabaseItemRepository extends JpaRepository<Item, UUID>, ItemRepository {
    
    /**
     * Finds an item by its UPC code using a custom JPQL query.
     * 
     * @param upc the UPC code of the item
     * @return an Optional containing the item if found, empty otherwise
     */
    @Override
    @Query("SELECT i FROM Item i WHERE i.upc = :upc")
    Optional<Item> findByUpc(@Param("upc") String upc);
    
    /**
     * Finds items by partial description match (case-insensitive).
     * 
     * @param description the partial description to search for
     * @return a list of items matching the description, empty list if none found
     */
    @Query("SELECT i FROM Item i WHERE LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<Item> findByDescriptionContainingIgnoreCase(@Param("description") String description);
    
    /**
     * Checks if an item with the given UPC already exists.
     * 
     * @param upc the UPC code to check
     * @return true if an item with this UPC exists, false otherwise
     */
    @Override
    @Query("SELECT COUNT(i) > 0 FROM Item i WHERE i.upc = :upc")
    boolean existsByUpc(@Param("upc") String upc);
    
    /**
     * {@inheritDoc}
     */
    @Override
    List<Item> findAll();
    
    /**
     * {@inheritDoc}
     */
    @Override
    Optional<Item> findById(UUID id);
    
    /**
     * {@inheritDoc}
     */
    @Override
    Item save(Item item);
    
    /**
     * {@inheritDoc}
     */
    @Override
    void deleteById(UUID id);
}