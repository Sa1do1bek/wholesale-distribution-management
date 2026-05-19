package com.wholesale.dto;

import com.wholesale.entity.Inventory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    
    private Long id;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private String productName;
    private String productSku;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @NotNull(message = "Minimum quantity is required")
    @Min(value = 0, message = "Minimum quantity cannot be negative")
    private Integer minQuantity;
    
    private Integer maxQuantity;
    
    @Size(max = 100, message = "Warehouse location must not exceed 100 characters")
    private String warehouseLocation;
    
    @Size(max = 50, message = "Shelf number must not exceed 50 characters")
    private String shelfNumber;
    
    private LocalDateTime lastRestocked;
    private boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static InventoryDTO fromEntity(Inventory inventory) {
        return InventoryDTO.builder()
            .id(inventory.getId())
            .productId(inventory.getProduct().getId())
            .productName(inventory.getProduct().getName())
            .productSku(inventory.getProduct().getSku())
            .quantity(inventory.getQuantity())
            .minQuantity(inventory.getMinQuantity())
            .maxQuantity(inventory.getMaxQuantity())
            .warehouseLocation(inventory.getWarehouseLocation())
            .shelfNumber(inventory.getShelfNumber())
            .lastRestocked(inventory.getLastRestocked())
            .lowStock(inventory.isLowStock())
            .createdAt(inventory.getCreatedAt())
            .updatedAt(inventory.getUpdatedAt())
            .build();
    }
}
