package com.example.itemservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an item in the inventory system.
 * 
 * <p>Each item has a unique identifier, description, weight, volume, and UPC code.
 * This class uses Lombok annotations to reduce boilerplate code for getters,
 * setters, constructors, and builder pattern.</p>
 * 
 * @author Item Service
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    /**
     * Unique identifier for the item.
     * Auto-generated using UUID.
     */
    private UUID id;
    
    /**
     * Human-readable description of the item.
     * Cannot be null or empty.
     */
    @NotBlank(message = "Description is required")
    private String description;
    
    /**
     * Weight of the item in kilograms.
     * Must be positive.
     */
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    /**
     * Volume of the item in cubic meters.
     * Must be positive.
     */
    @NotNull(message = "Volume is required")
    @Positive(message = "Volume must be positive")
    private Double volume;
    
    /**
     * Universal Product Code for the item.
     * Must be unique across all items.
     */
    @NotBlank(message = "UPC is required")
    private String upc;
    
    /**
     * Timestamp when the item was created.
     * Auto-generated on creation.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the item was last modified.
     * Auto-updated on modification.
     */
    private LocalDateTime updatedAt;
    
    /**
     * Creates a new item with auto-generated ID and timestamps.
     *
     * @param description the item description
     * @param weight the item weight in kg
     * @param volume the item volume in m³
     * @param upc the item's UPC code
     * @return a new Item instance
     */
    public static Item create(String description, Double weight, Double volume, String upc) {
        LocalDateTime now = LocalDateTime.now();
        return Item.builder()
                .id(UUID.randomUUID())
                .description(description)
                .weight(weight)
                .volume(volume)
                .upc(upc)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    /**
     * Updates the modification timestamp.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}