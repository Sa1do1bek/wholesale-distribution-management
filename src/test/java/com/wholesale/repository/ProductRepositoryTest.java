package com.wholesale.repository;

import com.wholesale.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindBySku() {
        Product product = Product.builder()
                .name("Test Product")
                .sku("SKU123")
                .category("Electronics")
                .price(new BigDecimal("99.99"))
                .build();
        productRepository.save(product);

        Optional<Product> found = productRepository.findBySku("SKU123");
        assertTrue(found.isPresent());
        assertEquals("Test Product", found.get().getName());
    }

    @Test
    void testFindAllCategories() {
        Product p1 = Product.builder().name("P1").sku("S1").category("Cat1").price(BigDecimal.ONE).active(true).build();
        Product p2 = Product.builder().name("P2").sku("S2").category("Cat2").price(BigDecimal.ONE).active(true).build();
        Product p3 = Product.builder().name("P3").sku("S3").category("Cat1").price(BigDecimal.ONE).active(true).build();
        productRepository.saveAll(List.of(p1, p2, p3));

        List<String> categories = productRepository.findAllCategories();
        assertEquals(2, categories.size());
        assertTrue(categories.contains("Cat1"));
        assertTrue(categories.contains("Cat2"));
    }

    @Test
    void testSearchProducts() {
        Product p = Product.builder().name("SuperPhone").sku("SP1").category("Phones").price(BigDecimal.TEN).build();
        productRepository.save(p);

        Page<Product> searchName = productRepository.searchProducts("super", PageRequest.of(0, 10));
        assertEquals(1, searchName.getTotalElements());

        Page<Product> searchCategory = productRepository.searchProducts("phone", PageRequest.of(0, 10));
        assertEquals(1, searchCategory.getTotalElements());
    }

    @Test
    void testFindByCategoryAndActiveTrue() {
        Product activeP = Product.builder().name("P1").sku("S1").category("Cat").price(BigDecimal.ONE).active(true).build();
        Product inactiveP = Product.builder().name("P2").sku("S2").category("Cat").price(BigDecimal.ONE).active(false).build();
        productRepository.saveAll(List.of(activeP, inactiveP));

        Page<Product> result = productRepository.findByCategoryAndActiveTrue("Cat", PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals("P1", result.getContent().get(0).getName());
    }
}
