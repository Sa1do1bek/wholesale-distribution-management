package com.wholesale.service;

import com.wholesale.dto.InventoryDTO;
import com.wholesale.entity.Inventory;
import com.wholesale.entity.Product;
import com.wholesale.exception.ResourceNotFoundException;
import com.wholesale.repository.InventoryRepository;
import com.wholesale.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private Product product;
    private InventoryDTO inventoryDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSku("SKU123");

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProduct(product);
        inventory.setQuantity(50);
        inventory.setMinQuantity(10);
        inventory.setWarehouseLocation("Main Warehouse");

        inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(1L);
        inventoryDTO.setProductId(1L);
        inventoryDTO.setProductName("Test Product");
        inventoryDTO.setQuantity(50);
        inventoryDTO.setMinQuantity(10);
        inventoryDTO.setWarehouseLocation("Main Warehouse");
    }

    @Test
    void testGetAllInventory() {
        Page<Inventory> page = new PageImpl<>(List.of(inventory));
        when(inventoryRepository.findAll(any(Pageable.class))).thenReturn(page);

        var result = inventoryService.getAllInventory(0, 10, "id", "asc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getProductName());
    }

    @Test
    void testGetInventoryById_Success() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        InventoryDTO result = inventoryService.getInventoryById(1L);

        assertNotNull(result);
        assertEquals(50, result.getQuantity());
    }

    @Test
    void testGetInventoryById_NotFound() {
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryById(99L));
    }

    @Test
    void testUpdateStock_Success() {
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryDTO result = inventoryService.updateStock(1L, 10);

        assertNotNull(result);
        assertEquals(60, inventory.getQuantity()); // 50 + 10
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testUpdateStock_InsufficientStock() {
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        assertThrows(IllegalArgumentException.class, () -> inventoryService.updateStock(1L, -100));
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void testGetLowStockItems() {
        when(inventoryRepository.findLowStockItems()).thenReturn(List.of(inventory));

        List<InventoryDTO> result = inventoryService.getLowStockItems();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
