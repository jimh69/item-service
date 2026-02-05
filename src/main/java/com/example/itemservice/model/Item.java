package com.example.itemservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an item in the inventory system.
 * 
 * <p>Each item has a unique identifier, description, weight, volume, and UPC code.
 * This class serves as both a JPA entity and DTO, using Lombok annotations to 
 * reduce boilerplate code for getters, setters, constructors, and builder pattern.</p>
 * 
 * @author Item Service
 * @version 1.0
 */
@Entity
@Table(name = "item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    /**
     * Unique identifier for the item.
     * Auto-generated using UUID.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    /**
     * Human-readable description of the item.
     * Cannot be null or empty.
     */
    @Column(nullable = false)
    @NotBlank(message = "Description is required")
    private String description;
    
    /**
     * Weight of the item in kilograms.
     * Must be positive.
     */
    @Column(nullable = false)
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    /**
     * Volume of the item in cubic meters.
     * Must be positive.
     */
    @Column(nullable = false)
    @NotNull(message = "Volume is required")
    @Positive(message = "Volume must be positive")
    private Double volume;
    
    /**
     * Universal Product Code for the item.
     * Must be unique across all items.
     */
    @Column(unique = true, nullable = false, length = 255)
    @NotBlank(message = "UPC is required")
    private String upc;
    
    /**
     * Timestamp when the item was created.
     * Auto-generated on creation.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the item was last modified.
     * Auto-updated on modification.
     */
    @UpdateTimestamp
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