package com.example.itemservice.repository;

import com.example.itemservice.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;   
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;   
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
    "spring.cloud.config.enabled=false",     // Disables the Config Client
    "spring.cloud.bootstrap.enabled=false",  // Disables the Bootstrap context phase
    "spring.config.import=optional:configserver:", // Forces the import to be optional if present
    "spring.jpa.hibernate.ddl-auto=create-drop", // Logic: Force schema creation for the test database
    "spring.jpa.show-sql=true"                   // Helpful for debugging schema issues    
})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = Item.create("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");
        entityManager.persist(testItem);
        entityManager.flush();
    }

    @Test
    void saveItem_ShouldPersistItem() {
        Item newItem = Item.create("saveItem", 1.0, 1.0, "saveItem999");        
        Item savedItem = itemRepository.save(newItem);
        assertNotNull(savedItem.getId());
        assertEquals("saveItem", savedItem.getDescription());
        assertEquals(1.0, savedItem.getWeight());
        assertEquals(1.0, savedItem.getVolume());
        assertEquals("saveItem999", savedItem.getUpc());        
    }

    @Test
    void findById_ShouldReturnItem() {
        Optional<Item> foundItem = itemRepository.findById(testItem.getId());
        assertTrue(foundItem.isPresent());
        assertEquals(testItem.getId(), foundItem.get().getId());
        assertEquals("Test Item", foundItem.get().getDescription());
    }

   @Test
    void findById_ShouldReturnEmptyForNonExistentId() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Item> foundItem = itemRepository.findById(nonExistentId);
        assertFalse(foundItem.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllItems() {
        Item secondItem = Item.create("Second Item", 2.0, 2.0, "B2C3D4E5F6G7");
        entityManager.persist(secondItem);
        entityManager.flush();

        List<Item> allItems = itemRepository.findAll();
        assertThat(allItems).hasSize(2);
    }

    @Test
    void count_ShouldReturnItemCount() {
        long count = itemRepository.count();
        assertEquals(1, count);
    }

    @Test
    void deleteById_ShouldRemoveItem() {
        itemRepository.deleteById(testItem.getId());
        Optional<Item> foundItem = itemRepository.findById(testItem.getId());
        assertFalse(foundItem.isPresent());
    }

    @Test
    void existsById_ShouldReturnTrueForExistingItem() {
        boolean exists = itemRepository.existsById(testItem.getId());
        assertTrue(exists);
    }

    @Test
    void existsById_ShouldReturnFalseForNonExistentItem() {
        UUID nonExistentId = UUID.randomUUID();
        boolean exists = itemRepository.existsById(nonExistentId);
        assertFalse(exists);
    }

    @Test
    void findByUpc_ShouldReturnItemWithMatchingUpc() {
        Optional<Item> foundItem = itemRepository.findByUpc("A1B2C3D4E5F6");
        assertTrue(foundItem.isPresent());
        assertEquals(testItem.getId(), foundItem.get().getId());
    }

    @Test
    void findByUpc_ShouldReturnEmptyForNonExistentUpc() {
        Optional<Item> foundItem = itemRepository.findByUpc("NONEXISTENTUPC");
        assertFalse(foundItem.isPresent());
    }

    @Test
    void existsByUpc_ShouldReturnTrueForExistingUpc() {
        boolean exists = itemRepository.existsByUpc("A1B2C3D4E5F6");
        assertTrue(exists);
    }

    @Test
    void existsByUpc_ShouldReturnFalseForNonExistentUpc() {
        boolean exists = itemRepository.existsByUpc("NONEXISTENTUPC");
        assertFalse(exists);
    }       
}