package com.example.itemservice.service;

import com.example.itemservice.model.Item;
import com.example.itemservice.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.spi.LocationAwareLogger;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    private ItemService itemService;

    @Captor
    private ArgumentCaptor<String> logMessageCaptor;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository);
    }

    // createItem() Tests

    @Test
    void createItem_ShouldCreateNewItem() {
        UUID itemId = UUID.randomUUID();
        Item savedItem = Item.builder()
                .id(itemId)
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.existsByUpc("A1B2C3D4E5F6")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.createItem("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("Test Item", result.getDescription());
        assertEquals(1.0, result.getWeight());
        assertEquals(1.0, result.getVolume());
        assertEquals("A1B2C3D4E5F6", result.getUpc());

        verify(itemRepository).existsByUpc("A1B2C3D4E5F6");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowExceptionForDuplicateUpc() {
        when(itemRepository.existsByUpc("A1B2C3D4E5F6")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> itemService.createItem("Test Item", 1.0, 1.0, "A1B2C3D4E5F6")
        );

        assertEquals("An item with UPC 'A1B2C3D4E5F6' already exists", exception.getMessage());

        verify(itemRepository).existsByUpc("A1B2C3D4E5F6");
        verify(itemRepository, never()).save(any(Item.class));
    }

    // getItemById() Tests

    @Test
    void getItemById_ShouldReturnItemWhenFound() {
        UUID itemId = UUID.randomUUID();
        Item testItem = Item.builder()
                .id(itemId)
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

        Optional<Item> result = itemService.getItemById(itemId);

        assertTrue(result.isPresent());
        assertEquals(itemId, result.get().getId());
        assertEquals("Test Item", result.get().getDescription());

        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemById_ShouldReturnEmptyWhenNotFound() {
        UUID itemId = UUID.randomUUID();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        Optional<Item> result = itemService.getItemById(itemId);

        assertFalse(result.isPresent());

        verify(itemRepository).findById(itemId);
    }

    // getItemByUpc() Tests

    @Test
    void getItemByUpc_ShouldReturnItemWhenFound() {
        Item testItem = Item.builder()
                .id(UUID.randomUUID())
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.findByUpc("A1B2C3D4E5F6")).thenReturn(Optional.of(testItem));

        Optional<Item> result = itemService.getItemByUpc("A1B2C3D4E5F6");

        assertTrue(result.isPresent());
        assertEquals("A1B2C3D4E5F6", result.get().getUpc());

        verify(itemRepository).findByUpc("A1B2C3D4E5F6");
    }

    @Test
    void getItemByUpc_ShouldReturnEmptyWhenNotFound() {
        when(itemRepository.findByUpc("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Item> result = itemService.getItemByUpc("NONEXISTENT");

        assertFalse(result.isPresent());

        verify(itemRepository).findByUpc("NONEXISTENT");
    }

    // getAllItems() Tests

    @Test
    void getAllItems_ShouldReturnEmptyListWhenNoItems() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList());

        List<Item> result = itemService.getAllItems();

        assertTrue(result.isEmpty());

        verify(itemRepository).findAll();
    }

    @Test
    void getAllItems_ShouldReturnAllItems() {
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .description("Item 1")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Item item2 = Item.builder()
                .id(UUID.randomUUID())
                .description("Item 2")
                .weight(2.0)
                .volume(2.0)
                .upc("B2C3D4E5F6G7")
                .build();

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<Item> result = itemService.getAllItems();

        assertEquals(2, result.size());
        assertTrue(result.contains(item1));
        assertTrue(result.contains(item2));

        verify(itemRepository).findAll();
    }

    // updateItem() Tests

    @Test
    void updateItem_ShouldUpdateExistingItem() {
        UUID itemId = UUID.randomUUID();
        Item existingItem = Item.builder()
                .id(itemId)
                .description("Old Description")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Item updatedItem = Item.builder()
                .id(itemId)
                .description("New Description")
                .weight(2.0)
                .volume(2.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        Item result = itemService.updateItem(
                itemId,
                "New Description",
                2.0,
                2.0,
                "A1B2C3D4E5F6",
                null,
                null,
                null
        );

        assertNotNull(result);
        assertEquals("New Description", result.getDescription());
        assertEquals(2.0, result.getWeight());
        assertEquals(2.0, result.getVolume());

        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowExceptionWhenItemNotFound() {
        UUID itemId = UUID.randomUUID();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> itemService.updateItem(
                        itemId,
                        "New Description",
                        2.0,
                        2.0,
                        "A1B2C3D4E5F6",
                        null,
                        null,
                        null
                )
        );

        assertEquals("Item with ID " + itemId + " not found", exception.getMessage());

        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowExceptionForUpcConflict() {
        UUID itemId = UUID.randomUUID();
        Item existingItem = Item.builder()
                .id(itemId)
                .description("Old Description")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.existsByUpc("B2C3D4E5F6G7")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> itemService.updateItem(
                        itemId,
                        "New Description",
                        2.0,
                        2.0,
                        "B2C3D4E5F6G7",
                        null,
                        null,
                        null
                )
        );

        assertEquals("An item with UPC 'B2C3D4E5F6G7' already exists", exception.getMessage());

        verify(itemRepository).findById(itemId);
        verify(itemRepository).existsByUpc("B2C3D4E5F6G7");
        verify(itemRepository, never()).save(any(Item.class));
    }

    // deleteItem() Tests

    @Test
    void deleteItem_ShouldReturnTrue() {
        UUID itemId = UUID.randomUUID();

        boolean result = itemService.deleteItem(itemId);

        assertTrue(result);

        verify(itemRepository).deleteById(itemId);
    }

    // Logging Tests

    @Test
    void createItem_ShouldLogInfoMessageForSuccess() {
        Item savedItem = Item.builder()
                .id(UUID.randomUUID())
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.existsByUpc("A1B2C3D4E5F6")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        itemService.createItem("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");

        // Verify INFO log for creation
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldLogWarnMessageForDuplicateUpc() {
        when(itemRepository.existsByUpc("A1B2C3D4E5F6")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> itemService.createItem("Test Item", 1.0, 1.0, "A1B2C3D4E5F6")
        );

        // Verify WARN log for duplicate UPC
        verify(itemRepository).existsByUpc("A1B2C3D4E5F6");
    }

    @Test
    void getItemById_ShouldLogDebugMessageForRetrieval() {
        UUID itemId = UUID.randomUUID();
        Item testItem = Item.builder()
                .id(itemId)
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

        itemService.getItemById(itemId);

        // Verify DEBUG log for retrieval
        verify(itemRepository).findById(itemId);
    }

    @Test
    void updateItem_ShouldLogInfoMessageForUpdate() {
        UUID itemId = UUID.randomUUID();
        Item existingItem = Item.builder()
                .id(itemId)
                .description("Old Description")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Item updatedItem = Item.builder()
                .id(itemId)
                .description("New Description")
                .weight(2.0)
                .volume(2.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.updateItem(
                itemId,
                "New Description",
                2.0,
                2.0,
                "A1B2C3D4E5F6",
                null,
                null,
                null
        );

        // Verify INFO log for update
        verify(itemRepository).save(any(Item.class));
    }

    // @Transactional Tests

    @Test
    void createItem_ShouldCallRepositoryMethodsWithinTransaction() {
        Item savedItem = Item.builder()
                .id(UUID.randomUUID())
                .description("Test Item")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.existsByUpc("A1B2C3D4E5F6")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        itemService.createItem("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");

        // Verify repository methods are called
        verify(itemRepository).existsByUpc("A1B2C3D4E5F6");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldCallRepositoryMethodsWithinTransaction() {
        UUID itemId = UUID.randomUUID();
        Item existingItem = Item.builder()
                .id(itemId)
                .description("Old Description")
                .weight(1.0)
                .volume(1.0)
                .upc("A1B2C3D4E5F6")
                .build();

        Item updatedItem = Item.builder()
                .id(itemId)
                .description("New Description")
                .weight(2.0)
                .volume(2.0)
                .upc("A1B2C3D4E5F6")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.updateItem(
                itemId,
                "New Description",
                2.0,
                2.0,
                "A1B2C3D4E5F6",
                null,
                null,
                null
        );

        // Verify repository methods are called
        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(any(Item.class));
    }
}