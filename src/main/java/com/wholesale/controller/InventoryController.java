package com.wholesale.controller;

import com.wholesale.dto.InventoryDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @GetMapping
    public ResponseEntity<PageResponse<InventoryDTO>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(inventoryService.getAllInventory(page, size, sortBy, sortDir));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
    
    @GetMapping("/low-stock/paged")
    public ResponseEntity<PageResponse<InventoryDTO>> getLowStockItemsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(inventoryService.getLowStockItemsPaged(page, size));
    }
    
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryDTO>> getOutOfStockItems() {
        return ResponseEntity.ok(inventoryService.getOutOfStockItems());
    }
    
    @GetMapping("/warehouse/{location}")
    public ResponseEntity<PageResponse<InventoryDTO>> getInventoryByWarehouse(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouse(location, page, size));
    }
    
    @GetMapping("/warehouses")
    public ResponseEntity<List<String>> getAllWarehouseLocations() {
        return ResponseEntity.ok(inventoryService.getAllWarehouseLocations());
    }
    
    @GetMapping("/total-count")
    public ResponseEntity<Long> getTotalInventoryCount() {
        return ResponseEntity.ok(inventoryService.getTotalInventoryCount());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryDTO> createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) {
        InventoryDTO created = inventoryService.createInventory(inventoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryDTO));
    }
    
    @PatchMapping("/product/{productId}/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<InventoryDTO> adjustStock(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(productId, quantity));
    }
    
    @PatchMapping("/product/{productId}/restock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryDTO> restockProduct(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestParam(required = false) String warehouseLocation) {
        return ResponseEntity.ok(inventoryService.restockProduct(productId, quantity, warehouseLocation));
    }
}
