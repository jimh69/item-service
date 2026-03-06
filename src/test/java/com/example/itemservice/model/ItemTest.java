package com.example.itemservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ValidationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Autowired    
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Validation Tests

    @Test
    void descriptionNotBlankValidation() {
        Item item = Item.builder()
                .description("")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description") &&
                        v.getMessage().equals("Description is required")));
    }

    @Test
    void descriptionSizeValidation() {
        String longDescription = "A".repeat(256);
        Item item = Item.builder()
                .description(longDescription)
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description") &&
                        v.getMessage().equals("Description must not exceed 255 characters")));
    }

    @Test
    void weightNotNullValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(null)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("weight") &&
                        v.getMessage().equals("Weight is required")));
    }

    @Test
    void weightMinValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(0.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("weight") &&
                        v.getMessage().equals("Weight must be greater than 0")));
    }

    @Test
    void volumeNotNullValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(null)
                .upc("A1B2C3D4E5F6")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("volume") &&
                        v.getMessage().equals("Volume is required")));
    }

    @Test
    void volumeMinValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(0.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("volume") &&
                        v.getMessage().equals("Volume must be greater than 0")));
    }

    @Test
    void upcNotBlankValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("upc") &&
                        v.getMessage().equals("UPC is required")));
    }

    @Test
    void upcPatternValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("INVALID-UPC")
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("upc") &&
                        v.getMessage().equals("UPC must be 12 characters alphanumeric")));
    }

    @Test
    void quantityMinValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .quantity(-1)
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity") &&
                        v.getMessage().equals("Quantity must be zero or greater")));
    }

    @Test
    void costMinValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .cost(new BigDecimal("-1.00"))
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cost") &&
                        v.getMessage().equals("Cost must be zero or greater")));
    }

    @Test
    void priceMinValidation() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .price(new BigDecimal("-1.00"))
                .build();

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("price") &&
                        v.getMessage().equals("Price must be zero or greater")));
    }

    // Builder Pattern Tests

    @Test
    void builderCreatesValidItem() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .quantity(10)
                .cost(new BigDecimal("9.99"))
                .price(new BigDecimal("19.99"))
                .build();

        assertNotNull(item);
        assertEquals("Test Item", item.getDescription());
        assertEquals(1.0, item.getWeight());
        assertEquals(1.0, item.getVolume());
        assertEquals("A1B2C3D4E5F6", item.getUpc());
        assertEquals(Integer.valueOf(10), item.getQuantity());
        assertEquals(new BigDecimal("9.99"), item.getCost());
        assertEquals(new BigDecimal("19.99"), item.getPrice());
        assertNull(item.getId()); // Builder doesn't set ID automatically
        assertNull(item.getCreatedAt()); // set at the DB level, null by default
        assertNull(item.getUpdatedAt()); // set at the DB level, null by default
    }

    @Test
    void builderWithMinimalFields() {
        Item item = Item.builder()
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        assertNotNull(item);
        assertEquals("Test Item", item.getDescription());
        assertEquals(1.0, item.getWeight());
        assertEquals(1.0, item.getVolume());
        assertEquals("A1B2C3D4E5F6", item.getUpc());
        assertNull(item.getQuantity());
        assertNull(item.getCost());
        assertNull(item.getPrice());
        assertNull(item.getId()); // Builder doesn't set ID automatically
        assertNull(item.getCreatedAt()); // set at the DB level, null by default
        assertNull(item.getUpdatedAt()); // set at the DB level, null by default
    }

    // Factory Method Tests

    @Test
    void factoryMethodCreatesValidItem() {
        Item item = Item.create("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");

        assertNotNull(item);
        assertEquals("Test Item", item.getDescription());
        assertEquals(1.0, item.getWeight());
        assertEquals(1.0, item.getVolume());
        assertEquals("A1B2C3D4E5F6", item.getUpc());
        assertNotNull(item.getId());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
        assertEquals(item.getCreatedAt(), item.getUpdatedAt());
    }

@Test
void factoryMethodWithNullValues() {
    // Factory method doesn't throw NullPointerException for null values
    // It will fail validation instead
    Item item = Item.create(null, 1.0, 1.0, "A1B2C3D4E5F6");
    Set<ConstraintViolation<Item>> violations = validator.validate(item);
    assertFalse(violations.isEmpty());

    item = Item.create("Test Item", null, 1.0, "A1B2C3D4E5F6");
    violations = validator.validate(item);
    assertFalse(violations.isEmpty());

    item = Item.create("Test Item", 1.0, null, "A1B2C3D4E5F6");
    violations = validator.validate(item);
    assertFalse(violations.isEmpty());
}

    // Timestamp Tests

    @Test
    void touchUpdatesUpdatedAt() {
        Item item = Item.create("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");
        LocalDateTime originalUpdatedAt = item.getUpdatedAt();

        // Add a small delay to ensure timestamp changes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        item.touch();

        assertNotEquals(originalUpdatedAt, item.getUpdatedAt());
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    void touchDoesNotChangeCreatedAt() {
        Item item = Item.create("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");
        LocalDateTime originalCreatedAt = item.getCreatedAt();

        item.touch();

        assertEquals(originalCreatedAt, item.getCreatedAt());
    }

    @Test
    void touchUpdatesTimestampToCurrentTime() {
        Item item = Item.create("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");
        LocalDateTime beforeTouch = LocalDateTime.now();

        item.touch();

        LocalDateTime afterTouch = LocalDateTime.now();
        assertTrue(item.getUpdatedAt().isAfter(beforeTouch) || item.getUpdatedAt().isEqual(beforeTouch));
        assertTrue(item.getUpdatedAt().isBefore(afterTouch) || item.getUpdatedAt().isEqual(afterTouch));
    }
}