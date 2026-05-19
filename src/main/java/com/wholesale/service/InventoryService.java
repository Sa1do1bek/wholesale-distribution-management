package com.wholesale.service;

import com.wholesale.dto.InventoryDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.entity.Inventory;
import com.wholesale.entity.Product;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    
    public PageResponse<InventoryDTO> getAllInventory(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
        
        return buildPageResponse(inventoryPage);
    }
    
    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
        return InventoryDTO.fromEntity(inventory);
    }
    
    public InventoryDTO getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        return InventoryDTO.fromEntity(inventory);
    }
    
    public List<InventoryDTO> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
            .map(InventoryDTO::fromEntity)
            .toList();
    }
    
    public PageResponse<InventoryDTO> getLowStockItemsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryRepository.findLowStockItems(pageable);
        return buildPageResponse(inventoryPage);
    }
    
    public List<InventoryDTO> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems().stream()
            .map(InventoryDTO::fromEntity)
            .toList();
    }
    
    public PageResponse<InventoryDTO> getInventoryByWarehouse(String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryRepository.findByWarehouseLocation(location, pageable);
        return buildPageResponse(inventoryPage);
    }
    
    public List<String> getAllWarehouseLocations() {
        return inventoryRepository.findAllWarehouseLocations();
    }
    
    @Transactional
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) {
        Product product = productRepository.findById(inventoryDTO.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", inventoryDTO.getProductId()));
        
        if (inventoryRepository.findByProductId(inventoryDTO.getProductId()).isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for this product");
        }
        
        Inventory inventory = Inventory.builder()
            .product(product)
            .quantity(inventoryDTO.getQuantity())
            .minQuantity(inventoryDTO.getMinQuantity())
            .maxQuantity(inventoryDTO.getMaxQuantity())
            .warehouseLocation(inventoryDTO.getWarehouseLocation())
            .shelfNumber(inventoryDTO.getShelfNumber())
            .build();
        
        inventory = inventoryRepository.save(inventory);
        return InventoryDTO.fromEntity(inventory);
    }
    
    @Transactional
    public InventoryDTO updateInventory(Long id, InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
        
        inventory.setQuantity(inventoryDTO.getQuantity());
        inventory.setMinQuantity(inventoryDTO.getMinQuantity());
        inventory.setMaxQuantity(inventoryDTO.getMaxQuantity());
        inventory.setWarehouseLocation(inventoryDTO.getWarehouseLocation());
        inventory.setShelfNumber(inventoryDTO.getShelfNumber());
        
        inventory = inventoryRepository.save(inventory);
        return InventoryDTO.fromEntity(inventory);
    }
    
    @Transactional
    public InventoryDTO updateStock(Long productId, int quantityChange) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        
        int newQuantity = inventory.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + inventory.getQuantity());
        }
        
        inventory.setQuantity(newQuantity);
        if (quantityChange > 0) {
            inventory.setLastRestocked(LocalDateTime.now());
        }
        
        inventory = inventoryRepository.save(inventory);
        return InventoryDTO.fromEntity(inventory);
    }
    
    @Transactional
    public InventoryDTO restockProduct(Long productId, int quantity, String warehouseLocation) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setLastRestocked(LocalDateTime.now());
        if (warehouseLocation != null) {
            inventory.setWarehouseLocation(warehouseLocation);
        }
        
        inventory = inventoryRepository.save(inventory);
        return InventoryDTO.fromEntity(inventory);
    }
    
    public Long getTotalInventoryCount() {
        Long count = inventoryRepository.getTotalInventoryCount();
        return count != null ? count : 0L;
    }
    
    private PageResponse<InventoryDTO> buildPageResponse(Page<Inventory> page) {
        return PageResponse.<InventoryDTO>builder()
            .content(page.getContent().stream().map(InventoryDTO::fromEntity).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
