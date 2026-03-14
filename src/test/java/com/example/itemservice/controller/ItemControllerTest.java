package com.example.itemservice.controller;

// Spring Boot Test Imports
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

// JSON and Web Imports
import org.springframework.http.MediaType;

// Static Imports for MockMvc (Essential for the fluent API)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.example.itemservice.model.Item;
import com.example.itemservice.service.ItemService;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest(value = ItemController.class, properties = "spring.cloud.config.enabled=false")
class ItemControllerTest {
@Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ItemService itemService;

  @Test
  void getAllItems_ShouldReturnAllItems() throws Exception {
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

    when(itemService.getAllItems()).thenReturn(Arrays.asList(item1, item2));

    mockMvc.perform(get("/api/v1/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].description").value("Item 1"))
        .andExpect(jsonPath("$[1].description").value("Item 2"));

    verify(itemService).getAllItems();
  }

  @Test
  void getAllItems_ShouldReturnEmptyList() throws Exception {
    when(itemService.getAllItems()).thenReturn(Arrays.asList());

    mockMvc.perform(get("/api/v1/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    verify(itemService).getAllItems();
  }

  // GET /api/v1/items/{id} Tests

  @Test
  void getItemById_ShouldReturnItemWhenFound() throws Exception {
    UUID itemId = UUID.randomUUID();
    Item testItem = Item.builder()
        .id(itemId)
        .description("Test Item")
        .weight(1.0)
        .volume(1.0)
        .upc("A1B2C3D4E5F6")
        .build();

    when(itemService.getItemById(itemId)).thenReturn(Optional.of(testItem));

    mockMvc.perform(get("/api/v1/items/{id}", itemId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Test Item"))
        .andExpect(jsonPath("$.weight").value(1.0))
        .andExpect(jsonPath("$.volume").value(1.0))
        .andExpect(jsonPath("$.upc").value("A1B2C3D4E5F6"));

    verify(itemService).getItemById(itemId);
  }

  @Test
  void getItemById_ShouldReturnNotFoundWhenNotFound() throws Exception {
    UUID itemId = UUID.randomUUID();

    when(itemService.getItemById(itemId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/items/{id}", itemId))
        .andExpect(status().isNotFound());

    verify(itemService).getItemById(itemId);
  }

  // GET /api/v1/items/upc/{upc} Tests

  @Test
  void getItemByUpc_ShouldReturnItemWhenFound() throws Exception {
    Item testItem = Item.builder()
        .id(UUID.randomUUID())
        .description("Test Item")
        .weight(1.0)
        .volume(1.0)
        .upc("A1B2C3D4E5F6")
        .build();

    when(itemService.getItemByUpc("A1B2C3D4E5F6")).thenReturn(Optional.of(testItem));

    mockMvc.perform(get("/api/v1/items/upc/{upc}", "A1B2C3D4E5F6"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Test Item"))
        .andExpect(jsonPath("$.weight").value(1.0))
        .andExpect(jsonPath("$.volume").value(1.0))
        .andExpect(jsonPath("$.upc").value("A1B2C3D4E5F6"));

    verify(itemService).getItemByUpc("A1B2C3D4E5F6");
  }

  @Test
  void getItemByUpc_ShouldReturnNotFoundWhenNotFound() throws Exception {
    when(itemService.getItemByUpc("NONEXISTENT")).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/items/upc/{upc}", "NONEXISTENT"))
        .andExpect(status().isNotFound());

    verify(itemService).getItemByUpc("NONEXISTENT");
  }

  //  POST /api/v1/items Tests

@Test
  void createItem_ShouldCreateNewItem() throws Exception {
    Item createdItem = Item.builder()
        .id(UUID.randomUUID())
        .description("Test Item")
        .weight(1.0)
        .volume(1.0)
        .upc("A1B2C3D4E5F6")
        .build();

    when(itemService.createItem("Test Item", 1.0, 1.0, "A1B2C3D4E5F6"))
        .thenReturn(createdItem);

    mockMvc.perform(post("/api/v1/items")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"description\":\"Test Item\",\"weight\":1.0,\"volume\":1.0,\"upc\":\"A1B2C3D4E5F6\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.description").value("Test Item"))
        .andExpect(jsonPath("$.weight").value(1.0))
        .andExpect(jsonPath("$.volume").value(1.0))
        .andExpect(jsonPath("$.upc").value("A1B2C3D4E5F6"));

    verify(itemService).createItem("Test Item", 1.0, 1.0, "A1B2C3D4E5F6");
  }

  @Test
  void createItem_ShouldReturnBadRequestForValidationFailure() throws Exception {
    // Logic: Construct an item that violates multiple constraints (NotBlank, DecimalMin)
    // Logic: perform() simulates the POST request; the @Valid annotation now triggers a 400 Bad Request
    mockMvc.perform(post("/api/v1/items")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"description\":\"\",\"weight\":-1.0,\"volume\":0.0,\"upc\":\"\"}"))
        .andExpect(status().isBadRequest());

    // Logic: Verify that the service was never called because validation stopped the request
    verify(itemService, never()).createItem(any(), any(), any(), any());
  }

  // // PUT /api/v1/items/{id} Tests

  @Test
  void updateItem_ShouldUpdateExistingItem() throws Exception {
    UUID itemId = UUID.randomUUID();
    Item updatedItem = Item.builder()
        .id(itemId)
        .description("Updated Item")
        .weight(2.0)
        .volume(2.0)
        .upc("A1B2C3D4E5F6")
        .build();

    when(itemService.updateItem(itemId, "Updated Item", 2.0, 2.0, "A1B2C3D4E5F6", null, null, null))
        .thenReturn(updatedItem);

    mockMvc.perform(put("/api/v1/items/{id}", itemId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"description\":\"Updated Item\",\"weight\":2.0,\"volume\":2.0,\"upc\":\"A1B2C3D4E5F6\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Updated Item"))
        .andExpect(jsonPath("$.weight").value(2.0))
        .andExpect(jsonPath("$.volume").value(2.0))
        .andExpect(jsonPath("$.upc").value("A1B2C3D4E5F6"));

    verify(itemService).updateItem(itemId, "Updated Item", 2.0, 2.0, "A1B2C3D4E5F6", null, null, null);
  }

  @Test
  void updateItem_ShouldReturnNotFoundWhenItemNotFound() throws Exception {
    UUID itemId = UUID.randomUUID();

    when(itemService.updateItem(itemId, "Updated Item", 2.0, 2.0, "A1B2C3D4E5F6", null, null, null))
        .thenThrow(new IllegalArgumentException("Item with ID " + itemId + " not found"));

    mockMvc.perform(put("/api/v1/items/{id}", itemId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"description\":\"Updated Item\",\"weight\":2.0,\"volume\":2.0,\"upc\":\"A1B2C3D4E5F6\"}"))
        .andExpect(status().isNotFound());

    verify(itemService).updateItem(itemId, "Updated Item", 2.0, 2.0, "A1B2C3D4E5F6", null, null, null);
  }

  @Test
  void updateItem_ShouldReturnBadRequestForValidationFailure() throws Exception {
    UUID itemId = UUID.randomUUID();
    mockMvc.perform(put("/api/v1/items/{id}", itemId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"description\":\"\",\"weight\":-1.0,\"volume\":0.0,\"upc\":\"\"}"))
        .andExpect(status().isBadRequest());

    verify(itemService, never()).updateItem(any(), any(), any(), any(), any(), any(), any(), any());
  }

  // DELETE /api/v1/items/{id} Tests

  @Test
  void deleteItem_ShouldDeleteItem() throws Exception {
    UUID itemId = UUID.randomUUID();

    doNothing().when(itemService).deleteItem(itemId);

    mockMvc.perform(delete("/api/v1/items/{id}", itemId))
        .andExpect(status().isNoContent());

    verify(itemService).deleteItem(itemId);
  }

  @Test
  void deleteItem_ShouldReturnNotFoundWhenItemNotFound() throws Exception {
    UUID itemId = UUID.randomUUID();

    doThrow(new IllegalArgumentException("Item not found")).when(itemService).deleteItem(itemId);

    mockMvc.perform(delete("/api/v1/items/{id}", itemId))
        .andExpect(status().isNotFound());

    verify(itemService).deleteItem(itemId);
  }

  // GET /api/v1/items/search Tests

  @Test
  void searchItems_ShouldReturnMatchingItems() throws Exception {
    Item item1 = Item.builder()
        .id(UUID.randomUUID())
        .description("Test Item 1")
        .weight(1.0)
        .volume(1.0)
        .upc("A1B2C3D4E5F6")
        .build();

    Item item2 = Item.builder()
        .id(UUID.randomUUID())
        .description("Test Item 2")
        .weight(2.0)
        .volume(2.0)
        .upc("B2C3D4E5F6G7")
        .build();

    when(itemService.searchItems("test")).thenReturn(Arrays.asList(item1, item2));

    mockMvc.perform(get("/api/v1/items/search")
        .param("description", "test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].description").value("Test Item 1"))
        .andExpect(jsonPath("$[1].description").value("Test Item 2"));

    verify(itemService).searchItems("test");
  }

  @Test
  void searchItems_ShouldReturnEmptyListWhenNoMatches() throws Exception {
    when(itemService.searchItems("test")).thenReturn(Arrays.asList());

    mockMvc.perform(get("/api/v1/items/search")
        .param("description", "test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    verify(itemService).searchItems("test");
  }
}