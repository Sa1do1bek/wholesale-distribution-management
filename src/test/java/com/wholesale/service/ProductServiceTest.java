package com.wholesale.service;

import com.wholesale.dto.ProductDTO;
import com.wholesale.entity.Inventory;
import com.wholesale.entity.Product;
import com.wholesale.exception.DuplicateResourceException;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("99.99"));
        product.setActive(true);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setSku("SKU123");
        productDTO.setCategory("Electronics");
        productDTO.setPrice(new BigDecimal("99.99"));
        productDTO.setStockQuantity(10);
    }

    @Test
    void testGetAllProducts() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        var result = productService.getAllProducts(0, 10, "id", "asc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getName());
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("SKU123", result.getSku());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void testCreateProduct_Success() {
        when(productRepository.existsBySku(productDTO.getSku())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(new Inventory());

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals("SKU123", result.getSku());
        verify(productRepository).save(any(Product.class));
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testCreateProduct_DuplicateSku() {
        when(productRepository.existsBySku(productDTO.getSku())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(productDTO));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productDTO.setName("Updated Product");
        ProductDTO result = productService.updateProduct(1L, productDTO);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(1L);

        assertFalse(product.isActive());
        verify(productRepository).save(product);
    }
}
