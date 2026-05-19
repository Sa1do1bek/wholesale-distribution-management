package com.wholesale.service;

import com.wholesale.dto.PageResponse;
import com.wholesale.dto.ProductDTO;
import com.wholesale.entity.Inventory;
import com.wholesale.entity.Product;
import com.wholesale.exception.DuplicateResourceException;
import com.wholesale.exception.ResourceNotFoundException;
import com.wholesale.repository.InventoryRepository;
import com.wholesale.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    
    public PageResponse<ProductDTO> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Product> productPage = productRepository.findAll(pageable);
        
        return buildPageResponse(productPage);
    }
    
    public PageResponse<ProductDTO> getActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findByActiveTrue(pageable);
        
        return buildPageResponse(productPage);
    }
    
    public PageResponse<ProductDTO> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findByCategoryAndActiveTrue(category, pageable);
        
        return buildPageResponse(productPage);
    }
    
    public PageResponse<ProductDTO> searchProducts(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.searchProducts(search, pageable);
        
        return buildPageResponse(productPage);
    }
    
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductDTO.fromEntity(product);
    }
    
    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return ProductDTO.fromEntity(product);
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new DuplicateResourceException("Product", "sku", productDTO.getSku());
        }
        
        Product product = productDTO.toEntity();
        product.setActive(true);
        product = productRepository.save(product);
        
        // Create initial inventory record
        Inventory inventory = Inventory.builder()
            .product(product)
            .quantity(productDTO.getStockQuantity() != null ? productDTO.getStockQuantity() : 0)
            .minQuantity(10)
            .build();
        inventoryRepository.save(inventory);
        
        product.setInventory(inventory);
        return ProductDTO.fromEntity(product);
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        if (!productDTO.getSku().equals(product.getSku())) {
            if (productRepository.existsBySku(productDTO.getSku())) {
                throw new DuplicateResourceException("Product", "sku", productDTO.getSku());
            }
        }
        
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setSku(productDTO.getSku());
        product.setPrice(productDTO.getPrice());
        product.setSize(productDTO.getSize());
        product.setColor(productDTO.getColor());
        
        product = productRepository.save(product);
        return ProductDTO.fromEntity(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.setActive(false);
        productRepository.save(product);
    }
    
    @Transactional
    public void permanentlyDeleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }
    
    private PageResponse<ProductDTO> buildPageResponse(Page<Product> page) {
        return PageResponse.<ProductDTO>builder()
            .content(page.getContent().stream().map(ProductDTO::fromEntity).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
