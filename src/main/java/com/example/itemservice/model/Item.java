package com.example.itemservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an item in the inventory system.
 * 
 * <p>This class models an item with properties such as description,
 * weight, volume, and UPC code. It includes validation constraints
 * to ensure data integrity.</p>
 * 
 * <p>Uses Lombok annotations to reduce boilerplate code for getters,
 * setters, constructors, and builder pattern.</p>
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(name = "description", nullable = false, length = 255)
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    @Column(name = "weight", nullable = false)
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be greater than 0")
    private Double weight;
    
    @Column(name = "volume", nullable = false)
    @NotNull(message = "Volume is required")
    @DecimalMin(value = "0.001", message = "Volume must be greater than 0")
    private Double volume;
    
    @Column(name = "upc", nullable = false, unique = true, length = 255)
    @NotBlank(message = "UPC is required")
    @Pattern(regexp = "^[A-Z0-9]{12}$", message = "UPC must be 12 characters alphanumeric")
    private String upc;
    
    @Column(name = "quantity")
    @Min(value = 0, message = "Quantity must be zero or greater")
    private Integer quantity;

    @Column(name = "cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Cost must be zero or greater")
    private BigDecimal cost;

    @Column(name = "price", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Price must be zero or greater")
    private BigDecimal price;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /**
     * Creates a new item with the specified properties.
     * Auto-generates ID and timestamps.
     *
     * @param description the item description
     * @param weight the item weight in kilograms
     * @param volume the item volume in cubic meters
     * @param upc the item's UPC code
     * @return a new Item instance
     */
    public static Item create(String description, Double weight, Double volume, String upc) {
        return Item.builder()
                //.id(UUID.randomUUID())
                .description(description)
                .weight(weight)
                .volume(volume)
                .upc(upc)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Updates the item's timestamps to the current time.
     * Should be called whenever the item is modified.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}