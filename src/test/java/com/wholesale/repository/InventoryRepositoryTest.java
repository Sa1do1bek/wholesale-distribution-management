package com.wholesale.repository;

import com.wholesale.entity.Inventory;
import com.wholesale.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product createProduct(String sku) {
        Product p = Product.builder()
                .name("Prod " + sku)
                .sku(sku)
                .category("Cat")
                .price(BigDecimal.ONE)
                .build();
        return productRepository.save(p);
    }

    @Test
    void testFindLowStockItems() {
        Product p1 = createProduct("S1");
        Inventory i1 = Inventory.builder().product(p1).quantity(5).minQuantity(10).build();
        inventoryRepository.save(i1);

        Product p2 = createProduct("S2");
        Inventory i2 = Inventory.builder().product(p2).quantity(15).minQuantity(10).build();
        inventoryRepository.save(i2);

        List<Inventory> lowStock = inventoryRepository.findLowStockItems();
        assertEquals(1, lowStock.size());
        assertEquals("S1", lowStock.get(0).getProduct().getSku());
    }

    @Test
    void testFindAllWarehouseLocations() {
        Product p1 = createProduct("S1");
        inventoryRepository.save(Inventory.builder().product(p1).quantity(10).warehouseLocation("W1").build());

        Product p2 = createProduct("S2");
        inventoryRepository.save(Inventory.builder().product(p2).quantity(10).warehouseLocation("W2").build());

        Product p3 = createProduct("S3");
        inventoryRepository.save(Inventory.builder().product(p3).quantity(10).warehouseLocation("W1").build());

        List<String> locations = inventoryRepository.findAllWarehouseLocations();
        assertEquals(2, locations.size());
        assertTrue(locations.containsAll(List.of("W1", "W2")));
    }

    @Test
    void testGetTotalInventoryCount() {
        Product p1 = createProduct("S1");
        inventoryRepository.save(Inventory.builder().product(p1).quantity(10).build());

        Product p2 = createProduct("S2");
        inventoryRepository.save(Inventory.builder().product(p2).quantity(25).build());

        Long total = inventoryRepository.getTotalInventoryCount();
        assertEquals(35L, total);
    }

    @Test
    void testFindOutOfStockItems() {
        Product p1 = createProduct("S1");
        inventoryRepository.save(Inventory.builder().product(p1).quantity(0).build());

        Product p2 = createProduct("S2");
        inventoryRepository.save(Inventory.builder().product(p2).quantity(1).build());

        List<Inventory> outOfStock = inventoryRepository.findOutOfStockItems();
        assertEquals(1, outOfStock.size());
        assertEquals("S1", outOfStock.get(0).getProduct().getSku());
    }
}
